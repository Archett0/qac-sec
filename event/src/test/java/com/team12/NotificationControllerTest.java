package com.team12;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team12.clients.notification.dto.NotificationRequest;
import com.team12.clients.notification.dto.NotificationType;
import com.team12.event.notification.controller.NotificationController;
import com.team12.event.notification.entity.NotificationDTO;
import com.team12.event.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @MockBean
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
    }

    @Test
    void sendNotification_ShouldReturnOk() throws Exception {
        NotificationRequest request = new NotificationRequest(UUID.randomUUID(), "Test message", NotificationType.COMMENT_POSTED);

        doNothing().when(notificationService).sendNotification(any(NotificationRequest.class));

        mockMvc.perform(post("/api/v1/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void getNotificationsById_ShouldReturnNotificationList() throws Exception {
        UUID userId = UUID.randomUUID();
        List<NotificationDTO> notifications = List.of(new NotificationDTO("Test Message", null, NotificationType.UPVOTE_RECEIVED));

        when(notificationService.getNotificationListById(userId)).thenReturn(Optional.of(notifications));

        mockMvc.perform(get("/api/v1/notification/" + userId + "/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Test Message"));
    }

    @Test
    void getNotificationsById_ShouldReturnEmptyList_WhenNoNotifications() throws Exception {
        UUID userId = UUID.randomUUID();

        when(notificationService.getNotificationListById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/notification/" + userId + "/"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deleteNotification_ShouldReturnOk_WhenDeleted() throws Exception {
        UUID userId = UUID.randomUUID();
        int type = 1;

        when(notificationService.notificationDelete(userId, type)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/notification/deleteNotification/" + userId + "/" + type))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted notification successfully."));
    }

    @Test
    void deleteNotification_ShouldReturnNotFound_WhenNotDeleted() throws Exception {
        UUID userId = UUID.randomUUID();
        int type = 1;

        when(notificationService.notificationDelete(userId, type)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/notification/deleteNotification/" + userId + "/" + type))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Notification not found or empty"));
    }
}
