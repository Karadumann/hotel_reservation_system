package com.example.hotel_management.config;

import com.example.hotel_management.model.Hotel;
import com.example.hotel_management.model.User;
import com.example.hotel_management.model.UserRole;
import com.example.hotel_management.repository.HotelRepository;
import com.example.hotel_management.repository.UserRepository;
import com.example.hotel_management.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRoleRepository.count() == 0) {
            UserRole adminRole = new UserRole();
            adminRole.setRoleName("ROLE_ADMINISTRATOR");
            userRoleRepository.save(adminRole);

            UserRole ownerRole = new UserRole();
            ownerRole.setRoleName("ROLE_OWNER");
            userRoleRepository.save(ownerRole);

            UserRole managerRole = new UserRole();
            managerRole.setRoleName("ROLE_MANAGER");
            userRoleRepository.save(managerRole);

            UserRole receptionistRole = new UserRole();
            receptionistRole.setRoleName("ROLE_RECEPTIONIST");
            userRoleRepository.save(receptionistRole);

            Hotel hotel = new Hotel();
            hotel.setName("Default Hotel");
            hotel.setAddress("123 Default St.");
            hotelRepository.save(hotel);

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(adminRole);
            admin.setHotel(hotel);
            userRepository.save(admin);
        }
    }
}
