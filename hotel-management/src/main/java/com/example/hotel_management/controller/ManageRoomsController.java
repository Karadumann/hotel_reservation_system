package com.example.hotel_management.controller;

import com.example.hotel_management.model.Room;
import com.example.hotel_management.model.RoomStatus;
import com.example.hotel_management.model.User;
import com.example.hotel_management.repository.RoomRepository;
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
@RequestMapping("/manageRooms")
public class ManageRoomsController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String manageRoomsForm(@RequestParam(required = false) String hotelName, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userService.findByUsername(username);

        List<Room> rooms;

        if (user.getRole().getRoleName().equals("ROLE_ADMIN")) {
            rooms = roomRepository.findAll();
        } else {
            rooms = roomRepository.findByHotelId(user.getHotel().getId());
        }

        model.addAttribute("newRoom", new Room());
        model.addAttribute("rooms", rooms);
        return "manageRooms";
    }

    @PostMapping("/addRoom")
    public String addRoomSubmit(@ModelAttribute("room") Room room, @RequestParam("hotelId") Integer hotelId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userService.findByUsername(username);

        if (!user.getRole().getRoleName().equals("ROLE_ADMIN") && !user.getHotel().getId().equals(hotelId)) {
            throw new IllegalStateException("You do not have permission to add a room to this hotel.");
        }

        try {
            room.setHotel(user.getHotel());
            roomService.addRoom(room, hotelId);
            model.addAttribute("successMessage", "Room added successfully to Hotel: " + room.getHotel().getName());
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/manageRooms";
    }

    @PostMapping("/updateRoomStatus/{roomId}")
    public String updateRoomStatus(@PathVariable("roomId") Integer roomId, @RequestParam RoomStatus status, Model model) {
        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Invalid room ID: " + roomId));
            room.setStatus(status);
            roomRepository.save(room);
            model.addAttribute("successMessage", "Room status updated successfully.");
        } catch (Exception e) {
            model.addAttribute("error", "Error updating room status: " + e.getMessage());
        }
        return "redirect:/manageRooms";
    }

    @GetMapping("/deleteRoom/{roomId}")
    public String deleteRoom(@PathVariable("roomId") Integer roomId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userService.findByUsername(username);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room ID: " + roomId));

        if (!user.getRole().getRoleName().equals("ROLE_ADMIN") && !room.getHotel().getId().equals(user.getHotel().getId())) {
            throw new IllegalStateException("You do not have permission to delete this room.");
        }

        try {
            roomRepository.deleteById(roomId);
            model.addAttribute("message", "Room successfully deleted.");
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting the room: " + e.getMessage());
        }
        return "redirect:/manageRooms";
    }
}
