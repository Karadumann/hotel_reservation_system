package com.example.hotel_management.controller;

import com.example.hotel_management.model.Room;
import com.example.hotel_management.model.User;
import com.example.hotel_management.repository.RoomRepository;
import com.example.hotel_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.hotel_management.model.Client;
import com.example.hotel_management.model.Reservation;
import com.example.hotel_management.service.ClientService;
import com.example.hotel_management.service.ReservationService;
import com.example.hotel_management.service.RoomService;

import java.util.List;

@Controller
@RequestMapping("/receptionist")
public class ReceptionistController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ClientService clientService;

    @GetMapping("/home")
    public String receptionistHome() {
        return "receptionist/home";
    }

    @GetMapping("/addReservation")
    public String addReservationForm(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("client", new Client());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("rooms", roomService.getRoomsByHotelId(user.getHotel().getId()));
        return "receptionist/addReservation";
    }

    @PostMapping("/addReservation")
    public String addReservationSubmit(@ModelAttribute("reservation") Reservation reservation,
                                       @ModelAttribute("client") Client client,
                                       Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());

        client.setHotel(user.getHotel());
        Client savedClient = clientService.saveClient(client);
        reservation.setClient(savedClient);
        reservation.setHotel(user.getHotel());

        reservationService.createReservation(reservation);

        model.addAttribute("successMessage", "Reservation has been successfully added.");
        return "redirect:/receptionist/addReservation?success=true";
    }

    @GetMapping("/cancelReservation")
    public String cancelReservationForm(Model model) {
        model.addAttribute("reservationNumber", "");
        return "receptionist/cancelReservation";
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
        return "redirect:/receptionist/cancelReservation";
    }

    @GetMapping("/viewReservations")
    public String viewReservations(Model model) {
        model.addAttribute("reservations", reservationService.getAllReservations());
        return "receptionist/viewReservations";
    }

    @GetMapping("/manageRooms")
    public String manageRoomsForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userService.findByUsername(username);

        List<Room> rooms = roomRepository.findByHotelId(user.getHotel().getId());

        model.addAttribute("rooms", rooms);
        return "receptionist/manageRooms";
    }
}
