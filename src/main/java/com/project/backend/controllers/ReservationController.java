package com.project.backend.controllers;

import com.google.common.collect.Multimap;
import com.project.backend.dtos.ReservationDetailsDTO;
import com.project.backend.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin
@RequestMapping(value = "/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping()
    public ResponseEntity<List<ReservationDetailsDTO>> getReservations() {
        List<ReservationDetailsDTO> dtos = reservationService.findReservations();
        for (ReservationDetailsDTO dto : dtos) {
            Link reservationLink = linkTo(methodOn(ReservationController.class)
                    .getReservation(dto.getIdReservation())).withRel("reservationDetails");
            dto.add(reservationLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ReservationDetailsDTO> getReservation(@PathVariable("id") UUID reservationId) {
        ReservationDetailsDTO dto = reservationService.findReservationById(reservationId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/availableIntervals")
    public ResponseEntity<Map<Integer, Collection<Integer>>> availableIntervals(@Valid @RequestBody ReservationDetailsDTO reservationDetailsDTO) {
        Multimap<Integer,Integer> intervals = reservationService.availableIntervals(reservationDetailsDTO);
        return new ResponseEntity<>(intervals.asMap(), HttpStatus.OK);
    }

    @PostMapping("/computePrice")
    public ResponseEntity<Double> computePrice(@Valid @RequestBody ReservationDetailsDTO reservationDetailsDTO) {
        double price = reservationService.computePrice(reservationDetailsDTO);
        return new ResponseEntity<>(price, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UUID> insertReservation(@Valid @RequestBody ReservationDetailsDTO reservationDetailsDTO) {
        UUID reservationId = reservationService.insert(reservationDetailsDTO);
        return new ResponseEntity<>(reservationId, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ReservationDetailsDTO> updateReservation(@PathVariable("id") UUID reservationId, @Valid @RequestBody ReservationDetailsDTO reservationDetailsDTO){
        ReservationDetailsDTO reservationDetailsDTO2 = reservationService.update(reservationId, reservationDetailsDTO);
        return new ResponseEntity<>(reservationDetailsDTO2, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UUID> deleteReservation(@PathVariable("id") UUID reservationId){
        reservationService.deleteReservationById(reservationId);
        return new ResponseEntity<>(reservationId, HttpStatus.OK);
    }

    @PostMapping(value="/search")
    public ResponseEntity<String> searchPlayer(@Valid @RequestBody ReservationDetailsDTO reservationDetailsDTO){
        String code = reservationService.searchPlayer(reservationDetailsDTO);
        return new ResponseEntity<>(code, HttpStatus.OK);
    }

    @PostMapping(value="/transfer")
    public ResponseEntity<String> transferReservation(@Valid @RequestBody ReservationDetailsDTO reservationDetailsDTO){
        String code = reservationService.transferReservation(reservationDetailsDTO);
        return new ResponseEntity<>(code, HttpStatus.OK);
    }

    @PostMapping(value="/changeClient")
    public ResponseEntity<ReservationDetailsDTO> changeClient(@Valid @RequestBody ReservationDetailsDTO reservationDetailsDTO) {
        ReservationDetailsDTO reservationDetailsDTO1 = reservationService.changeClient(reservationDetailsDTO);
        return new ResponseEntity<>(reservationDetailsDTO1, HttpStatus.OK);
    }
}
