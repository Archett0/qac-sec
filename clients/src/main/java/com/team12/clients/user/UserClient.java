package com.team12.clients.user;

import com.team12.clients.user.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "user", path = "api/v1/user")
public interface UserClient {
    
    @GetMapping("/search")
    List<UserDto> searchUsersByUsername(@RequestParam("keyword") String keyword);

    @PostMapping("/getUserName")
    Map<UUID, String> getUsersByIds(@RequestBody List<UUID> userIds);
}
