package com.example.hotel_management.service;

import com.example.hotel_management.DTO.RoomDTO;
import com.example.hotel_management.model.Hotel;
import com.example.hotel_management.model.Room;
import com.example.hotel_management.model.RoomStatus;
import com.example.hotel_management.repository.HotelRepository;
import com.example.hotel_management.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddRoom() {
        Room room = new Room();
        room.setRoomNumber("101");

        Hotel hotel = new Hotel();
        hotel.setId(1L);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(roomRepository.existsByHotelIdAndRoomNumber(1L, "101")).thenReturn(false);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Room addedRoom = roomService.addRoom(room, 1L);

        assertNotNull(addedRoom);
        assertEquals("101", addedRoom.getRoomNumber());
        verify(roomRepository, times(1)).save(any(Room.class));
    }


    @Test
    void testUpdateRoomStatus() {
        Room room = new Room();
        room.setId(1L);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Room updatedRoom = roomService.updateRoomStatus(1L, RoomStatus.OCCUPIED);

        assertNotNull(updatedRoom);
        assertEquals(RoomStatus.OCCUPIED, updatedRoom.getStatus());
        verify(roomRepository, times(1)).save(room);
    }
}
