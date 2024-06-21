package com.example.hotel_management.controller;

import com.example.hotel_management.model.Reservation;
import com.example.hotel_management.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/roomReservations")
    public List<Reservation> getRoomReservations(@RequestParam String roomNumber) {
        return reservationService.getReservationsByRoomNumber(roomNumber);
    }
}
