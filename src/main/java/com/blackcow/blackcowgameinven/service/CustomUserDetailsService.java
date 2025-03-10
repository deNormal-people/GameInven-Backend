package com.blackcow.blackcowgameinven.service;

import com.blackcow.blackcowgameinven.model.User;
import com.blackcow.blackcowgameinven.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 사용자 권한 포함 로직 추가
     * @param username the username identifying the user whose data is required.
     * @return  사용자 권한을 포함한 사용자 정보
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) new UsernameNotFoundException("User not found with username: " + username);

        String roles = user.getRole();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(roles))     //authories를 써야 "ROLE_"이 안붙음
                .build();
    }
}
