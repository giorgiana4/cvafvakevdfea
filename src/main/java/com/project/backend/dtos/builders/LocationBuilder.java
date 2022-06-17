package com.project.backend.dtos.builders;

import com.project.backend.dtos.LocationDTO;
import com.project.backend.entities.Location;

public class LocationBuilder {

    public LocationBuilder() {
    }

    public static LocationDTO toLocationDTO(Location location){
        return new LocationDTO(location.getIdLocation(),
                location.getLatitude(),
                location.getLongitude(),
                location.getAddress(),
                location.getName(),
                location.getStartHour(),
                location.getEndHour());
    }

    public static Location toEntity(LocationDTO locationDTO){
        return new Location(locationDTO.getLatitude(),
                locationDTO.getLongitude(),
                locationDTO.getAddress(),
                locationDTO.getName(),
                locationDTO.getStartHour(),
                locationDTO.getEndHour());
    }

}
