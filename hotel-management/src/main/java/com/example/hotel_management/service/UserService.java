package com.example.hotel_management.service;

import com.example.hotel_management.model.Hotel;
import com.example.hotel_management.model.User;
import com.example.hotel_management.model.UserRole;
import com.example.hotel_management.repository.UserRepository;
import com.example.hotel_management.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createOwner(String username, String password, Hotel hotel, String name, String surname, String telephone, String address) {
        return createUserWithRole(username, password, hotel, "ROLE_OWNER", name, surname, telephone, address);
    }

    public User createManager(String username, String password, Hotel hotel, String name, String surname, String telephone, String address) {
        return createUserWithRole(username, password, hotel, "ROLE_MANAGER", name, surname, telephone, address);
    }

    public User createReceptionist(String username, String password, Hotel hotel, String name, String surname, String telephone, String address) {
        return createUserWithRole(username, password, hotel, "ROLE_RECEPTIONIST", name, surname, telephone, address);
    }

    private User createUserWithRole(String username, String password, Hotel hotel, String roleName, String name, String surname, String telephone, String address) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("A user with this username already exists.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setHotel(hotel);
        user.setName(name);
        user.setSurname(surname);
        user.setTelephone(telephone);
        user.setAddress(address);

        UserRole role = userRoleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public List<User> getUsersByHotelId(Long hotelId) {
        return userRepository.findByHotelId(hotelId);
    }

    public void setUserActiveStatus(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setActive(active);
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }
}
