package com.project.backend.controllers;


import com.project.backend.dtos.LocationDTO;
import com.project.backend.services.LocationService;
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
@RequestMapping(value = "/locations")

public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping()
    public ResponseEntity<List<LocationDTO>> getLocations() {
        List<LocationDTO> dtos = locationService.findLocations();
        for (LocationDTO dto : dtos) {
            Link locationCredLink = linkTo(methodOn(LocationController.class)
                    .getLocation(dto.getIdLocation())).withRel("locationDetails");
            dto.add(locationCredLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LocationDTO> getLocation(@PathVariable("id") UUID locationId) {
        LocationDTO dto = locationService.findLocationById(locationId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UUID> insertLocation(@Valid @RequestBody LocationDTO locationDTO) {
        UUID locationID = locationService.insert(locationDTO);
        return new ResponseEntity<>(locationID, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<LocationDTO> updateLocation(@PathVariable("id") UUID locationId, @Valid @RequestBody LocationDTO locationDTO){
        LocationDTO locationDTO2 = locationService.update(locationId, locationDTO);
        return new ResponseEntity<>(locationDTO2, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UUID> deleteLocation(@PathVariable("id") UUID locationId){
        locationService.deleteLocationById(locationId);
        return new ResponseEntity<>(locationId, HttpStatus.OK);
    }
}
