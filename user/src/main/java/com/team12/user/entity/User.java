package com.team12.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class User {
    private UUID id;
    private String username;
    private String email;
    private Role role;

    public User(UUID userId, String username, String email) {
        this.id = userId;
        this.username = username;
        this.email = email;
        this.role = Role.USER;
    }
}
