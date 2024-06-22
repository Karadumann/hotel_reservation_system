package com.example.hotel_management.repository;

import com.example.hotel_management.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByHotelId(Long hotelId);
}
