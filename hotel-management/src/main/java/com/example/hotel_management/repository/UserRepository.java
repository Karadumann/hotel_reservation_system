package com.example.hotel_management.repository;

import com.example.hotel_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByHotelId(Long hotelId);
    List<User> findByRoleRoleName(String roleName);
    List<User> findByHotelIdAndRoleRoleName(Long hotelId, String roleName);
}
