package com.blackcow.blackcowgameinven.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.blackcow.blackcowgameinven.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "SELECT * FROM USER WHERE username=:username", nativeQuery = true)
    List<User> findAllByUsername(@Param("username") String username);

    User findByUsername(String username);

    boolean existsUserByUsername(String username);
}
