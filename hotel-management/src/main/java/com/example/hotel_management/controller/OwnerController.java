package com.example.hotel_management.controller;

import com.example.hotel_management.model.*;
import com.example.hotel_management.repository.RoomRepository;
import com.example.hotel_management.repository.UserRepository;
import com.example.hotel_management.service.ComplaintService;
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
@RequestMapping("/owner")
public class OwnerController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/home")
    public String ownerHome() {
        return "owner/home";
    }
    @GetMapping("/viewComplaints")
    public String viewComplaints(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());
        List<Client> complaints = complaintService.getComplaintsByHotelId(user.getHotel().getId());
        model.addAttribute("complaints", complaints);
        return "owner/viewComplaints";
    }
    @GetMapping("/manageRooms")
    public String manageRoomsForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userService.findByUsername(username);

        List<Room> rooms = roomRepository.findByHotelId(user.getHotel().getId());

        model.addAttribute("newRoom", new Room());
        model.addAttribute("rooms", rooms);
        return "owner/manageRooms";
    }

    @PostMapping("/addRoom")
    public String addRoomSubmit(@ModelAttribute("room") Room room, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userService.findByUsername(username);

        room.setHotel(user.getHotel());
        room.setOccupied(false);
        room.setStatus(RoomStatus.AVAILABLE);

        try {
            roomService.addRoom(room, user.getHotel().getId());
            model.addAttribute("successMessage", "Room added successfully to Hotel: " + room.getHotel().getName());
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", "Error adding room: " + e.getMessage());
        }
        return "redirect:/owner/manageRooms";
    }

    @PostMapping("/updateRoomStatus/{roomId}")
    public String updateRoomStatus(@PathVariable("roomId") Long roomId, @RequestParam RoomStatus status, Model model) {
        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Invalid room ID: " + roomId));
            room.setStatus(status);
            roomRepository.save(room);
            model.addAttribute("successMessage", "Room status updated successfully.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating room status: " + e.getMessage());
        }
        return "redirect:/owner/manageRooms";
    }

    @PostMapping("/deleteRoom/{roomId}")
    public String deleteRoom(@PathVariable("roomId") Long roomId, Model model) {
        try {
            roomRepository.deleteById(roomId);
            model.addAttribute("message", "Room successfully deleted.");
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting the room: " + e.getMessage());
        }
        return "redirect:/owner/manageRooms";
    }

    @GetMapping("/addManager")
    public String addManagerForm(Model model) {
        model.addAttribute("manager", new User());
        return "owner/addManager";
    }

    @PostMapping("/addManager")
    public String addManagerSubmit(@ModelAttribute("manager") User manager, Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User owner = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("Owner not found"));

        try {
            userService.createManager(manager.getUsername(), manager.getPassword(), owner.getHotel(), manager.getName(), manager.getSurname(), manager.getTelephone(), manager.getAddress());
            model.addAttribute("successMessage", "Manager added successfully!");
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "owner/addManager";
    }

    @GetMapping("/viewManagers")
    public String viewManagers(Model model) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User owner = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Owner not found"));

            if (owner.getHotel() == null || owner.getHotel().getId() == null) {
                throw new IllegalStateException("Hotel information is missing for the owner.");
            }

            Long hotelId = owner.getHotel().getId();
            List<User> managers = userRepository.findByHotelIdAndRoleRoleName(hotelId, "ROLE_MANAGER");
            model.addAttribute("managers", managers);
            return "owner/viewManagers";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error retrieving manager list: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/manageUsers")
    public String manageUsersForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User owner = userService.findByUsername(username);

        List<User> users = userService.getUsersByHotelId(owner.getHotel().getId());
        model.addAttribute("users", users);
        return "owner/manageUsers";
    }

    @PostMapping("/updateUserStatus")
    public String updateUserStatus(@RequestParam("userId") Long userId, @RequestParam("active") boolean active, Model model) {
        try {
            userService.setUserActiveStatus(userId, active);
            model.addAttribute("successMessage", "User status updated successfully.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating user status: " + e.getMessage());
        }
        return "redirect:/owner/manageUsers";
    }
}
