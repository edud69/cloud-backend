

package io.theshire.common.service.infrastructure.impl.indexation;

import io.theshire.common.service.infrastructure.indexation.IndexationProcessor.IndexationProcessState;
import io.theshire.common.service.infrastructure.indexation.IndexationTenantAwareService;
import io.theshire.common.service.infrastructure.indexation.IndexationTenantResolver;
import io.theshire.common.utils.jpa.constants.JpaPersistenceUnitConstants;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;


@Service
@Transactional


@Slf4j
public class IndexationTenantAwareServiceImpl extends IndexationServiceAbstract
    implements
      IndexationTenantAwareService {

 
  @PersistenceContext(unitName = JpaPersistenceUnitConstants.MULTI_TENANT_PERSISTENCE_CONTEXT)
  private EntityManager em;

  @Autowired
  private IndexationTenantResolver tenantResolver;

 
  @Override
  protected void onIndexationStateChanged(final IndexationStateMessage incoming) {
    if (incoming.getTenantId() == null) {
      return; // reject msg that are not tenant aware
    }

    final String indexingType = incoming.getIndexingType();
    final boolean removeEntry = !incoming.getState().equals(IndexationProcessState.INDEXING);

    if (indexingType == null) {
      // Index all type query, specific type will happen in upcoming messages
      if (removeEntry) {
        this.indexationProcessStates.remove(incoming.getTenantId());
      } else {
        this.indexationProcessStates.putIfAbsent(incoming.getTenantId(), incoming.getState());
      }
    } else {
      if (removeEntry) {
        this.indexationProcessStates.remove(incoming.getTenantId() + "$" + indexingType);
      } else {
        this.indexationProcessStates.putIfAbsent(incoming.getTenantId() + "$" + indexingType,
            incoming.getState());
      }
    }
  }


  @Override
  protected boolean isTenantAware() {
    return true;
  }


  @Override
  protected EntityManager getEntityManager() {
    return em;
  }


  @Override
  protected void onIndexAllBegin() {
    final String tenantId = tenantResolver.resolveTenantId();
    final IndexationProcessState previousState = indexationProcessStates.get(tenantId);

    if (previousState == null || !previousState.equals(IndexationProcessState.INDEXING)) {
      log.info("Index all types started for tenant {}.", tenantId);
      notifyAllInstances(tenantId, null, IndexationProcessState.INDEXING);
    } else {
      throw new UnsupportedOperationException(
          "Another node is currently indexing the tenant : " + tenantId + ".");
    }
  }

 
  private void notifyAllInstances(final String tenantId, final Class<?> type,
      final IndexationProcessState state) {
    final IndexationStateMessage payload = new IndexationStateMessage();
    payload.setIndexingType(type != null ? type.getSimpleName() : null);
    payload.setState(state);
    payload.setTenantId(tenantId);

    final Message<IndexationStateMessage> msg = MessageBuilder.withPayload(payload).build();
    indexationProcessor.indexationProcessStates().send(msg);
  }


  @Override
  protected void onIndexTypeBegin(Class<?> type) {
    final String tenantId = tenantResolver.resolveTenantId();
    final IndexationProcessState previousState =
        indexationProcessStates.get(tenantId + "$" + type.getSimpleName());
    if (previousState == null || previousState.equals(IndexationProcessState.INDEXING)) {
      log.debug("Indexation of type : {} beginned for tenant: {}.", type, tenantId);
      notifyAllInstances(tenantId, type, IndexationProcessState.INDEXING);
    } else {
      throw new UnsupportedOperationException("Another node is currently indexing the type "
          + type.getSimpleName() + " of tenant : " + tenantId + ".");
    }
  }


  @Override
  protected void onIndexTypeCompleted(Class<?> type, AtomicInteger progress, int total) {
    final String tenantId = tenantResolver.resolveTenantId();
    log.debug("Indexation of type : {}, completed for tenant: {}, progresssion {} / {}.", type,
        tenantId, progress.incrementAndGet(), total);
    notifyAllInstances(tenantId, type, IndexationProcessState.COMPLETED);
  }


  @Override
  protected void onIndexProcessCompletion() {
    final String tenantId = tenantResolver.resolveTenantId();
    log.info("Indexation completed for tenant: {}.", tenantId);
    notifyAllInstances(tenantId, null, IndexationProcessState.COMPLETED);
  }
}
