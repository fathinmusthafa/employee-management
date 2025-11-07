package com.example.assessment.viewcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewEmployee {

    @GetMapping("/")
    public String index() {
        return "redirect:/employees";
    }

    @GetMapping("/employees")
    public String employees() {
        return "employees";
    }

//    @GetMapping("/departments")
//    public String departments() {
//        return "departments";
//    }
//
//    @GetMapping("/salaries")
//    public String salaries() {
//        return "salaries";
//    }
//
//    @GetMapping("/titles")
//    public String titles() {
//        return "titles";
//    }
//
//    @GetMapping("/dept-emp")
//    public String deptEmp() {
//        return "dept-emp";
//    }
//
//    @GetMapping("/dept-manager")
//    public String deptManager() {
//        return "dept-manager";
//    }
}
