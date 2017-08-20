
package io.theshire.chat.server.configuration;

import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;
import io.theshire.common.websocket.configuration.WebsocketOauth2ResourceIdentifier;


class ChatServerWebsocketOauth2ResourceIdentifier implements WebsocketOauth2ResourceIdentifier {


  @Override
  public OAuth2ResourceIdentifier getOAuth2ResourceIdentifier() {
    return OAuth2ResourceIdentifier.ChatService;
  }
}
