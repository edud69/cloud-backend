

package io.theshire.common.server.configuration.jpa;

import io.theshire.common.server.tenant.TenantConnectionProvider;
import io.theshire.common.server.tenant.TenantResolver;
import io.theshire.common.utils.constants.PackageConstants;
import io.theshire.common.utils.jpa.constants.JpaPersistenceUnitConstants;
import io.theshire.common.utils.jpa.constants.JpaTransactionManagerConstants;

import org.hibernate.MultiTenancyStrategy;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
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
        pattern = JpaMultitenantConfigurer.JPA_REPOSITORY_PATTERN) },
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = JpaTransactionManagerConstants.MULTI_TENANT_TRANSACTION_MANAGER)
public class JpaMultitenantConfigurer {

 
  protected static final String JPA_REPOSITORY_PATTERN = ".*JpaRepository";

 
  @Primary
  @Bean(name = JpaTransactionManagerConstants.MULTI_TENANT_TRANSACTION_MANAGER)
  protected PlatformTransactionManager
      singleTenantTransactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }

 
  @Primary
  @Bean(name = "entityManagerFactory")
  public LocalContainerEntityManagerFactoryBean tenantAwareEntityManagerFactory(
      final JpaVendorAdapter jpaVendorAdapter, final JpaProperties jpaProperties,
      final TenantConnectionProvider multiTenantConnectionProvider, final DataSource dataSource) {
    final Map<String, Object> additionalJpaProperties =
        new LinkedHashMap<>(jpaProperties.getProperties());
    additionalJpaProperties.put(org.hibernate.cfg.Environment.MULTI_TENANT,
        MultiTenancyStrategy.SCHEMA);
    additionalJpaProperties.put(org.hibernate.cfg.Environment.MULTI_TENANT_CONNECTION_PROVIDER,
        multiTenantConnectionProvider);
    additionalJpaProperties.put(org.hibernate.cfg.Environment.MULTI_TENANT_IDENTIFIER_RESOLVER,
        TenantResolver.class);
    return new EntityManagerFactoryBuilder(jpaVendorAdapter, additionalJpaProperties, null)
        .dataSource(cloneDefaultDatasource(dataSource)).packages(PackageConstants.ROOT_BASE_PACKAGE)
        .persistenceUnit(JpaPersistenceUnitConstants.MULTI_TENANT_PERSISTENCE_CONTEXT).build();
  }

 
  private DataSource cloneDefaultDatasource(final DataSource dataSource) {
    final org.apache.tomcat.jdbc.pool.DataSource currentDataSource =
        (org.apache.tomcat.jdbc.pool.DataSource) dataSource;

    // In multitenant context, the default datasource (which represents a non-tenant context) does
    // not need to have a connection pool

    final PGSimpleDataSource mainDs = new PGSimpleDataSource();
    mainDs.setPassword(currentDataSource.getPassword());
    mainDs.setUrl(currentDataSource.getUrl());
    mainDs.setUser(currentDataSource.getUsername());

    return mainDs;
  }
}
