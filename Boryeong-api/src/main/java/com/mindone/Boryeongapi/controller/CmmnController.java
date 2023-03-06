package com.mindone.Boryeongapi.controller;

import com.mindone.Boryeongapi.repository.hmi.HmiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CmmnController {

    @Autowired
    private HmiRepository cmmnRepository;

    @RequestMapping(value = "/")
    public String applicationStart() {
        return "test";
    }
}
