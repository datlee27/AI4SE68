package com.example.mealplanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }
    
    @GetMapping("/meals")
    public String meals(Model model) {
        return "meals";
    }
    
    @GetMapping("/nutrition")
    public String nutrition(Model model) {
        return "nutrition";
    }
    
    @GetMapping("/shopping-list")
    public String shoppingList(Model model) {
        return "shopping-list";
    }
}