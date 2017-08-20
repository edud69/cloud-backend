

package io.theshire.common.service.infrastructure.indexation;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;


public interface IndexationProcessor {

 
  public enum IndexationProcessState {

   
    INDEXING,
   
    COMPLETED
  }

 
  String INDEXATION_INPUT = "indexationInput";

 
  String INDEXATION_OUTPUT = "indexationOutput";

 
  @Output(IndexationProcessor.INDEXATION_OUTPUT)
  MessageChannel indexationProcessStates();

 
  @Input(IndexationProcessor.INDEXATION_INPUT)
  SubscribableChannel indexationProcessStateChanged();

}
