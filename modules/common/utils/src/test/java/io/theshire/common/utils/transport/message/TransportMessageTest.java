

package io.theshire.common.utils.transport.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TransportMessageTest {

 

 
  @Data




  @EqualsAndHashCode(callSuper = true)
  private class TestableTransportMessage extends TransportMessage {

   
    private static final long serialVersionUID = 551686879712465762L;

   
    private String anotherPropertyToSerialize = "hello";

  }

 
  private TransportMessage classUnderTest;

 
  @Before
  public void setup() {
    this.classUnderTest = new TestableTransportMessage();
  }

 
  @Test
  public void shouldHaveABindingClassName() {
    Assert.assertEquals("TestableTransportMessage", this.classUnderTest.getBindingClassName());
  }

 
  @Test
  public void shouldSerializeAsJson() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();
    final String jsonString = mapper.writeValueAsString(this.classUnderTest);
    Assert.assertEquals(
        "{\"anotherPropertyToSerialize\":\"hello\",\"$bindingClassName\":\"TestableTransportMessage\"}",
        jsonString);
  }

}
