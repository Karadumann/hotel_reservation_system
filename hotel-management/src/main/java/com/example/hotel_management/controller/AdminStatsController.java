package com.example.hotel_management.controller;

import com.example.hotel_management.DTO.RoomDTO;
import com.example.hotel_management.model.Complaint;
import com.example.hotel_management.model.Room;
import com.example.hotel_management.model.RoomStatus;
import com.example.hotel_management.model.User;
import com.example.hotel_management.service.ComplaintService;
import com.example.hotel_management.service.RoomService;
import com.example.hotel_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminStatsController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @GetMapping("/complaints-stats")
    public Map<String, Object> getComplaintsStats() {
        List<Complaint> complaints = complaintService.getAllComplaints();
        Map<String, Long> complaintsByHotel = complaints.stream()
                .collect(Collectors.groupingBy(complaint -> complaint.getHotel().getName(), Collectors.counting()));

        List<String> labels = new ArrayList<>(complaintsByHotel.keySet());
        List<Long> values = new ArrayList<>(complaintsByHotel.values());

        return Map.of("labels", labels, "values", values);
    }

    @GetMapping("/room-status-stats")
    public Map<String, Object> getRoomStatusStats() {
        List<RoomDTO> rooms = roomService.getAllRooms();
        Map<String, Long> roomsByStatus = rooms.stream()
                .collect(Collectors.groupingBy(room -> room.getStatus().name(), Collectors.counting()));

        List<String> labels = new ArrayList<>(roomsByStatus.keySet());
        List<Long> values = new ArrayList<>(roomsByStatus.values());

        return Map.of("labels", labels, "values", values);
    }

    @GetMapping("/user-roles-stats")
    public Map<String, Object> getUserRolesStats() {
        List<User> users = userService.getAllUsers();
        Map<String, Long> usersByRole = users.stream()
                .collect(Collectors.groupingBy(user -> user.getRole().getRoleName(), Collectors.counting()));

        List<String> labels = new ArrayList<>(usersByRole.keySet());
        List<Long> values = new ArrayList<>(usersByRole.values());

        return Map.of("labels", labels, "values", values);
    }
}
