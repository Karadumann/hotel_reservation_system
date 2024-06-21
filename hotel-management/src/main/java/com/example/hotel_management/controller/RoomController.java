package com.example.hotel_management.controller;

import com.example.hotel_management.model.Room;
import com.example.hotel_management.model.RoomStatus;
import com.example.hotel_management.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/{roomId}/status")
    public Room updateRoomStatus(@PathVariable Integer roomId, @RequestParam("status") String status) {
        RoomStatus roomStatus = RoomStatus.valueOf(status.toUpperCase());
        return roomService.updateRoomStatus(Long.valueOf(roomId), roomStatus);
    }
}
