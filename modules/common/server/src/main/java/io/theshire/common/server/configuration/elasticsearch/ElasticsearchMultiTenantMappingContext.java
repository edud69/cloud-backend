

package io.theshire.common.server.configuration.elasticsearch;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchPersistentProperty;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;


public class ElasticsearchMultiTenantMappingContext
    extends
      AbstractMappingContext<ElasticsearchMultiTenantPersistentEntity<?>,
                             ElasticsearchPersistentProperty>
    implements
      ApplicationContextAware {

 
  private ApplicationContext context;


  @Override
  protected <T> ElasticsearchMultiTenantPersistentEntity<?>
      createPersistentEntity(TypeInformation<T> typeInformation) {
    final ElasticsearchMultiTenantPersistentEntity<T> persistentEntity =
        new ElasticsearchMultiTenantPersistentEntity<T>(typeInformation);
    if (context != null) {
      persistentEntity.setApplicationContext(context);
    }
    return persistentEntity;
  }


  @Override
  protected ElasticsearchPersistentProperty createPersistentProperty(Field field,
      PropertyDescriptor descriptor, ElasticsearchMultiTenantPersistentEntity<?> owner,
      SimpleTypeHolder simpleTypeHolder) {
    return new SimpleElasticsearchPersistentProperty(field, descriptor, owner, simpleTypeHolder);
  }


  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    this.context = context;
  }
}
