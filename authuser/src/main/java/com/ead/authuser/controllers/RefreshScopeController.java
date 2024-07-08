package com.ead.authuser.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping("/refreshscope")
public class RefreshScopeController {

    @Value("${authserver.refreshscope.name}")
    private String name;

    @GetMapping
    public String refreshscope(){
        return this.name;
    }

}
