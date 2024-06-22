package com.example.hotel_management.repository;

import com.example.hotel_management.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationNumber(String reservationNumber);

    @Query("SELECT r FROM Reservation r WHERE r.roomNumber = :roomNumber AND r.startDate <= :endDate AND r.endDate >= :startDate")
    List<Reservation> findByRoomNumberAndDates(@Param("roomNumber") String roomNumber, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<Reservation> findByRoomNumber(String roomNumber);

    List<Reservation> findByHotelId(Long hotelId);

}
