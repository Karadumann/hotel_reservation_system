package com.example.hotel_management.controller;

import com.example.hotel_management.model.Hotel;
import com.example.hotel_management.model.Room;
import com.example.hotel_management.model.User;
import com.example.hotel_management.repository.HotelRepository;
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
@RequestMapping("/owner")
public class OwnerController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private HotelRepository hotelRepository;

    @GetMapping("/home")
    public String ownerHome() {
        return "owner/home";
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

            Long hotelId = owner.getHotel().getId().longValue();
            List<User> managers = userRepository.findByHotelIdAndRoleRoleName(hotelId, "ROLE_MANAGER");
            model.addAttribute("managers", managers);
            return "owner/viewManagers";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error retrieving manager list: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/manageRooms")
    public String manageRoomsForm(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User owner = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        List<Room> rooms = roomService.getRoomsByHotelId(owner.getHotel().getId());
        model.addAttribute("newRoom", new Room());
        model.addAttribute("rooms", rooms);
        return "owner/manageRooms";
    }

    @PostMapping("/manageRooms")
    public String addRoomSubmit(@ModelAttribute("room") Room room, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Hotel hotel = hotelRepository.findById(owner.getHotel().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid hotel ID"));
        room.setHotel(hotel);
        room.setOccupied(false);

        if (!roomRepository.existsByHotelIdAndRoomNumber(hotel.getId(), room.getRoomNumber())) {
            roomRepository.save(room);
            model.addAttribute("successMessage", "Room added successfully to your hotel.");
        } else {
            model.addAttribute("errorMessage", "A room with the same number already exists in this hotel.");
        }
        return "redirect:/owner/manageRooms";
    }

    @GetMapping("/deleteRoom/{roomId}")
    public String deleteRoom(@PathVariable("roomId") Integer roomId, Model model) {
        roomRepository.deleteById(roomId);
        model.addAttribute("message", "Room successfully deleted.");
        return "redirect:/owner/manageRooms";
    }
}
