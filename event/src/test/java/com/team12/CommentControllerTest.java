package com.team12;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team12.clients.comment.dto.CommentModifyRequest;
import com.team12.clients.comment.dto.CommentSendRequest;
import com.team12.event.comment.controller.CommentController;
import com.team12.event.comment.entity.CommentDto;
import com.team12.event.comment.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCommentByAnswerId_ShouldReturnComments() throws Exception {
        UUID answerId = UUID.randomUUID();
        List<CommentDto> comments = List.of(new CommentDto(UUID.randomUUID(), "Test comment", null, UUID.randomUUID().toString(), answerId, "User1"));

        when(commentService.getCommentsByAnswerId(answerId)).thenReturn(comments);

        mockMvc.perform(get("/api/v1/comment/getCommentByAnswerId/" + answerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test comment"));
    }

    @Test
    void sendComment_ShouldReturnCreatedComment() throws Exception {
        CommentSendRequest request = new CommentSendRequest("New comment", UUID.randomUUID().toString(), UUID.randomUUID());
        CommentDto responseDto = new CommentDto(UUID.randomUUID(), "New comment", null, request.ownerId(), request.answerId(), "User1");

        when(commentService.commentSend(any(CommentSendRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/comment/sendComment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("New comment"));
    }

    @Test
    void modifyComment_ShouldReturnOk_WhenModified() throws Exception {
        UUID commentId = UUID.randomUUID();
        CommentModifyRequest request = new CommentModifyRequest("Updated comment");

        when(commentService.commentModify(commentId, request)).thenReturn(true);

        mockMvc.perform(put("/api/v1/comment/modifyComment/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment modified successfully"));
    }

    @Test
    void modifyComment_ShouldReturnNotFound_WhenNotModified() throws Exception {
        UUID commentId = UUID.randomUUID();
        CommentModifyRequest request = new CommentModifyRequest("Updated comment");

        when(commentService.commentModify(commentId, request)).thenReturn(false);

        mockMvc.perform(put("/api/v1/comment/modifyComment/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Comment not found"));
    }

    @Test
    void deleteComment_ShouldReturnOk_WhenDeleted() throws Exception {
        UUID commentId = UUID.randomUUID();

        when(commentService.commentDelete(commentId)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/comment/deleteComment/" + commentId))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment deleted successfully"));
    }

    @Test
    void deleteComment_ShouldReturnNotFound_WhenNotDeleted() throws Exception {
        UUID commentId = UUID.randomUUID();

        when(commentService.commentDelete(commentId)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/comment/deleteComment/" + commentId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Comment not found"));
    }

    @Test
    void deleteCommentByAnswerId_ShouldReturnNoContent() throws Exception {
        UUID answerId = UUID.randomUUID();

        doNothing().when(commentService).commentDeleteByAnswerId(answerId);

        mockMvc.perform(delete("/api/v1/comment/deleteCommentByAnswerId/" + answerId))
                .andExpect(status().isNoContent());
    }
}
