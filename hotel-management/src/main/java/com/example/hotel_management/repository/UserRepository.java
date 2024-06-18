package com.example.hotel_management.repository;

import com.example.hotel_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByHotelId(Long hotelId);
    List<User> findByHotelIdAndRoleRoleName(Long hotelId, String roleName);
}
