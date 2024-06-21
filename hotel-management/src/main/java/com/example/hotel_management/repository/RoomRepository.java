package com.example.hotel_management.repository;

import com.example.hotel_management.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findByRoomNumberAndHotelId(String roomNumber, Long hotelId);
    List<Room> findByHotelId(Long hotelId);
    List<Room> findByHotelName(String hotelName);
    boolean existsByHotelIdAndRoomNumber(Long hotelId, String roomNumber);
}
