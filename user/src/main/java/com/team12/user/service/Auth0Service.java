package com.team12.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class Auth0Service {
    private final String domain;
    private final String clientId;
    private final String clientSecret;
    private final String audience;
    private String accessToken;
    private Instant tokenExpiryTime;

    public Auth0Service(@Value("${auth0.domain}") String domain,
                        @Value("${auth0.client-id}") String clientId,
                        @Value("${auth0.client-secret}") String clientSecret,
                        @Value("${auth0.audience}") String audience) {
        this.domain = domain;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.audience = audience;
    }

    public String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(tokenExpiryTime)) {
            refreshAccessToken();
        }
        return accessToken;
    }

    private void refreshAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://" + domain + "/oauth/token";

        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("audience", audience);
        body.put("grant_type", "client_credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> responseBody = response.getBody();
            this.accessToken = (String) responseBody.get("access_token");
            this.tokenExpiryTime = Instant.now().plusSeconds((Integer) responseBody.get("expires_in") - 60);
        }
    }
}
