package com.project.backend.services;

import com.project.backend.controllers.handlers.exceptions.ResourceNotFoundException;
import com.project.backend.dtos.CourtDetailsDTO;
import com.project.backend.dtos.builders.CourtBuilder;
import com.project.backend.entities.Court;
import com.project.backend.entities.Location;
import com.project.backend.repositories.CourtRepo;
import com.project.backend.repositories.LocationRepo;
import com.project.backend.validators.UtilsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourtService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourtService.class);
    private final CourtRepo courtRepo;
    private final LocationRepo locationRepo;
    private final UtilsValidator utilsValidator;

    @Autowired
    public CourtService(CourtRepo courtRepo, LocationRepo locationRepo, UtilsValidator utilsValidator) {
        this.courtRepo = courtRepo;
        this.locationRepo=locationRepo;
        this.utilsValidator=utilsValidator;
    }

    public List<CourtDetailsDTO> findCourts() {
        List<Court> courtList = courtRepo.findAll();
        if (courtList.isEmpty()) {
            LOGGER.error("No court found");
            throw new ResourceNotFoundException(Court.class.getSimpleName());
        }
        List<CourtDetailsDTO> courtDetailsDTOS=new ArrayList<>();
        for(Court court:courtList)
            courtDetailsDTOS.add(CourtBuilder.toCourtDetailsDTO(court,court.getLocation().getAddress()));
        return courtDetailsDTOS;
    }

    public CourtDetailsDTO findCourtById(UUID idCourt) {
        Optional<Court> courtOptional = courtRepo.findById(idCourt);
        if (!courtOptional.isPresent()) {
            LOGGER.error("Court with id {} was not found in db", idCourt);
            throw new ResourceNotFoundException(Court.class.getSimpleName() + " with id: " + idCourt);
        }
        return CourtBuilder.toCourtDetailsDTO(courtOptional.get(),courtOptional.get().getLocation().getAddress());
    }

    public UUID insert(CourtDetailsDTO courtDetailsDTO) {
        utilsValidator.validateDetails(courtDetailsDTO.getDetails());
        utilsValidator.validateIntPositive(courtDetailsDTO.getCourtNumber());

        Court court = new Court(courtDetailsDTO.getCourtNumber(),courtDetailsDTO.getType(), courtDetailsDTO.getDetails());
        List<Location> locations=locationRepo.findAll();
        List<String> addresses=new ArrayList<>();
        for(Location location:locations){
            addresses.add(courtDetailsDTO.getLocationAddress());
            if(location.getAddress().equals(courtDetailsDTO.getLocationAddress()))
                court.setLocation(location);
        }
        if (!addresses.contains(courtDetailsDTO.getLocationAddress())) {
            LOGGER.error("Location with address {} was not found in db", courtDetailsDTO.getLocationAddress());
            throw new ResourceNotFoundException(Location.class.getSimpleName() + " with address: " + courtDetailsDTO.getLocationAddress());
        }
        court = courtRepo.save(court);
        LOGGER.debug("Court with id {} was inserted in db", court.getIdCourt());
        return court.getIdCourt();
    }

    public CourtDetailsDTO update(UUID idCourt, CourtDetailsDTO courtDetailsDTO){
        utilsValidator.validateDetails(courtDetailsDTO.getDetails());
        utilsValidator.validateIntPositive(courtDetailsDTO.getCourtNumber());

        Optional<Court> courtOptional = courtRepo.findById(idCourt);
        if (!courtOptional.isPresent()) {
            LOGGER.error("Court with id {} was not found in db", idCourt);
            throw new ResourceNotFoundException(Court.class.getSimpleName() + " with id: " + idCourt);
        }
        Court court = new Court(courtDetailsDTO.getCourtNumber(),courtDetailsDTO.getType(), courtDetailsDTO.getDetails());
        court.setIdCourt(idCourt);
        List<Location> locations=locationRepo.findAll();
        for(Location location:locations){
            if(location.getAddress().equals(courtDetailsDTO.getLocationAddress()))
                court.setLocation(location);
        }
        court = courtRepo.save(court);
        LOGGER.debug("Court with id {} was updated in db", court.getIdCourt());
        return CourtBuilder.toCourtDetailsDTO(court, court.getLocation().toString());
    }

    public void deleteCourtById(UUID idCourt){
        Optional<Court> courtOptional = courtRepo.findById(idCourt);
        if (!courtOptional.isPresent()) {
            LOGGER.error("Court with id {} was not found in db", idCourt);
            throw new ResourceNotFoundException(Court.class.getSimpleName() + " with id: " + idCourt);
        }
        courtRepo.deleteById(idCourt);
        LOGGER.debug("Court with id {} was deleted from db", idCourt);
    }

}
