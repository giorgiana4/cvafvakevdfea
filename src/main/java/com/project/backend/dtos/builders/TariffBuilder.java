package com.project.backend.dtos.builders;

import com.project.backend.dtos.TariffDTO;
import com.project.backend.entities.Location;
import com.project.backend.entities.Tariff;

public class TariffBuilder {

    public TariffBuilder() {
    }

    public static TariffDTO toTariffDTO(Tariff tariff, Location location) {
        return new TariffDTO(tariff.getIdTariff(), tariff.getSeason(), tariff.getDayOfWeek(), tariff.isNight(),
                tariff.getPrice(), location.getAddress());
    }

    public static Tariff toEntity(TariffDTO tariffDTO) {
        return new Tariff(tariffDTO.getSeason(), tariffDTO.getDayOfWeek(), tariffDTO.isNight(), tariffDTO.getPrice());
    }
}
