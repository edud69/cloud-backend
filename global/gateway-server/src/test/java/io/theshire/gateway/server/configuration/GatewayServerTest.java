

package io.theshire.gateway.server.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.SpringApplication;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SpringApplication.class)
public class GatewayServerTest {

 
  private final GatewayServer classUnderTest = new GatewayServer();

 
  @SuppressWarnings("static-access")
  @Test
  public void testMain() throws Exception {
    PowerMockito.mockStatic(SpringApplication.class);
    PowerMockito.doReturn(null).when(SpringApplication.class);

    final String[] args = new String[] {};
    classUnderTest.main(args);

    PowerMockito.verifyStatic(Mockito.times(1));
    Assert.assertEquals("rest-api-server", System.getProperty("spring.config.name"));
  }

}
