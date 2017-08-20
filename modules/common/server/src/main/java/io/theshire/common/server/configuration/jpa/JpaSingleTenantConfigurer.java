

package io.theshire.common.server.configuration.jpa;

import io.theshire.common.server.tenant.TenantConnectionProvider;
import io.theshire.common.utils.constants.PackageConstants;
import io.theshire.common.utils.jpa.constants.JpaPersistenceUnitConstants;
import io.theshire.common.utils.jpa.constants.JpaTransactionManagerConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;


@Configuration
@EnableJpaRepositories(basePackages = PackageConstants.ROOT_BASE_PACKAGE,
    includeFilters = { @ComponentScan.Filter(type = FilterType.REGEX,
        pattern = JpaSingleTenantConfigurer.JPA_SINGLE_TENANT_REPOSITORY_PATTERN) },
    entityManagerFactoryRef = "singleTenantEntityManagerFactory",
    transactionManagerRef = JpaTransactionManagerConstants.SINGLE_TENANT_TRANSACTION_MANAGER)
public class JpaSingleTenantConfigurer {

 
  protected static final String JPA_SINGLE_TENANT_REPOSITORY_PATTERN =
      ".*JpaSingleTenantRepository";

 
  @Autowired
  private DataSource dataSource;

 
  @Bean(name = JpaTransactionManagerConstants.SINGLE_TENANT_TRANSACTION_MANAGER)
  protected PlatformTransactionManager singleTenantTransactionManager(
      @Qualifier("singleTenantEntityManagerFactory") EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }

 
  @Bean(name = "singleTenantEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean singleTenantEntityManagerFactory(
      final JpaVendorAdapter jpaVendorAdapter, final JpaProperties jpaProperties,
      final TenantConnectionProvider multiTenantConnectionProvider) {
    final Map<String, Object> additionalJpaProperties =
        new LinkedHashMap<>(jpaProperties.getProperties());
    return new EntityManagerFactoryBuilder(jpaVendorAdapter, additionalJpaProperties, null)
        .dataSource(dataSource).packages(PackageConstants.ROOT_BASE_PACKAGE)
        .persistenceUnit(JpaPersistenceUnitConstants.SINGLE_TENANT_PERSISTENCE_CONTEXT).build();
  }
}
