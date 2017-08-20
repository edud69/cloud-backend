

package io.theshire.common.domain.token;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;


public class RandomConfirmationTokenTest {

 
  @Test
  public void shouldBeRandom() {
    final Set<String> cached = new HashSet<>();
    for (int i = 0; i < 100; i++) {
      final RandomConfirmationToken randomToken = new RandomConfirmationToken();
      if (cached.contains(randomToken.getToken())) {
        Assert.fail("Token value should be unique!");
      }
      cached.add(randomToken.getToken());
    }
  }

 
  @Test
  public void shouldBeTheSameWhenValueMatches() {
    String valueOfFirstToken = null;
    RandomConfirmationToken firstToken = null;

    for (int i = 0; i < 100; i++) {
      final RandomConfirmationToken randomToken = new RandomConfirmationToken();
      if (i == 0) {
        firstToken = randomToken;
        valueOfFirstToken = randomToken.getToken();
        Assert.assertNotNull(valueOfFirstToken);
      } else {
        Assert.assertFalse(randomToken.isSame(valueOfFirstToken));
      }
    }

    Assert.assertTrue(firstToken.isSame(valueOfFirstToken));
  }

}
