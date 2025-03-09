package com.team12;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.team12.clients.notification.dto.NotificationRequest;
import com.team12.clients.notification.dto.NotificationType;
import com.team12.event.notification.controller.NotificationController;
import com.team12.event.notification.entity.NotificationDTO;
import com.team12.event.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class NotificationControllerTest {

//    private NotificationService notificationService;
//    private NotificationController notificationController;
//
//    @BeforeEach
//    void setUp() {
//        notificationService = mock(NotificationService.class);
//        notificationController = new NotificationController(notificationService);
//    }
//
//    @Test
//    void sendNotification_successful() {
//        NotificationRequest request = new NotificationRequest(UUID.randomUUID(),"Notification content", NotificationType.COMMENT_POSTED);
//
//        doNothing().when(notificationService).sendNotification(request);
//
//        notificationController.sendNotification(request);
//
//        verify(notificationService, times(1)).sendNotification(request);
//    }
//
//    @Test
//    void getNotificationsById_successful() {
//        UUID userId = UUID.randomUUID();
//
//        List<NotificationDTO> notifications = Collections.singletonList(new NotificationDTO("Notification content", LocalDateTime.now(), NotificationType.COMMENT_POSTED));
//
//        when(notificationService.getNotificationListById(userId)).thenReturn(Optional.of(notifications));
//        ResponseEntity<List<NotificationDTO>> response = notificationController.getNotificationsById(userId);
//        verify(notificationService, times(1)).getNotificationListById(userId);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(notifications, response.getBody());
//    }
//
//
//
//    @Test
//    void deleteNotification_successful() {
//        UUID userId = UUID.randomUUID();
//        when(notificationService.notificationDelete(userId, 1)).thenReturn(true);
//
//        ResponseEntity<String> response = notificationController.deleteNotification(userId, 1);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Deleted notification successfully.", response.getBody());
//    }
//
//    @Test
//    void deleteNotification_notFound() {
//        UUID userId = UUID.randomUUID();
//        when(notificationService.notificationDelete(userId, 1)).thenReturn(false);
//
//        ResponseEntity<String> response = notificationController.deleteNotification(userId, 1);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertEquals("Notification not found or empty", response.getBody());
//    }
}
