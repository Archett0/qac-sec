package com.team12.answer;

import com.team12.clients.notification.NotificationClient;
import com.team12.clients.notification.dto.NotificationRequest;
import com.team12.question.Question;
import com.team12.question.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private AnswerService answerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAnswer() {
        Answer answer = new Answer();
        answer.setContent("Test answer content");
        answer.setQuestionId(UUID.randomUUID());
        Question question = new Question();
        question.setOwnerId(UUID.randomUUID());

        when(questionService.getQuestionById(answer.getQuestionId())).thenReturn(question);
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        Answer createdAnswer = answerService.createAnswer(answer);

        assertNotNull(createdAnswer);
        assertEquals("Test answer content", createdAnswer.getContent());
        verify(notificationClient, times(1)).sendNotification(any(NotificationRequest.class));
        verify(answerRepository, times(1)).save(any(Answer.class));
    }

    @Test
    void testGetAllAnswers() {
        List<Answer> answers = List.of(new Answer(), new Answer());
        when(answerRepository.findAll()).thenReturn(answers);

        List<Answer> result = answerService.getAllAnswers();

        assertEquals(2, result.size());
        verify(answerRepository, times(1)).findAll();
    }

    @Test
    void testGetAnswerById() {
        UUID answerId = UUID.randomUUID();
        Answer answer = new Answer();
        answer.setId(answerId);
        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));

        Optional<Answer> result = answerService.getAnswerById(answerId);

        assertTrue(result.isPresent());
        assertEquals(answerId, result.get().getId());
        verify(answerRepository, times(1)).findById(answerId);
    }

    @Test
    void testUpdateAnswer() {
        UUID answerId = UUID.randomUUID();
        Answer existingAnswer = new Answer();
        existingAnswer.setId(answerId);
        existingAnswer.setContent("Old content");

        Answer updatedAnswer = new Answer();
        updatedAnswer.setContent("New content");

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(existingAnswer));
        when(answerRepository.save(any(Answer.class))).thenReturn(existingAnswer);

        Answer result = answerService.updateAnswer(answerId, updatedAnswer);

        assertNotNull(result);
        assertEquals("New content", result.getContent());
        verify(answerRepository, times(1)).findById(answerId);
        verify(answerRepository, times(1)).save(existingAnswer);
    }

    @Test
    void testDeleteAnswer() {
        UUID answerId = UUID.randomUUID();

        doNothing().when(answerRepository).deleteById(answerId);

        answerService.deleteAnswer(answerId);

        verify(answerRepository, times(1)).deleteById(answerId);
    }

    @Test
    void testGetAnswersByQuestionId() {
        UUID questionId = UUID.randomUUID();
        List<Answer> answers = List.of(new Answer(), new Answer());

        when(answerRepository.findByQuestionId(questionId)).thenReturn(answers);

        List<Answer> result = answerService.getAnswersByQuestionId(questionId);

        assertEquals(2, result.size());
        verify(answerRepository, times(1)).findByQuestionId(questionId);
    }
}
