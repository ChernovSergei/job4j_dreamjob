package ru.job4j.dreamjob.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DuplicateKeyException;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegistrationPage() {
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        userService.save(user);
        return "redirect:/vacancies";
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleIllegalArgument(DuplicateKeyException e) {
        return ResponseEntity.status(500).body(e.getMessage());
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "user/login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model) {
        var userOptional = userService.findByEmailAndPassword(user.getEmail(), user.getPassword());
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Email or password are not correct");
            return "user/login";
        }
        return "redirect:/vacancies";
    }
}
