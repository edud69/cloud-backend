

package io.theshire.admin.service.impl.microservices;

import io.theshire.admin.domain.MicroServiceSqlDbConfig;
import io.theshire.admin.service.microservices.MicroServicesSqlConfigService;
import io.theshire.common.service.infrastructure.bridge.MicroserviceBridgeService;
import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;


@Service
@Slf4j
class MicroServicesSqlConfigServiceImpl implements MicroServicesSqlConfigService {

 
  private static final String PROPERTIES_EXT = ".properties";

 
  @Autowired
  private MicroserviceBridgeService microserviceBridgeService;

 
  @Autowired
  private Environment environment;


  @Override
  public MicroServiceSqlDbConfig retrieveSqlConfig(String serviceName) {
    final String configFileName = serviceName + "-" + getActiveProfile() + PROPERTIES_EXT;
    final String content = microserviceBridgeService.invokeRestCall(
        OAuth2ResourceIdentifier.ConfigService, configFileName, null, HttpMethod.GET, String.class);
    return buildDbConfig(content);
  }

 
  private MicroServiceSqlDbConfig buildDbConfig(final String content) {
    if (content == null) {
      return null;
    }

    final Properties props = new Properties();
    try (final StringReader reader = new StringReader(content)) {
      try {
        props.load(reader);
      } catch (IOException exc) {
        log.error("Failed to load property files : ", exc);
      }
    }

    // check if props contains db infos
    if (props.keySet().stream().filter(k -> ((String) k).startsWith("spring.datasource")).findAny()
        .isPresent()) {
      final String driverClassName = props.getProperty("spring.datasource.driver-class-name");
      final String username = props.getProperty("spring.datasource.username");
      final String password = props.getProperty("spring.datasource.password");
      final String url = props.getProperty("spring.datasource.url");
      return new MicroServiceSqlDbConfig(url, username, password, driverClassName);
    }

    return null;
  }

 
  private String getActiveProfile() {
    final String[] profiles = environment.getActiveProfiles();
    return profiles.length > 0 ? profiles[0] : "dev";
  }

}
