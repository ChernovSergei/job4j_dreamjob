package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import ru.job4j.dreamjob.model.User;

@Controller
@SuppressWarnings("unused")
public class IndexController {
    @GetMapping({"/", "/index"})
    public String getIndex() {
        return "index";
    }
}
