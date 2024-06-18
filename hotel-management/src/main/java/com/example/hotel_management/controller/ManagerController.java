package com.example.hotel_management.controller;

import com.example.hotel_management.model.Room;
import com.example.hotel_management.model.User;
import com.example.hotel_management.repository.UserRepository;
import com.example.hotel_management.service.RoomService;
import com.example.hotel_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/home")
    public String managerHome() {
        return "manager/home";
    }

    @GetMapping("/addReceptionist")
    public String addReceptionistForm(Model model) {
        model.addAttribute("receptionist", new User());
        return "manager/addReceptionist";
    }

    @PostMapping("/addReceptionist")
    public String addReceptionistSubmit(@ModelAttribute("receptionist") User receptionist, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User manager = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        try {
            userService.createReceptionist(receptionist.getUsername(), receptionist.getPassword(), manager.getHotel(), receptionist.getName(), receptionist.getSurname(), receptionist.getTelephone(), receptionist.getAddress());
            model.addAttribute("successMessage", "Receptionist added successfully!");
            return "manager/addReceptionist";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding receptionist: " + e.getMessage());
            return "manager/addReceptionist";
        }
    }

    @GetMapping("/viewReceptionists")
    public String viewReceptionists(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User manager = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Manager not found"));

            if (manager.getHotel() == null || manager.getHotel().getId() == null) {
                throw new IllegalStateException("Hotel information is missing for the manager.");
            }

            Long hotelId = manager.getHotel().getId().longValue();
            List<User> receptionists = userRepository.findByHotelIdAndRoleRoleName(hotelId, "ROLE_RECEPTIONIST");
            model.addAttribute("receptionists", receptionists);
            return "manager/viewReceptionists";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error retrieving receptionist list: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/details/{username}")
    public User getUserDetails(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    @GetMapping("/manageRooms")
    public String manageRoomsForm(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User manager = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        List<Room> rooms = roomService.getRoomsByHotelId(manager.getHotel().getId());
        model.addAttribute("newRoom", new Room());
        model.addAttribute("rooms", rooms);
        return "manager/manageRooms";
    }
}
