package com.project.backend.dtos.builders;

import com.project.backend.dtos.CourtDetailsDTO;
import com.project.backend.entities.Court;

public class CourtBuilder {

    public CourtBuilder() {
    }

    public static CourtDetailsDTO toCourtDetailsDTO(Court court, String locationAddress){
        return new CourtDetailsDTO(court.getIdCourt(),
                court.getCourtNumber(),
                court.getType(),
                court.getDetails(),
                locationAddress);
    }

    public static Court toEntity(CourtDetailsDTO courtDetailsDTO){
        return new Court(courtDetailsDTO.getCourtNumber(),
                courtDetailsDTO.getType(),
                courtDetailsDTO.getDetails());
    }

}
