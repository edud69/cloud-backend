

package io.theshire.common.utils.oauth2.resource.identifier;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class OAuth2ResourceIdentifierTest {

 
  @Test
  public void shouldEnumerateAdminServices() {
    final Set<OAuth2ResourceIdentifier> adminServices = new HashSet<OAuth2ResourceIdentifier>();
    adminServices.add(OAuth2ResourceIdentifier.AdminPanel);
    Assert.assertEquals(adminServices, OAuth2ResourceIdentifier.enumerateAdminServices());
  }

 
  @Test
  public void shouldHaveUniqueMicroserviceNames() {
    Assert.assertEquals(OAuth2ResourceIdentifier.values().length,
        Arrays.stream(OAuth2ResourceIdentifier.values())
            .filter(item -> item.getMicroserviceName() != null
                && Arrays.stream(OAuth2ResourceIdentifier.values())
                    .filter(item2 -> item.getMicroserviceName().equals(item2.getMicroserviceName()))
                    .count() == 1)
            .collect(Collectors.toList()).size());
  }

}
