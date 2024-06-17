    package com.example.hotel_management.controller;

    import com.example.hotel_management.model.Hotel;
    import com.example.hotel_management.model.User;
    import com.example.hotel_management.service.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/users")
    public class UserController {

        @Autowired
        private UserService userService;

        @PostMapping("/owners")
        public User createOwner(@RequestBody User user, @RequestBody Hotel hotel) {
            return userService.createOwner(user.getUsername(), user.getPassword(), hotel, user.getName(), user.getSurname(), user.getTelephone(), user.getAddress());
        }

        @PostMapping("/managers")
        public User createManager(@RequestParam String username, @RequestParam String password,
                                  @RequestParam String name, @RequestParam String surname,
                                  @RequestParam String telephone, @RequestParam String address,
                                  @RequestBody Hotel hotel) {
            return userService.createManager(username, password, hotel, name, surname, telephone, address);
        }

        @PostMapping("/receptionists")
        public User createReceptionist(@RequestParam String username, @RequestParam String password,
                                       @RequestParam String name, @RequestParam String surname,
                                         @RequestParam String telephone, @RequestParam String address,
                                       @RequestBody Hotel hotel) {
            return userService.createReceptionist(username, password, hotel, name, surname, telephone, address);
        }
    }
