package com.blackcow.blackcowgameinven.UserTest;

import com.blackcow.blackcowgameinven.model.User;
import com.blackcow.blackcowgameinven.repository.UserRepository;
import com.blackcow.blackcowgameinven.service.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@Rollback(value = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .username("asdf")
                .password("asdf")
                .role("ADMIN")
                .phone("123456789")
                .email("asdf@gmail.com")
                .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("사용자 조회")
    void 사용자조회(){

        List<User> userList = userRepository.findAllByUsername("asdf");

        assertEquals(1, userList.size());
    }

    @Test
    @DisplayName("중복체크")
    void 중복체크(){
        //중복됨
        assertEquals(false, authorizationService.duplicationCheck("asdf"));
    }

}
