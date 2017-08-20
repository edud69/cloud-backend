

package io.theshire.chat.service;

import io.theshire.chat.domain.ChatGroupMessage;
import io.theshire.chat.domain.ChatPrivateMessage;
import io.theshire.common.service.OutPort;
import io.theshire.common.utils.security.permission.constants.SecurityChatMicroservicePermissionConstants;

import org.springframework.security.access.prepost.PreAuthorize;


public interface ChatService {

 
  @PreAuthorize("hasPermission(#input, '" + SecurityChatMicroservicePermissionConstants.CHAT_SEND
      + "')")
  void send(final ChatSendPrivateInPort input, final OutPort<ChatPrivateMessage, ?> output);

 
  @PreAuthorize("hasPermission(#input, '" + SecurityChatMicroservicePermissionConstants.CHAT_SEND
      + "')")
  void send(final ChatSendGroupInPort input, final OutPort<ChatGroupMessage, ?> output);

}
