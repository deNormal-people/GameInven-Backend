package com.blackcow.blackcowgameinven.end2endtest;

import com.blackcow.blackcowgameinven.model.User;
import com.blackcow.blackcowgameinven.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(RestDocumentationExtension.class)
public class UserEndToEndTest {

    @Autowired
    private static MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentationContextProvider) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @Test
    @DisplayName("중복체크 - 성공 응답")
    public void 중복체크API_성공() throws Exception {
        String requestBody = "{\"username\": \"uniqueUser\"}";


        this.mockMvc.perform(post("/api/auth/dupl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())             //200 OK
                .andDo(document("Account duplicate check/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("중복체크 - 실패 응답")
    public void 중복체크API_실패() throws Exception {
        String requestBody = "{\"username\": \"duplicationUser\"}";

        userRepository.save(User.builder().username("duplicationUser").password("duplicationUser").build());

        this.mockMvc.perform(post("/api/auth/dupl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())             //409 conflict
                .andDo(document("Account duplicate check/failed",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("로그인 - 성공")
    public void 로그인_성공() throws Exception {
        String userName = "guest";

        String requestBody = """
                {
                    "username": "guest",
                    "password": "guest"
                }
                """;

        userRepository.save(User.builder().username(userName).password(userName).build());

        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())             //409 conflict
                .andDo(document("Account login/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

}
