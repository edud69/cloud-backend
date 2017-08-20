

package io.theshire.common.endpoint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


public class ManagedRestEndpointTest {

 
  private ManagedRestEndpoint classUnderTest;

 
  private class TestableManagedRestEndpoint extends ManagedRestEndpoint {
  }

 
  @Before
  public void setup() {
    this.classUnderTest = new TestableManagedRestEndpoint();
  }

 
  @Test
  public void shouldBuildAnEmptyResponse() {
    final ResponseEntity<String> response = classUnderTest.buildEmptyResponse();
    Assert.assertNull(response.getBody());
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

 
  @Test
  public void shouldBuildOkResponse() {
    final ResponseEntity<String> response = this.classUnderTest.buildResponse("a response");
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assert.assertEquals("a response", response.getBody());
  }

 
  @Test
  public void shouldBuildNotFoundResponse() {
    final ResponseEntity<String> response = this.classUnderTest.buildResponse(null);
    Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Assert.assertNull(response.getBody());
  }

 
  @Test
  public void shouldBuildNotFoundResponseFromAnEmptyCollection() {
    final ResponseEntity<List<String>> response =
        this.classUnderTest.buildResponse(new ArrayList<String>());
    Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Assert.assertNull(response.getBody());
  }

 
  @Test
  public void shouldBuildNotFoundResponseFromAnArray() {
    final ResponseEntity<String[]> response = this.classUnderTest.buildResponse(new String[] {});
    Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Assert.assertNull(response.getBody());
  }

}
