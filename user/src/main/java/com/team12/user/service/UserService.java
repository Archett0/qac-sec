package com.team12.user.service;


import com.team12.user.entity.Auth0User;
import com.team12.user.entity.Auth0UserFull;
import com.team12.user.entity.Role;
import com.team12.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final Auth0Service auth0Service;
    private final RestTemplate restTemplate;
    private final String auth0Domain;
    private static final String urlPrefix = "https://";
    private static final String ADMIN_ROLE_ID = "rol_AsQwOJJGwaxjabHk";
    private static final String USER_ROLE_ID = "rol_PSmHTpwEinvmx7NP";

    public UserService(Auth0Service auth0Service, @Value("${auth0.domain}") String auth0Domain) {
        this.auth0Service = auth0Service;
        this.auth0Domain = auth0Domain;
        this.restTemplate = new RestTemplate();
    }

    private List<String> fetchRoleUsers(String roleId) {
        String url = urlPrefix + auth0Domain + "/api/v2/roles/" + roleId + "/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth0Service.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Auth0User[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Auth0User[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return Arrays.stream(response.getBody())
                    .map(Auth0User::getEmail)
                    .toList();
        }
        return Collections.emptyList();
    }

    private Role determineUserRole(String email, List<String> adminEmails, List<String> userEmails) {
        if (adminEmails.contains(email)) {
            return Role.ADMIN;
        }
        if (userEmails.contains(email)) {
            return Role.USER;
        }
        return Role.USER;
    }

    public User getUserById(UUID id) {
        String url = urlPrefix + auth0Domain + "/api/v2/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth0Service.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Auth0User[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Auth0User[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<String> adminEmails = fetchRoleUsers(ADMIN_ROLE_ID);
            List<String> userEmails = fetchRoleUsers(USER_ROLE_ID);
            return Arrays.stream(response.getBody())
                    .filter(auth0User -> auth0User.getUserMetadata() != null && auth0User.getUserMetadata().containsKey("uuid"))
                    .filter(auth0User -> {
                        UUID userId = UUID.fromString(auth0User.getUserMetadata().get("uuid").toString());
                        return userId.equals(id);
                    })
                    .map(auth0User -> {
                        Role userRole = determineUserRole(auth0User.getEmail(), adminEmails, userEmails);
                        return new User(id, auth0User.getUsername(), auth0User.getEmail(), userRole);
                    })
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public User updateUser(UUID id, User user) {
        String fetchUsersUrl = urlPrefix + auth0Domain + "/api/v2/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth0Service.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Auth0UserFull[]> fetchUsersResponse = restTemplate.exchange(fetchUsersUrl, HttpMethod.GET, entity, Auth0UserFull[].class);

        if (!fetchUsersResponse.getStatusCode().is2xxSuccessful() || fetchUsersResponse.getBody() == null) {
            return null;
        }

        Optional<Auth0UserFull> auth0UserOptional = Arrays.stream(fetchUsersResponse.getBody())
                .filter(auth0User -> auth0User.getUserMetadata() != null && auth0User.getUserMetadata().containsKey("uuid"))
                .filter(auth0User -> {
                    UUID userId = UUID.fromString(auth0User.getUserMetadata().get("uuid").toString());
                    return userId.equals(id);
                })
                .findFirst();

        if (auth0UserOptional.isEmpty()) {
            return null;
        }

        String auth0UserId = auth0UserOptional.get().getUserId();
        String updateRoleUrl = urlPrefix + auth0Domain + "/api/v2/users/" + auth0UserId + "/roles";
        String prevRoleId = (user.getRole() == Role.ADMIN) ? USER_ROLE_ID : ADMIN_ROLE_ID;
        String roleId = (user.getRole() == Role.ADMIN) ? ADMIN_ROLE_ID : USER_ROLE_ID;

        HttpHeaders deleteHeaders = new HttpHeaders();
        deleteHeaders.setContentType(MediaType.APPLICATION_JSON);
        deleteHeaders.setBearerAuth(auth0Service.getAccessToken());

        Map<String, List<String>> deleteRequestBody = new HashMap<>();
        deleteRequestBody.put("roles", Collections.singletonList(prevRoleId));

        HttpEntity<Map<String, List<String>>> deleteEntity = new HttpEntity<>(deleteRequestBody, deleteHeaders);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(updateRoleUrl, HttpMethod.DELETE, deleteEntity, Void.class);

        if (!deleteResponse.getStatusCode().is2xxSuccessful()) {
            return null;
        }

        HttpHeaders updateHeaders = new HttpHeaders();
        updateHeaders.setContentType(MediaType.APPLICATION_JSON);
        updateHeaders.setBearerAuth(auth0Service.getAccessToken());
        Map<String, List<String>> requestBody = new HashMap<>();
        requestBody.put("roles", Collections.singletonList(roleId));

        HttpEntity<Map<String, List<String>>> updateEntity = new HttpEntity<>(requestBody, updateHeaders);

        ResponseEntity<Void> updateResponse = restTemplate.exchange(updateRoleUrl, HttpMethod.POST, updateEntity, Void.class);

        if (updateResponse.getStatusCode().is2xxSuccessful()) {
            return user;
        }

        return null;
    }


    public boolean deleteUser(UUID id) {
        String url = urlPrefix + auth0Domain + "/api/v2/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth0Service.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Auth0UserFull[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Auth0UserFull[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<Auth0UserFull> auth0Users = Arrays.asList(response.getBody());
            Optional<Auth0UserFull> userToDelete = auth0Users.stream()
                    .filter(auth0User -> auth0User.getUserMetadata() != null && auth0User.getUserMetadata().containsKey("uuid"))
                    .filter(auth0User -> {
                        UUID userId = UUID.fromString(auth0User.getUserMetadata().get("uuid").toString());
                        return userId.equals(id);
                    })
                    .findFirst();

            if (userToDelete.isPresent()) {
                String auth0UserId = userToDelete.get().getUserId();
                String url_del = urlPrefix + auth0Domain + "/api/v2/users/" + auth0UserId;
                HttpHeaders headers_del = new HttpHeaders();
                headers_del.setBearerAuth(auth0Service.getAccessToken());
                HttpEntity<Void> entity_del = new HttpEntity<>(headers_del);
                ResponseEntity<Void> response_del = restTemplate.exchange(url_del, HttpMethod.DELETE, entity_del, Void.class);
                return response_del.getStatusCode().is2xxSuccessful();
            }
        }
        return false;
    }

    public Iterable<User> getAllUsers() {
        String url = urlPrefix + auth0Domain + "/api/v2/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth0Service.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Auth0User[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Auth0User[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<String> adminEmails = fetchRoleUsers(ADMIN_ROLE_ID);
            List<String> userEmails = fetchRoleUsers(USER_ROLE_ID);
            return Arrays.stream(response.getBody())
                    .map(auth0User -> {
                        UUID userId = null;
                        if (auth0User.getUserMetadata() != null && auth0User.getUserMetadata().containsKey("uuid")) {
                            userId = UUID.fromString(auth0User.getUserMetadata().get("uuid").toString());
                        }
                        Role userRole = determineUserRole(auth0User.getEmail(), adminEmails, userEmails);
                        return new User(userId, auth0User.getUsername(), auth0User.getEmail(), userRole);
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public Map<UUID, String> getUserNamesByIds(List<UUID> userIds) {
        String url = urlPrefix + auth0Domain + "/api/v2/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth0Service.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Auth0User[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Auth0User[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return Arrays.stream(response.getBody())
                    .filter(auth0User -> auth0User.getUserMetadata() != null && auth0User.getUserMetadata().containsKey("uuid"))
                    .map(auth0User -> {
                        UUID userId = UUID.fromString(auth0User.getUserMetadata().get("uuid").toString());
                        return new AbstractMap.SimpleEntry<>(userId, auth0User.getUsername());
                    })
                    .filter(entry -> userIds.contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        return Collections.emptyMap();
    }

    public List<User> searchUsersByUsername(String keyword) {
        String url = urlPrefix + auth0Domain + "/api/v2/users?q=username:" + keyword + "*&search_engine=v3";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth0Service.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Auth0User[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Auth0User[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<String> adminEmails = fetchRoleUsers(ADMIN_ROLE_ID);
            List<String> userEmails = fetchRoleUsers(USER_ROLE_ID);
            return Arrays.stream(response.getBody())
                    .map(auth0User -> {
                        UUID userId = null;
                        if (auth0User.getUserMetadata() != null && auth0User.getUserMetadata().containsKey("uuid")) {
                            userId = UUID.fromString(auth0User.getUserMetadata().get("uuid").toString());
                        }
                        Role userRole = determineUserRole(auth0User.getEmail(), adminEmails, userEmails);
                        return new User(userId, auth0User.getUsername(), auth0User.getEmail(), userRole);
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
