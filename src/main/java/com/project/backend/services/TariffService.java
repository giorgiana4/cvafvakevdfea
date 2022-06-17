package com.project.backend.services;

import com.project.backend.controllers.handlers.exceptions.ResourceNotFoundException;
import com.project.backend.dtos.CourtDetailsDTO;
import com.project.backend.dtos.TariffDTO;
import com.project.backend.dtos.builders.CourtBuilder;
import com.project.backend.dtos.builders.TariffBuilder;
import com.project.backend.entities.Court;
import com.project.backend.entities.Location;
import com.project.backend.entities.Tariff;
import com.project.backend.repositories.LocationRepo;
import com.project.backend.repositories.TariffRepo;
import com.project.backend.validators.RegisterValidator;
import com.project.backend.validators.UtilsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TariffService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TariffService.class);
    private final TariffRepo tariffRepo;
    private final LocationRepo locationRepo;
    private final UtilsValidator utilsValidator;

    @Autowired
    public TariffService(TariffRepo tariffRepo, LocationRepo locationRepo, UtilsValidator utilsValidator) {
        this.tariffRepo = tariffRepo;
        this.locationRepo = locationRepo;
        this.utilsValidator = utilsValidator;
    }

    public List<TariffDTO> findTariffs(){
        List<Tariff> tariffList = tariffRepo.findAll();
        return listToDto(tariffList);
    }

    public TariffDTO findTariffById(UUID id) {
        Optional<Tariff> tariffOptional = tariffRepo.findById(id);
        if (!tariffOptional.isPresent()) {
            LOGGER.error("Tariff with id {} was not found in db", id);
            throw new ResourceNotFoundException(Tariff.class.getSimpleName() + " with id: " + id);
        }

        return TariffBuilder.toTariffDTO(tariffOptional.get(), tariffOptional.get().getLocation());
    }

    public List<TariffDTO> findTariffsByLocation(UUID locationId){
        Optional<Location> locationOptional = locationRepo.findById(locationId);
        if (!locationOptional.isPresent()) {
            LOGGER.error("Location with id {} was not found in db", locationId);
            throw new ResourceNotFoundException(Location.class.getSimpleName() + " with id: " + locationId);
        }
        List<Tariff> tariffs = tariffRepo.findAllByLocation(locationOptional.get());
        return listToDto(tariffs);
    }

    public UUID insertTariff(TariffDTO tariffDTO) {
        utilsValidator.validateDoublePositive(tariffDTO.getPrice());

        Tariff tariff = TariffBuilder.toEntity(tariffDTO);
        List<Location> locations = locationRepo.findAll();
        for(Location location:locations){
            if(location.getAddress().equals(tariffDTO.getLocationAddress()))
                tariff.setLocation(location);
        }
        tariff = tariffRepo.save(tariff);
        LOGGER.debug("Tariff with id {} was inserted in db", tariff.getIdTariff());

        return tariff.getIdTariff();
    }

    public TariffDTO update(UUID tariffId, TariffDTO tariffDTO){
        utilsValidator.validateDoublePositive(tariffDTO.getPrice());

        Optional<Tariff> tariffOptional = tariffRepo.findById(tariffId);
        if (!tariffOptional.isPresent()) {
            LOGGER.error("Tariff with id {} was not found in db", tariffId);
            throw new ResourceNotFoundException(Tariff.class.getSimpleName() + " with id: " + tariffId);
        }

        Tariff tariff = TariffBuilder.toEntity(tariffDTO);
        tariff.setIdTariff(tariffId);

        List<Location> locations = locationRepo.findAll();
        for(Location location:locations){
            if(location.getAddress().equals(tariffDTO.getLocationAddress()))
                tariff.setLocation(location);
        }

        tariff = tariffRepo.save(tariff);
        LOGGER.debug("Tariff with id {} was updated in db", tariff.getIdTariff());

        return TariffBuilder.toTariffDTO(tariff, tariff.getLocation());
    }

    public void deleteTariffById(UUID tariffId){
        Optional<Tariff> tariffOptional = tariffRepo.findById(tariffId);
        if (!tariffOptional.isPresent()) {
            LOGGER.error("Tariff with id {} was not found in db", tariffId);
            throw new ResourceNotFoundException(Tariff.class.getSimpleName() + " with id: " + tariffId);
        }
        tariffRepo.deleteById(tariffId);
        LOGGER.debug("Tariff with id {} was deleted from db", tariffId);
    }

    private List<TariffDTO> listToDto( List<Tariff> tariffs){
        if(tariffs.isEmpty()){
            LOGGER.error("No tariff found");
            throw new ResourceNotFoundException(Tariff.class.getSimpleName());
        }
        List<TariffDTO> tariffDTOs=new ArrayList<>();
        for(Tariff tariff:tariffs)
            tariffDTOs.add(TariffBuilder.toTariffDTO(tariff, tariff.getLocation()));
        return tariffDTOs;
    }
}
