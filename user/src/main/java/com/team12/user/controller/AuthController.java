package com.team12.user.controller;

import com.team12.clients.user.dto.AuthRequest;
import com.team12.clients.user.dto.UserRegistrationRequest;
import com.team12.user.config.JwtUtil;
import com.team12.user.entity.Role;
import com.team12.user.entity.User;
import com.team12.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("api/v1/user/auth")
@Slf4j
@AllArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest authRequest) {

        User user = userRepository.findByEmail(authRequest.email());

        if (user == null || !passwordEncoder.matches(authRequest.password(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "message", "Invalid credentials",
                            "timestamp", System.currentTimeMillis()
                    )
            );
        }

        String token = jwtUtil.createToken(String.valueOf(user.getId()), user.getRole());

        return ResponseEntity.ok(
                Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "Login successful",
                        "token", token,
                        "timestamp", System.currentTimeMillis()
                )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationRequest registerRequest) {

        if (userRepository.findByEmail(registerRequest.email()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
        }
        String encodedPassword = passwordEncoder.encode(registerRequest.password());

        User user = new User();
        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        user.setPasswordHash(encodedPassword);
        user.setRole(Role.USER);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/validate")
    public boolean validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String actualToken = token.substring(7);
            return jwtUtil.validateToken(actualToken);
        }
        return false;
    }


    @GetMapping("/google_login")
    public void handleGoogleLogin(@RequestParam("code") String authorizationCode, HttpServletRequest req, HttpServletResponse rep) {
        String tokenEndpoint = System.getenv("QAC_G_TOKEN");
        String userInfoEndpoint = System.getenv("QAC_G_USERINFO");
        String clientId = System.getenv("QAC_G_CLIENTID");
        String clientSecret = System.getenv("QAC_G_CLIENTSECRET");

        RestTemplate restTemplate = new RestTemplate();

        // Step 1: 用授权码获取 Access Token
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", authorizationCode);
        params.add("redirect_uri", "http://localhost:8081/api/v1/user/auth/google_login");
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
        String accessToken = (String) response.getBody().get("access_token");

        // Step 2: 用 Access Token 获取用户信息
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);
        HttpEntity<String> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoEndpoint, HttpMethod.GET, userInfoRequest, Map.class);
        Map<String, Object> userInfo = userInfoResponse.getBody();

        String googleId = (String) userInfo.get("sub");
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        // String picture = (String) userInfo.get("picture");

        // Register if user not found
        User user = userRepository.findByEmail(email);
        if (user == null) {
            UserRegistrationRequest registerRequest = new UserRegistrationRequest(name, googleId, email);
            this.register(registerRequest);
            user = userRepository.findByEmail(email);
        }

        String token = jwtUtil.createToken(String.valueOf(user.getId()), user.getRole());

        try {
            rep.sendRedirect("http://localhost:5173/login?token=" + token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
