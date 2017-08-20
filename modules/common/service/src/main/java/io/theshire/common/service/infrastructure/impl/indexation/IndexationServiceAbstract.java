

package io.theshire.common.service.infrastructure.impl.indexation;

import io.theshire.common.domain.DomainObject;
import io.theshire.common.domain.impl.repository.ElasticsearchSpringRepository;
import io.theshire.common.service.infrastructure.indexation.IndexationProcessor;
import io.theshire.common.service.infrastructure.indexation.IndexationProcessor.IndexationProcessState;
import io.theshire.common.service.infrastructure.indexation.IndexationService;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;


@Transactional
@EnableBinding(IndexationProcessor.class)
public abstract class IndexationServiceAbstract implements IndexationService {

 
  private static final int BULK_SIZE = 100;

 
  @Autowired
  private ListableBeanFactory listableBeanFactory;

 
  @Autowired
  protected IndexationProcessor indexationProcessor;

  @Autowired
  private ThreadPoolTaskExecutor threadPool;

 
  protected final Map<String, IndexationProcessState> indexationProcessStates =
      new ConcurrentHashMap<>();

 
  protected abstract void onIndexationStateChanged(final IndexationStateMessage incoming);

 
  protected abstract EntityManager getEntityManager();

 
  protected abstract boolean isTenantAware();

 
  protected abstract void onIndexAllBegin();

 
  protected abstract void onIndexTypeBegin(Class<?> type);

 
  protected abstract void onIndexTypeCompleted(Class<?> type, final AtomicInteger progress,
      final int total);

 
  protected abstract void onIndexProcessCompletion();


  @Override
  public void indexAll() {
    threadPool.execute(() -> doIndexAll(false));
  }


  @Override
  public void deleteAll() {
    threadPool.execute(() -> doIndexAll(true));
  }

 
  private void doIndexAll(final boolean removeOperation) {
    onIndexAllBegin();
    final Set<Class<? extends DomainObject>> indexableTypes = listIndexableTypes();
    final int total = indexableTypes.size();
    final AtomicInteger progress = new AtomicInteger(0);
    indexableTypes.forEach(it -> doIndexType(it, progress, total, removeOperation));
    if (indexableTypes.isEmpty()) {
      onIndexProcessCompletion();
    }
  }


  @SuppressWarnings("unchecked")
  @Override
  public Set<Class<? extends DomainObject>> listIndexableTypes() {
    return listableBeanFactory.getBeansOfType(ElasticsearchSpringRepository.class).values().stream()
        .filter(repo -> isTenantAware() && repo.isTenantAwareRepository()
            || !isTenantAware() && !repo.isTenantAwareRepository())
        .map(repo -> ((Class<DomainObject>) repo.getDomainClass())).collect(Collectors.toSet());
  }

 
  @SuppressWarnings("unchecked")
  private <T extends DomainObject> ElasticsearchSpringRepository<T>
      getRepositoryFor(Class<T> clazz) {
    @SuppressWarnings("rawtypes")
    final Optional<ElasticsearchSpringRepository> result =
        listableBeanFactory.getBeansOfType(ElasticsearchSpringRepository.class).values().stream()
            .filter(repo -> repo.getDomainClass().equals(clazz))
            .filter(repo -> isTenantAware() && !repo.isTenantAwareRepository()
                || !isTenantAware() && repo.isTenantAwareRepository())
            .findFirst();

    if (!result.isPresent()) {
      throw new UnsupportedOperationException(
          "Cannot index a tenant aware entity with a non tenant aware repository or "
              + "a non tenant aware entity with a tenant aware repository, domainClass: "
              + "clazz.");
    }

    return result.get();
  }

 
  private <T extends DomainObject> void doIndexType(Class<T> type, final AtomicInteger progress,
      final int total, final boolean removeOperation) {
    onIndexTypeBegin(type);

    ScrollableResults scroll = null;

    try {
      final ElasticsearchSpringRepository<T> elasticSearchRepository = getRepositoryFor(type);
      elasticSearchRepository.deleteAll();

      if (!removeOperation) {
        scroll = getEntityManager().createQuery("Select a from " + type.getName() + " a", type)
            .unwrap(Query.class).setFetchSize(BULK_SIZE).scroll(ScrollMode.FORWARD_ONLY);
        List<T> bulkObjects = this.loadBatch(scroll, type);

        while (!bulkObjects.isEmpty()) {
          elasticSearchRepository.save(bulkObjects);
          bulkObjects = this.loadBatch(scroll, type);
        }
      }
    } finally {
      if (scroll != null) {
        scroll.close();
      }

      onIndexTypeCompleted(type, progress, total);
      if (progress.get() == total) {
        onIndexProcessCompletion();
      }
    }
  }


  @Override
  public <T extends DomainObject> void indexType(Class<T> type) {
    threadPool.execute(() -> doIndexType(type, new AtomicInteger(0), 1, false));
  }

 
  private <T extends DomainObject> List<T> loadBatch(final ScrollableResults objects,
      final Class<T> type) {
    final List<T> bulkObjects = new ArrayList<>(BULK_SIZE);

    while (bulkObjects.size() < BULK_SIZE) {
      if (!objects.next()) {
        return bulkObjects;
      }

      bulkObjects.add(type.cast(objects.get()[0]));
    }

    return bulkObjects;
  }

}
