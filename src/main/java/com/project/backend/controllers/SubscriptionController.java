package com.project.backend.controllers;

import com.google.common.collect.Multimap;
import com.project.backend.dtos.SubscriptionDetailsDTO;
import com.project.backend.services.SubscriptionService;
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
@RequestMapping(value = "/subscriptions")

public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping()
    public ResponseEntity<List<SubscriptionDetailsDTO>> getSubscriptions() {
        List<SubscriptionDetailsDTO> dtos = subscriptionService.findSubscriptions();
        for (SubscriptionDetailsDTO dto : dtos) {
            Link subscriptionCredLink = linkTo(methodOn(SubscriptionController.class)
                    .getSubscription(dto.getIdSubscription())).withRel("subscriptionDetails");
            dto.add(subscriptionCredLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<SubscriptionDetailsDTO> getSubscription(@PathVariable("id") UUID subscriptionId) {
        SubscriptionDetailsDTO dto = subscriptionService.findSubscriptionById(subscriptionId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/availableIntervals")
    public ResponseEntity<Map<Integer, Collection<Integer>>> availableIntervals(@Valid @RequestBody SubscriptionDetailsDTO subscriptionDetailsDTO) {
        Multimap<Integer,Integer> intervals = subscriptionService.availableIntervals(subscriptionDetailsDTO);
        return new ResponseEntity<>(intervals.asMap(), HttpStatus.OK);
    }

    @PostMapping("/computePrice")
    public ResponseEntity<Double> computePrice(@Valid @RequestBody SubscriptionDetailsDTO subscriptionDetailsDTO) {
        double price = subscriptionService.computePrice(subscriptionDetailsDTO);
        return new ResponseEntity<>(price, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UUID> insertSubscription(@Valid @RequestBody SubscriptionDetailsDTO subscriptionDetailsDTO) {
        UUID subscriptionID = subscriptionService.insert(subscriptionDetailsDTO);
        return new ResponseEntity<>(subscriptionID, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<SubscriptionDetailsDTO> updateSubscription(@PathVariable("id") UUID subscriptionId, @Valid @RequestBody SubscriptionDetailsDTO subscriptionDetailsDTO){
        SubscriptionDetailsDTO subscriptionDetailsDTO2 = subscriptionService.update(subscriptionId, subscriptionDetailsDTO);
        return new ResponseEntity<>(subscriptionDetailsDTO2, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UUID> deleteSubscription(@PathVariable("id") UUID subscriptionId){
        subscriptionService.deleteSubscriptionById(subscriptionId);
        return new ResponseEntity<>(subscriptionId, HttpStatus.OK);
    }
}
