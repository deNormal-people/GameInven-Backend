package com.blackcow.blackcowgameinven.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Controller
@RequestMapping("/docs")
public class Docs {

    @GetMapping("/blackcow-api")
    public String main(){
        return "docs/restful API Manual";
    }

}
