package com.team12;

import com.team12.clients.qna.question.dto.QuestionDto;
import com.team12.clients.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchFacade searchFacade;

    private List<QuestionDto> mockQuestions;
    private List<UserDto> mockUsers;

    @BeforeEach
    void setUp() {
        // Mock questions data
        mockQuestions = Arrays.asList(
                new QuestionDto(UUID.randomUUID(), "First Question", "This is the first question content", UUID.randomUUID()),
                new QuestionDto(UUID.randomUUID(), "Second Question", "This is the second question content", UUID.randomUUID())
        );

        // Mock users data
        mockUsers = Arrays.asList(
                new UserDto(UUID.randomUUID(), "johndoe", "john@example.com"),
                new UserDto(UUID.randomUUID(), "janedoe", "jane@example.com")
        );
    }

    @Test
    void testSearchQuestions() throws Exception {
        // Mock searchService to return mockQuestions when searchQuestionsByKeyword is called
        Mockito.when(searchFacade.searchQuestionsByKeyword(anyString())).thenReturn(mockQuestions);

        // Perform a GET request to /search/questions with a keyword parameter
        mockMvc.perform(get("/api/v1/search/questions")
                        .param("keyword", "question")
                        .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP 200 OK status
                .andExpect(status().isOk())
                // Verify that the response is JSON and contains the expected data
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("First Question"))
                .andExpect(jsonPath("$[0].content").value("This is the first question content"))
                .andExpect(jsonPath("$[1].title").value("Second Question"))
                .andExpect(jsonPath("$[1].content").value("This is the second question content"));
    }

    @Test
    void testSearchUsers() throws Exception {
        // Mock searchService to return mockUsers when searchUsersByUsername is called
        Mockito.when(searchFacade.searchUsersByUsername(anyString())).thenReturn(mockUsers);

        // Perform a GET request to /search/users with a keyword parameter
        mockMvc.perform(get("/api/v1/search/users")
                        .param("keyword", "doe")
                        .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP 200 OK status
                .andExpect(status().isOk())
                // Verify that the response is JSON and contains the expected data
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username").value("johndoe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].username").value("janedoe"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));
    }
}
