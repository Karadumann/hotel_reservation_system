package com.example.hotel_management.service;

import com.example.hotel_management.model.Reservation;
import com.example.hotel_management.model.Room;
import com.example.hotel_management.model.RoomStatus;
import com.example.hotel_management.repository.ReservationRepository;
import com.example.hotel_management.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    public Reservation createReservation(Reservation reservation) {
        // Check for double booking
        List<Reservation> existingReservations = reservationRepository.findByRoomNumberAndDates(
                reservation.getRoomNumber(), reservation.getStartDate(), reservation.getEndDate());

        if (!existingReservations.isEmpty()) {
            throw new IllegalArgumentException("The room is already booked for the selected dates.");
        }

        Reservation savedReservation = reservationRepository.save(reservation);

        Room room = roomRepository.findByRoomNumberAndHotelId(reservation.getRoomNumber(), reservation.getHotel().getId());
        if (room != null) {
            room.setOccupied(true);
            room.setStatus(RoomStatus.OCCUPIED);
            room.setReservationNumber(reservation.getReservationNumber());
            roomRepository.save(room);
        }

        return savedReservation;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public void cancelReservation(String reservationNumber) {
        Reservation reservation = reservationRepository.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> new RuntimeException("Reservation not found with number: " + reservationNumber));

        Room room = roomRepository.findByRoomNumberAndHotelId(reservation.getRoomNumber(), reservation.getHotel().getId());
        if (room != null) {
            room.setOccupied(false);
            room.setStatus(RoomStatus.AVAILABLE);
            room.setReservationNumber(null);
            roomRepository.save(room);
        }

        reservation.setCancelled(true);
        reservationRepository.save(reservation);
    }

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByRoomNumber(String roomNumber) {
        return reservationRepository.findByRoomNumber(roomNumber);
    }

    public List<Reservation> getReservationsByHotelId(Long hotelId) {
        return reservationRepository.findByHotelId(hotelId);
    }
}
