package com.example.hotel_management.service;

import com.example.hotel_management.model.Hotel;
import com.example.hotel_management.model.Room;
import com.example.hotel_management.model.RoomStatus;
import com.example.hotel_management.repository.HotelRepository;
import com.example.hotel_management.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Transactional  // İşlemi transactional yapın
    public Room addRoom(Room room, Hotel hotel) {
        if (hotel.getId() == null) {
            hotel = hotelRepository.save(hotel);
        }
        room.setHotel(hotel);
        return roomRepository.save(room);
    }

    @Transactional
    public Room addRoom(Room room, Integer hotelId) {
        // Otel id ve oda numarasına göre varlık kontrolü
        if (roomRepository.existsByHotelIdAndRoomNumber(hotelId, room.getRoomNumber())) {
            throw new IllegalStateException("Room already exists in this hotel with the same number.");
        }

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));

        room.setHotel(hotel);
        return roomRepository.save(room);
    }

    public List<Room> getRoomsByHotelId(Integer hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    @Transactional
    public Room updateRoomStatus(Integer roomId, RoomStatus status) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
        room.setStatus(status);
        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Transactional
    public void deleteRoom(Integer roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
        roomRepository.delete(room);
    }

    @Transactional
    public Room updateRoom(Room updatedRoom) {
        Room existingRoom = roomRepository.findById(updatedRoom.getId())
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + updatedRoom.getId()));
        existingRoom.setRoomNumber(updatedRoom.getRoomNumber());
        existingRoom.setRoomCategory(updatedRoom.getRoomCategory());
        existingRoom.setStatus(updatedRoom.getStatus());
        existingRoom.setOccupied(updatedRoom.isOccupied());
        return roomRepository.save(existingRoom);
    }

    public Room getRoomById(Integer roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
    }
}
