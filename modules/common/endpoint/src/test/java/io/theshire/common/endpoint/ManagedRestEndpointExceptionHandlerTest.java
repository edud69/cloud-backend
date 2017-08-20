

package io.theshire.common.endpoint;

import io.theshire.common.domain.exception.DomainEntityNotFoundException;
import io.theshire.common.domain.exception.DomainException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.HashMap;
import java.util.Map;


@RunWith(PowerMockRunner.class)
public class ManagedRestEndpointExceptionHandlerTest {

 
  private ManagedRestEndpointExceptionHandler classUnderTest;

 
  @Before
  public void setup() {
    this.classUnderTest = new ManagedRestEndpointExceptionHandler();
  }

 
  @Test
  public void shouldHandleHttpStatusCodeException() {
    final HttpStatusCodeException exception = Mockito.mock(HttpStatusCodeException.class);
    Mockito.when(exception.getStatusCode()).thenReturn(HttpStatus.CONFLICT);
    Mockito.when(exception.getMessage()).thenReturn("exception message");

    final ResponseEntity<String> response = classUnderTest.handleHttpStatusCodeException(exception);
    Assert.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    Assert.assertEquals("exception message", response.getBody());
  }

 
  @Test
  public void shouldHandleDomainException() {
    DomainException exception = Mockito.mock(DomainException.class);
    this.prepareDomainException(exception, "errorCode", "a message", new HashMap<>());

    ResponseEntity<ApiError> response = classUnderTest.handleDomainException(exception);
    Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    this.assertApiErrorEquals(exception, response.getBody());

    // entity not found case
    exception = Mockito.mock(DomainEntityNotFoundException.class);
    this.prepareDomainException(exception, "errorCode", "a message", new HashMap<>());

    response = classUnderTest.handleDomainException(exception);
    Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    this.assertApiErrorEquals(exception, response.getBody());
  }

 
  @Test
  public void shouldHandleRuntimeException() {
    DomainException nestedException = Mockito.mock(DomainException.class);
    final ManagedRestEndpointExceptionHandler spy = Mockito.spy(classUnderTest);
    spy.handleRuntimeException(new RuntimeException(nestedException));
    Mockito.verify(spy).handleDomainException(nestedException);

    final RuntimeException exception = new RuntimeException();
    spy.handleRuntimeException(exception);
    Mockito.verify(spy).handleGenericException(exception);
  }

 
  @Test
  public void shouldHandleGenericException() {
    HttpStatusCodeException nestedException = Mockito.mock(HttpStatusCodeException.class);
    Mockito.when(nestedException.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN);
    final ManagedRestEndpointExceptionHandler spy = Mockito.spy(classUnderTest);
    spy.handleGenericException(new Exception(nestedException));
    Mockito.verify(spy).handleHttpStatusCodeException(nestedException);

    final Exception exception = new Exception();
    final ResponseEntity<String> response = spy.handleGenericException(exception);
    Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Assert.assertEquals("Internal Error.", response.getBody());
  }

 
  private void assertApiErrorEquals(final DomainException exception, final ApiError response) {
    Assert.assertEquals(exception.getErrorCode(), response.getCode());
    Assert.assertEquals(exception.getMessage(), response.getMessage());
    Assert.assertEquals(exception.getErrorParams(), response.getErrorParams());
  }

 
  private void prepareDomainException(final DomainException exception, final String errorCode,
      final String message, final Map<String, Object> errorParams) {
    Mockito.when(exception.getErrorCode()).thenReturn(errorCode);
    Mockito.when(exception.getMessage()).thenReturn(message);
    Mockito.when(exception.getErrorParams()).thenReturn(errorParams);
  }

}
