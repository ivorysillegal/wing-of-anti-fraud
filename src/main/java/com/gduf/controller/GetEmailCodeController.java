package com.gduf.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GetEmailCodeController {

    @Autowired
    private JavaMailSender javaMailSender;


}
