package com.project.backend.dtos;


import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;
import java.util.UUID;

public class CourtDetailsDTO extends RepresentationModel<CourtDetailsDTO> {
    private UUID idCourt;
    private int courtNumber;
    private String type;
    private String details;
    private String locationAddress;

    public CourtDetailsDTO() {
    }

    public CourtDetailsDTO(UUID idCourt, int courtNumber,  String type, String details, String locationAddress) {
        this.idCourt = idCourt;
        this.courtNumber = courtNumber;
        this.type = type;
        this.details =details;
        this.locationAddress=locationAddress;
    }

    public UUID getIdCourt() {
        return idCourt;
    }

    public void setIdCourt(UUID idCourt) {
        this.idCourt = idCourt;
    }

    public int getCourtNumber() {
        return courtNumber;
    }

    public void setCourtNumber(int courtNumber) {
        this.courtNumber = courtNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourtDetailsDTO courtDetailsDTO = (CourtDetailsDTO) o;
        return courtNumber == courtDetailsDTO.courtNumber
                && Objects.equals(idCourt, courtDetailsDTO.idCourt)
                && Objects.equals(type, courtDetailsDTO.type)
                && Objects.equals(locationAddress,courtDetailsDTO.locationAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCourt, courtNumber, type,locationAddress);
    }

}
