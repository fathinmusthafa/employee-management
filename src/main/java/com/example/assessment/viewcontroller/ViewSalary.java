package com.example.assessment.viewcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewSalary {

    @GetMapping("/salaries")
    public String salaries() {
        return "salaries";
    }
}
