package com.webapp.researchhub.controller;


import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class AuthenticationController {
    @Autowired
    private UserRepository repo;

    @RequestMapping("/login-user")
    public String login(@RequestParam(name = "error", defaultValue = "false") String formHasErrors, Model model) {
        model.addAttribute("formHasErrors", Boolean.parseBoolean(formHasErrors));
        return "security/login-form";
    }

    @RequestMapping("/login-success")
    public String success() {
        return "redirect:/myProfile";
    }

    @GetMapping("/search")
    public String search(){
        return "site/search";
    }
}
