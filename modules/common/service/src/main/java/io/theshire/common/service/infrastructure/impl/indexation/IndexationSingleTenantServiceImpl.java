

package io.theshire.common.service.infrastructure.impl.indexation;

import io.theshire.common.service.infrastructure.indexation.IndexationProcessor.IndexationProcessState;
import io.theshire.common.service.infrastructure.indexation.IndexationSingleTenantService;
import io.theshire.common.utils.jpa.constants.JpaPersistenceUnitConstants;

import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;


@Slf4j
@Service
@Transactional
public class IndexationSingleTenantServiceImpl extends IndexationServiceAbstract
    implements
      IndexationSingleTenantService {

 
  private static final String SINGLE_TENANT_INDEX_KEY = "single-tenant";

 
  @PersistenceContext(unitName = JpaPersistenceUnitConstants.SINGLE_TENANT_PERSISTENCE_CONTEXT)
  private EntityManager em;

 
  @Override
  protected void onIndexationStateChanged(final IndexationStateMessage incoming) {
    if (incoming.getTenantId() != null) {
      return; // reject msg that are tenant aware
    }

    final String indexingType = incoming.getIndexingType();
    final boolean removeEntry = !incoming.getState().equals(IndexationProcessState.INDEXING);

    if (indexingType == null) {
      // Index all type query, specific type will happen in upcoming messages
      if (removeEntry) {
        this.indexationProcessStates.remove(SINGLE_TENANT_INDEX_KEY);
      } else {
        this.indexationProcessStates.putIfAbsent(SINGLE_TENANT_INDEX_KEY, incoming.getState());
      }
    } else {
      if (removeEntry) {
        this.indexationProcessStates.remove(SINGLE_TENANT_INDEX_KEY + "$" + indexingType);
      } else {
        this.indexationProcessStates.putIfAbsent(SINGLE_TENANT_INDEX_KEY + "$" + indexingType,
            incoming.getState());
      }
    }
  }


  @Override
  protected EntityManager getEntityManager() {
    return em;
  }


  @Override
  protected boolean isTenantAware() {
    return false;
  }


  @Override
  protected void onIndexAllBegin() {
    final IndexationProcessState previousState =
        indexationProcessStates.get(SINGLE_TENANT_INDEX_KEY);

    if (previousState == null || !previousState.equals(IndexationProcessState.INDEXING)) {
      log.info("Index all types started.");
      notifyAllInstances(null, IndexationProcessState.INDEXING);
    } else {
      throw new UnsupportedOperationException("Another node is currently indexing all types.");
    }
  }


  @Override
  protected void onIndexTypeBegin(Class<?> type) {
    final IndexationProcessState previousState =
        indexationProcessStates.get(SINGLE_TENANT_INDEX_KEY + "$" + type.getSimpleName());
    if (previousState == null || previousState.equals(IndexationProcessState.INDEXING)) {
      log.debug("Indexation of type : {} beginned.", type);
      notifyAllInstances(type, IndexationProcessState.INDEXING);
    } else {
      throw new UnsupportedOperationException(
          "Another node is currently indexing the type " + type.getSimpleName() + ".");
    }
  }


  @Override
  protected void onIndexTypeCompleted(Class<?> type, AtomicInteger progress, int total) {
    log.debug("Indexation of type : {}, progresssion {} / {}.", type, progress.incrementAndGet(),
        total);
    notifyAllInstances(type, IndexationProcessState.COMPLETED);
  }


  @Override
  protected void onIndexProcessCompletion() {
    log.info("Indexation completed.");
    notifyAllInstances(null, IndexationProcessState.COMPLETED);
  }

 
  private void notifyAllInstances(final Class<?> type, final IndexationProcessState state) {
    final IndexationStateMessage payload = new IndexationStateMessage();
    payload.setIndexingType(type != null ? type.getSimpleName() : null);
    payload.setState(state);

    final Message<IndexationStateMessage> msg = MessageBuilder.withPayload(payload).build();
    indexationProcessor.indexationProcessStates().send(msg);
  }
}
