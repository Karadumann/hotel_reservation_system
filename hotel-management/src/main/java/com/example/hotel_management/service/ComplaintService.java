package com.example.hotel_management.service;

import com.example.hotel_management.model.Client;
import com.example.hotel_management.model.Complaint;
import com.example.hotel_management.model.Hotel;
import com.example.hotel_management.repository.ClientRepository;
import com.example.hotel_management.repository.ComplaintRepository;
import com.example.hotel_management.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {
    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private HotelRepository hotelRepository;

    public Complaint addComplaint(Long clientId, Long hotelId, String complaintText) {
        Client client = clientRepository.findById((long) Math.toIntExact(clientId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid client ID: " + clientId));
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid hotel ID: " + hotelId));

        Complaint complaint = new Complaint();
        complaint.setClient(client);
        complaint.setHotel(hotel);
        complaint.setComplaintText(complaintText);
        complaint.setCreatedAt(LocalDateTime.now());

        return complaintRepository.save(complaint);
    }
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public List<Complaint> getComplaintsByHotelId(Long hotelId) {
        return complaintRepository.findByHotelId(hotelId);
    }
}
