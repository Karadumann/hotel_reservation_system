package com.example.hotel_management.repository;

import com.example.hotel_management.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    Room findByRoomNumberAndHotelId(String roomNumber, Integer hotelId);
    List<Room> findByHotelId(Integer hotelId);
    List<Room> findByHotelName(String hotelName);
    boolean existsByHotelIdAndRoomNumber(Integer hotelId, String roomNumber);
}
