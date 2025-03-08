package com.team12.user.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class Auth0UserFull {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("nickname")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("user_metadata")
    private Map<String, Object> userMetadata;
}
