

package io.theshire.common.service.infrastructure.impl.email;


public class EmailException extends RuntimeException {

 
  private static final long serialVersionUID = 842287791714511664L;

 
  public EmailException(String message) {
    super(message);
  }

 
  public EmailException(String message, Throwable throwable) {
    super(message, throwable);
  }

}
