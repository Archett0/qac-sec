package com.team12.question;

import com.team12.clients.qna.question.dto.QuestionCreateRequest;
import com.team12.question.review.ContentReviewChain;
import com.team12.question.review.LengthChecker;
import com.team12.question.review.ProfanityFilter;
import com.team12.question.review.SpamChecker;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {

    private final ContentReviewChain contentReviewChain;
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
        // Set content review chain
        this.contentReviewChain = new ContentReviewChain();
        contentReviewChain.addHandler(new ProfanityFilter());
        contentReviewChain.addHandler(new LengthChecker());
        contentReviewChain.addHandler(new SpamChecker());
    }

    // Add new Question
    public Question addQuestion(QuestionCreateRequest request) {
        if (contentReviewChain.review(request.content())) {
            return questionRepository.save(
                    Question.builder()
                            .title(sanitizeInput(request.title()))
                            .content(sanitizeInput(request.content()))
                            .ownerId(request.ownerId())
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        }
        else {
            return new Question();
        }
    }

    // Find Question by ID
    public Question getQuestionById(UUID id) {
        return questionRepository.findById(id).orElse(null);
    }

    // Delete Question
    public void deleteQuestion(UUID id) {
        questionRepository.deleteById(id);
    }

    // Update Question
    public Question updateQuestion(Question question) {
        return questionRepository.save(question);
    }

    // Find Question by keyword
    public List<Question> searchQuestions(String keyword) {
        return questionRepository.searchByKeyword(keyword);
    }

    // Get all Questions
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // Find Questions by Owner ID
    public List<Question> getQuestionsByOwnerId(UUID ownerId) {
        return questionRepository.findByOwnerId(ownerId);
    }

    public String sanitizeInput(String input) {
        return input.replace("<", "&lt;").replace(">", "&gt;").trim();
    }
}
