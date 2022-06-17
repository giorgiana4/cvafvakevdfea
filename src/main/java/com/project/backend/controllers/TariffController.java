package com.project.backend.controllers;

import com.project.backend.dtos.TariffDTO;
import com.project.backend.services.TariffService;
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
@RequestMapping(value = "/tariffs")
public class TariffController {
    private final TariffService tariffService;

    @Autowired
    public TariffController(TariffService tariffService){
        this.tariffService = tariffService;
    }

    @GetMapping()
    public ResponseEntity<List<TariffDTO>> getTariffs() {
        List<TariffDTO> tariffDTOS = tariffService.findTariffs();
        for (TariffDTO dto : tariffDTOS) {
            Link tariffLinked = linkTo(methodOn(TariffController.class)
                    .getTariffById(dto.getIdTariff())).withRel("tariffDetails");
            dto.add(tariffLinked);
        }
        return new ResponseEntity<>(tariffDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TariffDTO> getTariffById(@PathVariable("id") UUID tariffId) {
        TariffDTO tariffDTO = tariffService.findTariffById(tariffId);
        return new ResponseEntity<>(tariffDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/location/{locationId}")
    public ResponseEntity<List<TariffDTO>> getTariffsByLocation(@PathVariable("locationId") UUID locationId) {
        List<TariffDTO> tariffDTOS = tariffService.findTariffsByLocation(locationId);
        for (TariffDTO dto : tariffDTOS) {
            Link tariffLinked = linkTo(methodOn(TariffController.class)
                    .getTariffById(dto.getIdTariff())).withRel("tariffDetails");
            dto.add(tariffLinked);
        }
        return new ResponseEntity<>(tariffDTOS, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UUID> insertTariff(@Valid @RequestBody TariffDTO tariffDTO) {
        UUID tariffId = tariffService.insertTariff(tariffDTO);
        return new ResponseEntity<>(tariffId, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<TariffDTO> updateTariff(@PathVariable("id") UUID tariffId, @Valid @RequestBody TariffDTO tariffDTO){
        TariffDTO tariffDTOUpdate = tariffService.update(tariffId, tariffDTO);
        return new ResponseEntity<>(tariffDTOUpdate, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UUID> deleteTariff(@PathVariable("id") UUID tariffId){
        tariffService.deleteTariffById(tariffId);
        return new ResponseEntity<>(tariffId, HttpStatus.OK);
    }
}
