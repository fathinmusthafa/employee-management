package com.example.assessment.viewcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewTitles {

    @GetMapping("/titles")
    public String titles() {
        return "titles";
    }
}
