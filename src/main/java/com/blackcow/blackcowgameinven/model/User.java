package com.blackcow.blackcowgameinven.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "USER")
public class User {

    @Id
    @Column(name = "seq", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)         //auto increment
    private int seq;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column
    private String email;

    @Column
    private String phone;

    @Column(name = "peristalsis_sns")
    private String peristalsisSNS;

    @Column(name = "account_type")
    private int accountType;
}
