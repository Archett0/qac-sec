package com.team12.event.notification.entity;

import com.team12.clients.notification.dto.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {

    private String message;

    private LocalDateTime sentAt;

    private NotificationType notificationType;

}
