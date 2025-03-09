package com.blackcow.blackcowgameinven.integrationtest;

import com.blackcow.blackcowgameinven.model.User;
import com.blackcow.blackcowgameinven.repository.UserRepository;
import com.blackcow.blackcowgameinven.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 중복체크_존재하지_않는_유저(){

        String userName = "asdf";

        boolean result = userService.duplicationCheck(userName);

        assertThat(result).isFalse();
    }

    @Test
    public void 중복체크_존재하는_유저(){

        String userName = "asdf";

        userRepository.save(User.builder().username(userName).password(userName).build());

        boolean result = userService.duplicationCheck(userName);

        assertThat(result).isTrue();
    }

}
