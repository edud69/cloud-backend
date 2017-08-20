

package io.theshire.chat.service.action;

import static io.theshire.common.utils.security.permission.constants.SecurityChatMicroservicePermissionConstants.CHAT_SEND;

import io.theshire.chat.domain.TypingAction;
import io.theshire.common.service.OutPort;

import org.springframework.security.access.prepost.PreAuthorize;


public interface TypingActionService {

 
  @PreAuthorize("hasPermission(#input, '" + CHAT_SEND + "')")
  void process(final TypingActionInPort input, final OutPort<TypingAction, ?> output);

}
