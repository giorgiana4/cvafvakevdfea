package com.project.backend.controllers;

import com.project.backend.dtos.ClientDTO;
import com.project.backend.dtos.ClientDetailsDTO;
import com.project.backend.dtos.ReservationDetailsDTO;
import com.project.backend.services.ClientService;
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
@RequestMapping(value = "/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping()
    public ResponseEntity<List<ClientDTO>> getClients() {
        List<ClientDTO> dtos = clientService.findClients();
        for (ClientDTO dto : dtos) {
            Link clientLink = linkTo(methodOn(ClientController.class)
                    .getClient(dto.getId())).withRel("clientDetails");
            dto.add(clientLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/details")
    public ResponseEntity<List<ClientDetailsDTO>> getClientsDetails() {
        List<ClientDetailsDTO> dtos = clientService.findClientsDetails();
        for (ClientDetailsDTO dto : dtos) {
            Link clientLink = linkTo(methodOn(ClientController.class)
                    .getClient(dto.getIdClient())).withRel("clientDetails");
            dto.add(clientLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ClientDetailsDTO> getClient(@PathVariable("id") UUID clientId) {
        ClientDetailsDTO dto = clientService.findClientById(clientId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(value = "/username/{username}")
    public ResponseEntity<ClientDetailsDTO> getClientByUsername(@PathVariable("username") String username) {
        ClientDetailsDTO dto = clientService.findClientByUsername(username);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    //nu se foloseste
    @PostMapping()
    public ResponseEntity<UUID> insertClient(@Valid @RequestBody ClientDetailsDTO clientDetailsDTO) {
        UUID clientID = clientService.insert(null, clientDetailsDTO);
        return new ResponseEntity<>(clientID, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ClientDetailsDTO> updateClient(@PathVariable("id") UUID clientId, @Valid @RequestBody ClientDetailsDTO clientDetailsDTO){
        ClientDetailsDTO clientDetailsDTO2 = clientService.update(clientId, clientDetailsDTO);
        return new ResponseEntity<>(clientDetailsDTO2, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UUID> deleteClient(@PathVariable("id") UUID clientId){
        clientService.deleteClientById(clientId);
        return new ResponseEntity<>(clientId, HttpStatus.OK);
    }

    @PostMapping(value="/returnMoney")
    public ResponseEntity<Double> returnMoney(@Valid @RequestBody ReservationDetailsDTO reservationDetailsDTO) {
        double wallet = clientService.returnMoney(reservationDetailsDTO);
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }
}
