package com.example.hotel_management.service;

import com.example.hotel_management.DTO.RoomDTO;
import com.example.hotel_management.model.Hotel;
import com.example.hotel_management.model.Room;
import com.example.hotel_management.model.RoomStatus;
import com.example.hotel_management.repository.HotelRepository;
import com.example.hotel_management.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Transactional
    public Room addRoom(Room room, Long hotelId) {
        if (roomRepository.existsByHotelIdAndRoomNumber(hotelId, room.getRoomNumber())) {
            throw new IllegalStateException("Room already exists in this hotel with the same number.");
        }

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));

        room.setHotel(hotel);
        return roomRepository.save(room);
    }

    public List<RoomDTO> getRoomsByHotelId(Long hotelId) {
        return roomRepository.findByHotelId(hotelId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private RoomDTO convertToDto(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRoomCategory(room.getRoomCategory());
        dto.setStatus(room.getStatus());
        return dto;
    }

    @Transactional
    public Room updateRoomStatus(Long roomId, RoomStatus status) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
        room.setStatus(status);
        return roomRepository.save(room);
    }

    public List<Room> getRoomsByHotelIdAsEntity(Long hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRoom(Long roomId) {
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
}
