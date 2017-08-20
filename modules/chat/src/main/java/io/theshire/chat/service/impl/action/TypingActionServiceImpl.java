

package io.theshire.chat.service.impl.action;

import io.theshire.chat.domain.TypingAction;
import io.theshire.chat.service.action.TypingActionInPort;
import io.theshire.chat.service.action.TypingActionService;
import io.theshire.common.service.OutPort;
import io.theshire.common.utils.security.authentication.AuthenticationContext;

import org.springframework.stereotype.Service;


@Service
class TypingActionServiceImpl implements TypingActionService {


  @Override
  public void process(final TypingActionInPort input, final OutPort<TypingAction, ?> output) {
    final boolean privateChannel = input.getTargetUsername() != null;
    final TypingAction typingAction = new TypingAction(AuthenticationContext.get().getUsername(),
        privateChannel ? input.getTargetUsername() : input.getChannelName(), privateChannel);
    output.receive(typingAction);
  }

}
