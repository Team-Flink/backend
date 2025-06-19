package spring.flink.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
