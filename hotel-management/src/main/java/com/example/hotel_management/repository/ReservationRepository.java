package com.example.hotel_management.repository;

import com.example.hotel_management.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationNumber(String reservationNumber);
}
