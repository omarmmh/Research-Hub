package com.webapp.researchhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {
    @GetMapping("/demo")
    public String getDemoPage() {
        return "demo/view";
    }
}
