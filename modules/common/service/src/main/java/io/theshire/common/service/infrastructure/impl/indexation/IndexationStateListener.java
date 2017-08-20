

package io.theshire.common.service.infrastructure.impl.indexation;

import io.theshire.common.service.infrastructure.indexation.IndexationProcessor;
import io.theshire.common.service.infrastructure.indexation.IndexationSingleTenantService;
import io.theshire.common.service.infrastructure.indexation.IndexationTenantAwareService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;


@Component
class IndexationStateListener {

 
  @Autowired
  private IndexationTenantAwareService indexationTenantAwareService;

 
  @Autowired
  private IndexationSingleTenantService indexationSingleTenantService;

 
  @StreamListener(IndexationProcessor.INDEXATION_INPUT)
  public void indexationStateChanged(final IndexationStateMessage incoming) {
    ((IndexationServiceAbstract)indexationSingleTenantService).onIndexationStateChanged(incoming);
    ((IndexationServiceAbstract)indexationTenantAwareService).onIndexationStateChanged(incoming);
  }

}
