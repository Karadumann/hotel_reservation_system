package com.example.hotel_management.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = request.getRemoteUser();
        model.addAttribute("username", username);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))) {
            return "redirect:/admin/home";
        }
        else if (authorities.contains(new SimpleGrantedAuthority("ROLE_OWNER"))) {
            return "redirect:/owner/home";
        }
        else if (authorities.contains(new SimpleGrantedAuthority("ROLE_MANAGER"))) {
            return "redirect:/manager/home";
        }
        else if (authorities.contains(new SimpleGrantedAuthority("ROLE_RECEPTIONIST"))) {
            return "redirect:/receptionist/home";
        }
        return "login";
    }
}
