

package io.theshire.document.service.impl.user.avatar;

import io.theshire.document.domain.user.avatar.UserAvatarUploadException;
import io.theshire.document.domain.user.avatar.UserAvatarUploadProvider;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;


@Service
class UserAvatarUploadProviderImpl implements UserAvatarUploadProvider {

 
  private final RestTemplate restTemplate = new RestTemplate();


  @Override
  public URL upload(String filename, InputStream avatar) throws UserAvatarUploadException {
    final MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("name", filename);
    map.add("filename", filename);

    try (final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
      IOUtils.copy(avatar, buffer);

      final ByteArrayResource contentsAsResource = new ByteArrayResource(buffer.toByteArray()) {
        @Override
        public String getFilename() {
          return filename;
        }
      };

      map.add("file", contentsAsResource);

      final HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);
      final HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);

      final ResponseEntity<String> response =
          restTemplate.postForEntity("http://uploads.im/api", request, String.class);
      if (response.getStatusCode().equals(HttpStatus.OK)) {
        final JSONObject jsonObj = new JSONObject(response.getBody());
        final int status = jsonObj.getInt("status_code");
        if (status == 200) {
          return new URL(jsonObj.getJSONObject("data").getString("thumb_url"));
        } else {
          throw new UserAvatarUploadException(
              "Avatar could not be uploaded. Provider replied with status of : " + status + ".");
        }
      }

      throw new UserAvatarUploadException(
          "Avatar could not be uploaded. Provider replied with status of : "
              + response.getStatusCode() + ".");
    } catch (final Exception exc) {
      throw new UserAvatarUploadException(
          "Avatar could not be uploaded. Details: " + exc.getMessage() + ".");
    }
  }

}
