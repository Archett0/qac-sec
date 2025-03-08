package com.team12.question;

import com.team12.clients.qna.question.dto.QuestionCreateRequest;
import com.team12.question.review.ContentReviewChain;
import com.team12.question.review.LengthChecker;
import com.team12.question.review.ProfanityFilter;
import com.team12.question.review.SpamChecker;
import com.team12.user.entity.Auth0User;
import com.team12.user.service.Auth0Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {

    private final ContentReviewChain contentReviewChain;
    private final QuestionRepository questionRepository;

    private final Auth0Service auth0Service;
    private final RestTemplate restTemplate;
    private final String auth0Domain;
    private static final String urlPrefix = "https://";

    public QuestionService(QuestionRepository questionRepository, Auth0Service auth0Service,@Value("${auth0.domain}") String auth0Domain) {
        this.auth0Service = auth0Service;
        this.auth0Domain = auth0Domain;
        this.restTemplate = new RestTemplate();
        this.questionRepository = questionRepository;
        // Set content review chain
        this.contentReviewChain = new ContentReviewChain();
        contentReviewChain.addHandler(new ProfanityFilter());
        contentReviewChain.addHandler(new LengthChecker());
        contentReviewChain.addHandler(new SpamChecker());
    }

    // Add new Question
    public Question addQuestion(QuestionCreateRequest request) {
        String url = urlPrefix + auth0Domain + "/api/v2/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth0Service.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Auth0User[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Auth0User[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            if (contentReviewChain.review(request.content())) {
                return questionRepository.save(
                        Question.builder()
                                .title(request.title())
                                .content(request.content())
                                .ownerId(request.ownerId())
                                .createdAt(LocalDateTime.now())
                                .build()
                );
            }
            else {
                return new Question();
            }
        }
        return null;
    }

    // Find Question by ID
    public Question getQuestionById(UUID id) {
        String url = urlPrefix + auth0Domain + "/api/v2/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth0Service.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Auth0User[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Auth0User[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return questionRepository.findById(id).orElse(null);
        }
        return null;
    }

    // Delete Question
    public void deleteQuestion(UUID id) {
        String url = urlPrefix + auth0Domain + "/api/v2/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth0Service.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Auth0User[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Auth0User[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            questionRepository.deleteById(id);
        }
    }

    // Update Question
    public Question updateQuestion(Question question) {
        String url = urlPrefix + auth0Domain + "/api/v2/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth0Service.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Auth0User[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Auth0User[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return questionRepository.save(question);
        }
        return null;
    }

    // Find Question by keyword
    public List<Question> searchQuestions(String keyword) {
        String url = urlPrefix + auth0Domain + "/api/v2/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth0Service.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Auth0User[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Auth0User[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return questionRepository.searchByKeyword(keyword);
        }
        return null;
    }

    // Get all Questions
    public List<Question> getAllQuestions() {
        String url = urlPrefix + auth0Domain + "/api/v2/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth0Service.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Auth0User[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Auth0User[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return questionRepository.findAll();
        }
        return null;
    }

    // Find Questions by Owner ID
    public List<Question> getQuestionsByOwnerId(UUID ownerId) {
        String url = urlPrefix + auth0Domain + "/api/v2/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth0Service.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Auth0User[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Auth0User[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return questionRepository.findByOwnerId(ownerId);
        }
        return null;
    }
}
