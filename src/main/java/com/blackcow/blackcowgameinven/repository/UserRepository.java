package com.blackcow.blackcowgameinven.repository;

import com.blackcow.blackcowgameinven.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "SELECT * FROM USER_ACCOUNT WHERE username=:username", nativeQuery = true)
    List<User> findAllByUsername(@Param("username") String username);

    User findByUsername(String username);

    boolean existsUserByUsername(String username);
}
