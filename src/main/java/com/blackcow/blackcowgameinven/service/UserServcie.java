package com.blackcow.blackcowgameinven.service;

import com.blackcow.blackcowgameinven.dto.UserDTO;
import com.blackcow.blackcowgameinven.model.User;
import com.blackcow.blackcowgameinven.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class UserServcie {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String emailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$";
    private final String phonePattern = "^010-\\d{3,4}-\\d{4}$";

    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
        }
        return null;
    }

    /***
     * 아이디 중복체크
     */
    public boolean duplicationCheck(String username){
        return !userRepository.existsUserByUsername(username);
    }

    public void createuser(UserDTO userDTO) throws SQLException{
        //2차 중복검증
        if(!duplicationCheck(userDTO.getUsername())){
            throw new RuntimeException("중복된 계정입니다.");
        }

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        User newUser = User.builder()
                            .username(userDTO.getUsername())
                            .password(encodedPassword)
                            .email(userDTO.getEmail())
                            .phone(userDTO.getPhone())
                            .build();


        userRepository.save(newUser);
    }

    /**
     * 이메일 형식에 맞는지 검사
     * @param username
     * @return
     */
    public boolean isValidEmail(String username) {
        return username.matches(emailPattern);
    }

    /**
     * 핸드폰 번호 형식에 맞는지 검사(하이폰 포함)
     * @param phone
     * @return
     */
    public boolean isValidPhone(String phone) {
        return phone.matches(phonePattern);
    }
}
