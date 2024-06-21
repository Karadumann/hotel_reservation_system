package com.example.hotel_management.controller;

import com.example.hotel_management.DTO.RoomStatusUpdateRequest;
import com.example.hotel_management.model.*;
import com.example.hotel_management.repository.RoomRepository;
import com.example.hotel_management.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/receptionist")
public class ReceptionistController {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ComplaintService complaintService;

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

        try {
            reservationService.createReservation(reservation);
            model.addAttribute("successMessage", "Reservation has been successfully added.");
            return "redirect:/receptionist/addReservation?success=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding reservation: " + e.getMessage());
            return "receptionist/addReservation";
        }
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
        User user = userService.findByUsername(auth.getName());

        List<Room> rooms = roomRepository.findByHotelId(user.getHotel().getId());
        model.addAttribute("rooms", rooms);
        return "receptionist/manageRooms";
    }

    @PostMapping("/updateRoomStatus/{roomId}")
    @ResponseBody
    public String updateRoomStatus(@PathVariable("roomId") Long roomId, @RequestBody RoomStatusUpdateRequest request) {
        try {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid room ID: " + roomId));
            room.setStatus(RoomStatus.valueOf(request.getStatus()));
            roomRepository.save(room);
            return "Room status updated successfully";
        } catch (Exception e) {
            return "Error updating room status: " + e.getMessage();
        }
    }

    private Long getCurrentUserHotelId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());
        return user.getHotel().getId();
    }

    @GetMapping("/addComplaint")
    public String addComplaintForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());

        List<Client> clients = clientService.getClientsByHotelId(user.getHotel().getId());
        model.addAttribute("clients", clients);
        model.addAttribute("complaint", new Complaint());
        return "receptionist/addComplaint";
    }

    @PostMapping("/addComplaint")
    public String addComplaintSubmit(@RequestParam("clientId") Long clientId,
                                     @RequestParam("complaintText") String complaintText,
                                     Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());

        try {
            Complaint complaint = complaintService.addComplaint(clientId, user.getHotel().getId(), complaintText);
            model.addAttribute("successMessage", "Complaint has been successfully added.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding complaint: " + e.getMessage());
        }
        return "receptionist/addComplaint";
    }



}
