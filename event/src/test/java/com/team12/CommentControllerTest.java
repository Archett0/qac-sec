package com.team12;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.team12.clients.comment.dto.CommentModifyRequest;
import com.team12.clients.comment.dto.CommentSendRequest;
import com.team12.event.comment.controller.CommentController;
import com.team12.event.comment.entity.CommentDto;
import com.team12.event.comment.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@SpringBootTest
class CommentControllerTest {

//    private CommentService commentService;
//    private CommentController commentController;
//
//    @BeforeEach
//    void setUp() {
//        commentService = mock(CommentService.class);
//        commentController = new CommentController(commentService);
//    }
//
//    @Test
//    void sendComment_successful() {
//        UUID answerId = UUID.randomUUID();
//        CommentSendRequest request = new CommentSendRequest("comment content", "ownerId", answerId);
//
//        CommentDto expectedComment = new CommentDto();
//        when(commentService.commentSend(request)).thenReturn(expectedComment);
//        ResponseEntity<CommentDto> response = commentController.sendComment(request);
//        verify(commentService, times(1)).commentSend(request);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(expectedComment, response.getBody());
//    }
//
//
//    @Test
//    void deleteComment_successful() {
//        UUID commentId = UUID.randomUUID();
//        when(commentService.commentDelete(commentId)).thenReturn(true);
//
//        ResponseEntity<String> response = commentController.deleteComment(commentId);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Comment deleted successfully", response.getBody());
//    }
//
//    @Test
//    void deleteComment_notFound() {
//        UUID commentId = UUID.randomUUID();
//        when(commentService.commentDelete(commentId)).thenReturn(false);
//
//        ResponseEntity<String> response = commentController.deleteComment(commentId);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertEquals("Comment not found", response.getBody());
//    }
//
//    @Test
//    void modifyComment_successful() {
//        UUID commentId = UUID.randomUUID();
//        CommentModifyRequest request = new CommentModifyRequest("New content");
//
//        when(commentService.commentModify(commentId, request)).thenReturn(true);
//
//        ResponseEntity<String> response = commentController.modifyComment(commentId, request);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Comment modified successfully", response.getBody());
//    }
//
//    @Test
//    void modifyComment_notFound() {
//        UUID commentId = UUID.randomUUID();
//        CommentModifyRequest request = new CommentModifyRequest("New content");
//
//        when(commentService.commentModify(commentId, request)).thenReturn(false);
//
//        ResponseEntity<String> response = commentController.modifyComment(commentId, request);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertEquals("Comment not found", response.getBody());
//    }



}
