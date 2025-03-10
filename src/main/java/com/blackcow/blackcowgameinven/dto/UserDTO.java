package com.blackcow.blackcowgameinven.dto;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {

    @Nonnull
    private String username;

    private String password;

    private String email;

    private String phone;

}
