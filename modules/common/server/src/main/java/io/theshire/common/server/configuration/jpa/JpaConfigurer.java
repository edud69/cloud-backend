

package io.theshire.common.server.configuration.jpa;

import lombok.Getter;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;


@Configuration
@EnableTransactionManagement
@Import({ JpaSingleTenantConfigurer.class, JpaMultitenantConfigurer.class })
public class JpaConfigurer {

 
  @Getter
  private static boolean active;

 
  @PostConstruct
  protected void configInitialized() {
    setActive(true);
  }

 
  private static final void setActive(final boolean isActive) {
    active = isActive;
  }

}
