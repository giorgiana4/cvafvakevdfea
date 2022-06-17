package com.project.backend.controllers;


import com.project.backend.dtos.CourtDetailsDTO;
import com.project.backend.services.CourtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin
@RequestMapping(value = "/courts")

public class CourtController {

    private final CourtService courtService;

    @Autowired
    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @GetMapping()
    public ResponseEntity<List<CourtDetailsDTO>> getCourts() {
        List<CourtDetailsDTO> dtos = courtService.findCourts();
        for (CourtDetailsDTO dto : dtos) {
            Link courtCredLink = linkTo(methodOn(CourtController.class)
                    .getCourt(dto.getIdCourt())).withRel("courtDetails");
            dto.add(courtCredLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CourtDetailsDTO> getCourt(@PathVariable("id") UUID courtId) {
        CourtDetailsDTO dto = courtService.findCourtById(courtId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UUID> insertCourt(@Valid @RequestBody CourtDetailsDTO courtDetailsDTO) {
        UUID courtID = courtService.insert(courtDetailsDTO);
        return new ResponseEntity<>(courtID, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CourtDetailsDTO> updateCourt(@PathVariable("id") UUID courtId, @Valid @RequestBody CourtDetailsDTO courtDetailsDTO){
        CourtDetailsDTO courtDetailsDTO2 = courtService.update(courtId, courtDetailsDTO);
        return new ResponseEntity<>(courtDetailsDTO2, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UUID> deleteCourt(@PathVariable("id") UUID courtId){
        courtService.deleteCourtById(courtId);
        return new ResponseEntity<>(courtId, HttpStatus.OK);
    }
}
