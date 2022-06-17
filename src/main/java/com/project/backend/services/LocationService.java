package com.project.backend.services;

import com.project.backend.controllers.handlers.exceptions.ResourceNotFoundException;
import com.project.backend.dtos.LocationDTO;
import com.project.backend.dtos.builders.LocationBuilder;
import com.project.backend.entities.Location;
import com.project.backend.repositories.LocationRepo;
import com.project.backend.validators.RegisterValidator;
import com.project.backend.validators.UtilsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LocationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationService.class);
    private final LocationRepo locationRepo;
    private final UtilsValidator utilsValidator;
    private final RegisterValidator registerValidator;

    @Autowired
    public LocationService(LocationRepo locationRepo, UtilsValidator utilsValidator, RegisterValidator registerValidator) {
        this.locationRepo = locationRepo;
        this.utilsValidator = utilsValidator;
        this.registerValidator = registerValidator;
    }

    public List<LocationDTO> findLocations() {
        List<Location> locationList = locationRepo.findAll();
        if (locationList.isEmpty()) {
            LOGGER.error("No location found");
            throw new ResourceNotFoundException(Location.class.getSimpleName());
        }
        return locationList.stream()
                .map(LocationBuilder::toLocationDTO)
                .collect(Collectors.toList());
    }

    public LocationDTO findLocationById(UUID idLocation) {
        Optional<Location> locationOptional = locationRepo.findById(idLocation);
        if (!locationOptional.isPresent()) {
            LOGGER.error("Location with id {} was not found in db", idLocation);
            throw new ResourceNotFoundException(Location.class.getSimpleName() + " with id: " + idLocation);
        }
        return LocationBuilder.toLocationDTO(locationOptional.get());
    }

    public UUID insert(LocationDTO locationDTO) {
        utilsValidator.validateLocation(locationDTO);
        registerValidator.validateName(locationDTO.getName());

        Location location = LocationBuilder.toEntity(locationDTO);
        location = locationRepo.save(location);
        LOGGER.debug("Location with id {} was inserted in db", location.getIdLocation());
        return location.getIdLocation();
    }

    public LocationDTO update(UUID idLocation, LocationDTO locationDTO){
        utilsValidator.validateLocation(locationDTO);
        registerValidator.validateName(locationDTO.getName());

        Optional<Location> locationOptional = locationRepo.findById(idLocation);
        if (!locationOptional.isPresent()) {
            LOGGER.error("Location with id {} was not found in db", idLocation);
            throw new ResourceNotFoundException(Location.class.getSimpleName() + " with id: " + idLocation);
        }
        Location location = LocationBuilder.toEntity(locationDTO);
        location.setIdLocation(idLocation);
        location = locationRepo.save(location);
        LOGGER.debug("Location with id {} was updated in db", location.getIdLocation());
        return LocationBuilder.toLocationDTO(location);
    }

    public void deleteLocationById(UUID idLocation){
        Optional<Location> locationOptional = locationRepo.findById(idLocation);
        if (!locationOptional.isPresent()) {
            LOGGER.error("Location with id {} was not found in db", idLocation);
            throw new ResourceNotFoundException(Location.class.getSimpleName() + " with id: " + idLocation);
        }
        locationRepo.deleteById(idLocation);
        LOGGER.debug("Location with id {} was deleted from db", idLocation);
    }
}
