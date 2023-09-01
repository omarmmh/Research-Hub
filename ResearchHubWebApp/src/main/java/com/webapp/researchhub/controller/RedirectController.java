package com.webapp.researchhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RedirectController {

    @GetMapping("/")
    public ModelAndView redirectToHomePage() {
        return new ModelAndView("forward:/site/");
    }
}
