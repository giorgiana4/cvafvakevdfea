package com.project.backend.dtos;

import org.springframework.hateoas.RepresentationModel;
import java.util.Objects;
import java.util.UUID;

public class LocationDTO extends RepresentationModel<LocationDTO> {
    private UUID idLocation;
    private double latitude;
    private double longitude;
    private String address;
    private String name;
    private int startHour;
    private int endHour;

    public LocationDTO() {
    }

    public LocationDTO(UUID idLocation, double latitude, double longitude, String address, String name, int startHour, int endHour) {
        this.idLocation = idLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.name = name;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public UUID getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(UUID idLocation) {
        this.idLocation = idLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationDTO that = (LocationDTO) o;
        return Double.compare(that.latitude, latitude) == 0
                && Double.compare(that.longitude, longitude) == 0
                && startHour == that.startHour
                && endHour == that.endHour
                && Objects.equals(idLocation, that.idLocation)
                && Objects.equals(address, that.address)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLocation, latitude, longitude, address, name, startHour, endHour);
    }
}
