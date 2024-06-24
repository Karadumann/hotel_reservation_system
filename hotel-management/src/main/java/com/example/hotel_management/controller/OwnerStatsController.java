package com.example.hotel_management.controller;

import com.example.hotel_management.DTO.RoomDTO;
import com.example.hotel_management.model.Complaint;
import com.example.hotel_management.model.User;
import com.example.hotel_management.service.ComplaintService;
import com.example.hotel_management.service.RoomService;
import com.example.hotel_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/owner")
public class OwnerStatsController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @GetMapping("/complaints-stats")
    public Map<String, Object> getComplaintsStats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        List<Complaint> complaints = complaintService.getComplaintsByHotelId(user.getHotel().getId());

        Map<String, Long> complaintsByHotel = complaints.stream()
                .collect(Collectors.groupingBy(complaint -> complaint.getHotel().getName(), Collectors.counting()));

        List<String> labels = new ArrayList<>(complaintsByHotel.keySet());
        List<Long> values = new ArrayList<>(complaintsByHotel.values());

        return Map.of("labels", labels, "values", values);
    }

    @GetMapping("/room-status-stats")
    public Map<String, Object> getRoomStatusStats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        List<RoomDTO> rooms = roomService.getRoomsByHotelId(user.getHotel().getId());

        Map<String, Long> roomsByStatus = rooms.stream()
                .collect(Collectors.groupingBy(room -> room.getStatus().name(), Collectors.counting()));

        List<String> labels = new ArrayList<>(roomsByStatus.keySet());
        List<Long> values = new ArrayList<>(roomsByStatus.values());

        return Map.of("labels", labels, "values", values);
    }

    @GetMapping("/managers-stats")
    public Map<String, Object> getManagersStats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        List<User> managers = userService.getUsersByHotelIdAndRole(user.getHotel().getId(), "ROLE_MANAGER");

        Map<String, Long> managersByRole = managers.stream()
                .collect(Collectors.groupingBy(manager -> manager.getRole().getRoleName(), Collectors.counting()));

        List<String> labels = new ArrayList<>(managersByRole.keySet());
        List<Long> values = new ArrayList<>(managersByRole.values());

        return Map.of("labels", labels, "values", values);
    }
}
