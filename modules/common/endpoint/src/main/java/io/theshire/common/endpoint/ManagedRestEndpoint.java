

package io.theshire.common.endpoint;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Array;
import java.util.Collection;


public abstract class ManagedRestEndpoint {

 
  protected ResponseEntity<String> buildEmptyResponse() {
    return new ResponseEntity<>(HttpStatus.OK);
  }

 
  protected <T> ResponseEntity<T> buildResponse(final T body) {
    if (body == null || body instanceof Collection && Collection.class.cast(body).isEmpty()
        || isEmptyArray(body)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(body, HttpStatus.OK);
  }

 
  private static boolean isEmptyArray(final Object object) {
    if (object != null && object.getClass().isArray()) {
      return Array.getLength(object) == 0;
    }

    return false;
  }
}
