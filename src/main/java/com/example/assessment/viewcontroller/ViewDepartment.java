package com.example.assessment.viewcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewDepartment {

    @GetMapping("/departments")
    public String departments() {
        return "departments";
    }

    @GetMapping("/dept-emp")
    public String deptEmp() {
        return "dept-emp";
    }

    @GetMapping("/dept-manager")
    public String deptManger() {
        return "dept-manager";
    }
}
