package com.example.hotel_management.service;

import com.example.hotel_management.model.Hotel;
import com.example.hotel_management.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Optional<Hotel> findById(Long id) {
        return hotelRepository.findById(id);
    }
    public Hotel saveHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }
}
