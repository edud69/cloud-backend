

package io.theshire.common.endpoint;

import io.theshire.common.domain.exception.DomainEntityNotFoundException;
import io.theshire.common.domain.exception.DomainException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice


@Slf4j
public class ManagedRestEndpointExceptionHandler extends ResponseEntityExceptionHandler {

 
  @ExceptionHandler(HttpStatusCodeException.class)
  public ResponseEntity<String> handleHttpStatusCodeException(final HttpStatusCodeException exc) {
    return new ResponseEntity<>(exc.getMessage(), exc.getStatusCode());
  }

 
  @ExceptionHandler(DomainException.class)
  @ResponseBody
  public ResponseEntity<ApiError> handleDomainException(final DomainException exception) {
    final ApiError body = new ApiError();
    body.setCode(exception.getErrorCode());
    body.setMessage(exception.getMessage());
    body.setErrorParams(exception.getErrorParams());

    if (exception instanceof DomainEntityNotFoundException) {
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

 
  @ExceptionHandler(IllegalStateException.class)
  @ResponseBody
  public ResponseEntity<?> handleHystrixRuntimeException(final IllegalStateException exception) {
    if (exception.getCause() != null
        && exception.getCause().getClass().getSimpleName().equals("HystrixRuntimeException")) {
      return new ResponseEntity<>("Service unavailable.", HttpStatus.SERVICE_UNAVAILABLE);
    }
    return handleGenericException(exception);
  }

 
  @ExceptionHandler(RuntimeException.class)
  @ResponseBody
  public ResponseEntity<?> handleRuntimeException(final RuntimeException exception) {
    if (exception.getCause() != null && exception.getCause() instanceof DomainException) {
      return handleDomainException((DomainException) exception.getCause());
    }
    return handleGenericException(exception);
  }

 
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ResponseEntity<String> handleGenericException(final Exception exception) {
    if (exception.getCause() != null && exception.getCause() instanceof HttpStatusCodeException) {
      return handleHttpStatusCodeException((HttpStatusCodeException) exception.getCause());
    } else {
      // This handler should be rare, specific exception must be handled with a concrete message
      log.debug("Exception occurred, trace: ", exception);
      return new ResponseEntity<>("Internal Error.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
