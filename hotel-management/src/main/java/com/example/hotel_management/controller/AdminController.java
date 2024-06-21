package com.example.hotel_management.controller;

import com.example.hotel_management.DTO.RoomDTO;
import com.example.hotel_management.model.*;
import com.example.hotel_management.repository.HotelRepository;
import com.example.hotel_management.repository.RoomRepository;
import com.example.hotel_management.repository.UserRepository;
import com.example.hotel_management.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/home")
    public String adminHome() {
        return "admin/home";
    }

    @GetMapping("/manageRooms")
    public String manageRoomsForm(Model model) {
        List<Hotel> hotels = hotelRepository.findAll();
        List<Room> rooms = roomRepository.findAll();
        model.addAttribute("newRoom", new Room());
        model.addAttribute("rooms", rooms);
        model.addAttribute("hotels", hotels);
        return "admin/manageRooms";
    }

    @PostMapping("/addRoom")
    public String addRoomSubmit(@ModelAttribute("room") Room room, @RequestParam("hotelId") Long hotelId, Model model) {
        try {
            Hotel hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid hotel ID: " + hotelId));
            room.setHotel(hotel);
            room.setOccupied(false);
            room.setStatus(RoomStatus.AVAILABLE);
            roomService.addRoom(room, hotelId);
            model.addAttribute("successMessage", "Room added successfully to Hotel: " + room.getHotel().getName());
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", "Error adding room: " + e.getMessage());
        }
        return "redirect:/admin/manageRooms";
    }

    @PostMapping("/deleteRoom/{roomId}")
    public String deleteRoom(@PathVariable("roomId") Long roomId, Model model) {
        try {
            roomRepository.deleteById(roomId);
            model.addAttribute("message", "Room successfully deleted.");
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting the room: " + e.getMessage());
        }
        return "redirect:/admin/manageRooms";
    }

    @PostMapping("/updateRoomStatus/{roomId}")
    public String updateRoomStatus(@PathVariable("roomId") Long roomId, @RequestParam RoomStatus status, Model model) {
        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Invalid room ID: " + roomId));
            room.setStatus(status);
            roomRepository.save(room);
            model.addAttribute("successMessage", "Room status updated successfully.");
        } catch (Exception e) {
            model.addAttribute("error", "Error updating room status: " + e.getMessage());
        }
        return "redirect:/admin/manageRooms";
    }

    @GetMapping("/addOwner")
    public String addOwnerForm(Model model) {
        model.addAttribute("owner", new User());
        model.addAttribute("hotel", new Hotel());
        return "admin/addOwner";
    }

    @PostMapping("/addOwner")
    public String addOwnerSubmit(@ModelAttribute("owner") User owner, @ModelAttribute("hotel") Hotel hotel, Model model) {
        try {
            Hotel savedHotel = hotelRepository.save(hotel);
            userService.createOwner(owner.getUsername(), owner.getPassword(), savedHotel, owner.getName(), owner.getSurname(), owner.getTelephone(), owner.getAddress());
            model.addAttribute("successMessage", "Owner and Hotel added successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding owner: " + e.getMessage());
        }
        model.addAttribute("owner", new User());
        model.addAttribute("hotel", new Hotel());
        return "admin/addOwner";
    }

    @GetMapping("/manageUsers")
    public String manageUsersForm(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/manageUsers";
    }

    @PostMapping("/updateUserStatus")
    public String updateUserStatus(@RequestParam("userId") Long userId, @RequestParam("active") boolean active, Model model) {
        try {
            userService.setUserActiveStatus(userId, active);
            model.addAttribute("successMessage", "User status updated successfully.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating user status: " + e.getMessage());
        }
        return "redirect:/admin/manageUsers";
    }

    @GetMapping("/viewUsers")
    public String viewUsers(Model model) {
        try {
            List<User> managers = userRepository.findByRoleRoleName("ROLE_MANAGER");
            List<User> owners = userRepository.findByRoleRoleName("ROLE_OWNER");
            List<User> receptionists = userRepository.findByRoleRoleName("ROLE_RECEPTIONIST");

            model.addAttribute("managers", managers);
            model.addAttribute("owners", owners);
            model.addAttribute("receptionists", receptionists);

            return "admin/viewUsers";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error retrieving user list: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/addReservation")
    public String adminAddReservationForm(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("client", new Client());
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("hotels", hotelService.getAllHotels());
        return "admin/addReservation";
    }

    @PostMapping("/addReservation")
    public String adminAddReservationSubmit(@ModelAttribute("reservation") Reservation reservation,
                                            @ModelAttribute("client") Client client,
                                            @RequestParam("hotelId") Long hotelId,
                                            Model model) {
        try {
            Hotel hotel = hotelService.findById(hotelId)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));

            client.setHotel(hotel);
            Client savedClient = clientService.saveClient(client);
            reservation.setClient(savedClient);
            reservation.setHotel(hotel);

            reservationService.createReservation(reservation);

            model.addAttribute("successMessage", "Reservation has been successfully added.");
            return "redirect:/admin/addReservation?success=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding reservation: " + e.getMessage());
            return "admin/addReservation";
        }
    }

    @GetMapping("/cancelReservation")
    public String cancelReservationForm(Model model) {
        model.addAttribute("reservationNumber", "");
        return "admin/cancelReservation";
    }

    @PostMapping("/cancelReservation")
    public String cancelReservation(@RequestParam("reservationNumber") String reservationNumber, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try {
            reservationService.cancelReservation(reservationNumber);
            model.addAttribute("successMessage", "Reservation has been successfully cancelled.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error cancelling reservation: " + e.getMessage());
        }
        return "redirect:/admin/viewReservations";
    }

    @GetMapping("/viewReservations")
    public String viewReservations(Model model) {
        model.addAttribute("reservations", reservationService.getAllReservations());
        return "admin/viewReservations";
    }

    @GetMapping("/getRooms")
    @ResponseBody
    public List<RoomDTO> getRooms(@RequestParam Long hotelId) {
        return roomService.getRoomsByHotelId(hotelId);
    }
}
