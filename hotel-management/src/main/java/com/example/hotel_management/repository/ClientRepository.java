package com.example.hotel_management.repository;

import com.example.hotel_management.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByHotelId(Long hotelId);
}
