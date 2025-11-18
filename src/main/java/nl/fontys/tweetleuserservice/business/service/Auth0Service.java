package nl.fontys.tweetleuserservice.business.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class Auth0Service {

    @Value("${auth0.domain}")
    private String domain;

    @Value("${auth0.clientId}")
    private String clientId;

    @Value("${auth0.clientSecret}")
    private String clientSecret;

    @Value("${auth0.audience}")
    private String audience;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getManagementToken() {
        String url = "https://" + domain + "/oauth/token";

        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("audience", audience);
        body.put("grant_type", "client_credentials");

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, body, Map.class);

        return (String) response.getBody().get("access_token");
    }

    public void deleteAuth0User(String auth0UserId) {
        String token = getManagementToken();

        String url = "https://" + domain + "/api/v2/users/" + auth0UserId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }
}
