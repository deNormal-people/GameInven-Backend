package com.blackcow.blackcowgameinven.end2endtest;

import com.blackcow.blackcowgameinven.dto.UserDTO;
import com.blackcow.blackcowgameinven.model.User;
import com.blackcow.blackcowgameinven.repository.UserRepository;
import com.blackcow.blackcowgameinven.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.SQLException;

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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)         //클래스단위로 테스트 인스턴스 유지
public class UserEndToEndTest {

    @Autowired
    private static MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;


    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentationContextProvider) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @BeforeAll
    void setUp() throws SQLException {
        String userName = "test";

        userService.createuser(new UserDTO(userName, userName, "",""));
    }

    @Test
    @DisplayName("회원가입 - 성공")
    public void 회원가입_성공() throws Exception {

        String requestBody = """
                {
                    "username": "guest",
                    "password": "guest",
                    "email": "guest@test.com",
                    "phone": "123456789"
                }
                """;

        this.mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andDo(document("Sign up/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("회원가입 - 실패")
    public void 회원가입_실패() throws Exception {

        String requestBody = """
                {
                    "username": "guest",
                    "email": "guest@test.com",
                    "phone": "123456789"
                }
                """;

        this.mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(document("Sign up/failed",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
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

        String requestBody = """
                {
                    "username": "test",
                    "password": "test"
                }
                """;

        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())             //409 conflict
                .andDo(document("Account login/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("로그인 - 실패")
    public void 로그인_실패() throws Exception {

        String requestBody = """
                {
                    "username": "guest",
                    "password": "guest"
                }
                """;

        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())             //401 UnAuthorized
                .andDo(document("Account login/failed",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("토큰 갱신 - 성공")
    public void Access_Token재발행_성공() throws Exception {
        String loginRequest = """
            {
                "username": "test",
                "password": "test"
            }
            """;

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        // 3. 로그인 응답에서 Refresh Token 추출
        String jsonResponse = loginResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        String refreshToken = jsonNode.get("refreshToken").asText();

        // 4. 토큰 갱신 요청
        String requestBody = String.format("""
            {
                "refreshToken": "%s"
            }
            """, refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()) // 200 OK 예상
                .andDo(document("Token refresh/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("토큰 갱신 - 실패")
    public void Access_Token재발행_실패() throws Exception {

        String requestBody = """
                {
                    "refreshToken": "eyJhbGcJIUzI1NiJ9.eyJzdWIiOiJndWVzdCIsImlhdCI6MTc0MTU4MzkzNywiZXhwIjoxNzQyMTg4NzM3LCJyb2xlcyI6WyJHVUVTVCJdfQ.U7wsvvhjgrjPuop1WCTAGI87jBxp6OQ9Agb2VNXOo3g"
                }
                """;

        this.mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())             //401 UnAuthorized
                .andDo(document("Token refresh/failed",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

}
