package com.project.backend.crud;

import com.project.backend.dtos.LocationDTO;
import com.project.backend.dtos.builders.LocationBuilder;
import com.project.backend.entities.Location;
import com.project.backend.repositories.LocationRepo;
import com.project.backend.repositories.UserRepo;
import com.project.backend.services.LocationService;
import com.project.backend.validators.RegisterValidator;
import com.project.backend.validators.UtilsValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class LocationTest {
    private final List<LocationDTO> locations = new ArrayList<>();
    private final UtilsValidator utilsValidator = new UtilsValidator();
    private RegisterValidator registerValidator;

    @Mock
    private LocationService locationService = mock(LocationService.class);
    private LocationRepo locationRepo = mock(LocationRepo.class);
    private LocationDTO locationDTO = mock(LocationDTO.class);
    private UserRepo userRepo = mock(UserRepo.class);

    @Before
    public void setup(){
        registerValidator = new RegisterValidator(userRepo);
    }

    @Before
    public void setupFindAll() {
        LocationDTO location1 = new LocationDTO();
        location1.setIdLocation(UUID.randomUUID());
        location1.setAddress("address");
        location1.setStartHour(13);
        location1.setEndHour(15);
        location1.setLatitude(30);
        location1.setLongitude(30);
        location1.setName("name");

        LocationDTO location2 = new LocationDTO();
        location2.setIdLocation(UUID.randomUUID());
        location2.setAddress("second address");
        location2.setStartHour(14);
        location2.setEndHour(16);
        location2.setLatitude(40);
        location2.setLongitude(40);
        location2.setName("name two");

        locations.add(location1);
        locations.add(location2);
    }

    @Before
    public void setupFindById(){
        locationDTO.setName("name");
        locationDTO.setAddress("address");
        locationDTO.setStartHour(10);
        locationDTO.setEndHour(12);
        locationDTO.setLatitude(10);
        locationDTO.setLongitude(10);
    }

    @Test
    public void findAllTest(){
        Mockito.when(locationService.findLocations()).thenReturn(locations);

        List<LocationDTO> locationsFindAll = locationService.findLocations();
        assertNotNull(locationsFindAll);
        assertEquals( 2, locationsFindAll.size());
    }

    @Test
    public void findByIdLocation(){
        Mockito.when(locationService.findLocationById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(locationDTO);

        LocationDTO locationDTOTest = locationService.findLocationById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        assertEquals(locationDTO, locationDTOTest);
    }

    @Test
    public void testInsertLocation(){
        assertNotNull(locationRepo);
        Location insertLocation = new Location(50,35,"address","name",10,23);

        Mockito.when(locationService.insert(LocationBuilder.toLocationDTO(insertLocation)))
                .thenReturn(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        Mockito.when(locationService.findLocationById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(LocationBuilder.toLocationDTO(insertLocation));

        UUID id = locationService.insert(LocationBuilder.toLocationDTO(insertLocation));
        Assert.assertNotNull(id);

        LocationDTO locationDTOTest = locationService.findLocationById(id);

        assertEquals(50, locationDTOTest.getLatitude(),0);
        assertEquals(35, locationDTOTest.getLongitude(),0);
    }

    @Test
    public void testDeleteLocation(){
        assertNotNull(locationRepo);
        Location location1 = new Location(50,35,"address","name",17,19);

        Mockito.when(locationService.insert(LocationBuilder.toLocationDTO(location1)))
                .thenReturn(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        Mockito.when(locationRepo.findById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(Optional.of(location1));

        LocationService locationBL=new LocationService(locationRepo, utilsValidator, registerValidator);
        UUID id = UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5");
        locationBL.deleteLocationById(id);
        verify(locationRepo, times(1)).deleteById(id);
    }

    @Test
    public void testUpdateLocation(){
        assertNotNull(locationRepo);
        Location location1 = new Location(15,15,"address","name",12,14);
        Location locationUpEntity = new Location(25,25,"address 2","name",13,15);
        LocationDTO locationUp = new LocationDTO(UUID.fromString("40aa7198-063a-434c-a14d-53096977c05b"), 25,25,"address 2","name",13,15);

        Mockito.when(locationRepo.findById(UUID.fromString("40aa7198-063a-434c-a14d-53096977c05b")))
                .thenReturn(Optional.of(location1));
        Mockito.when(locationRepo.save(Mockito.any(Location.class))).thenReturn(locationUpEntity);

        LocationService locationBL=new LocationService(locationRepo, utilsValidator,registerValidator);
        LocationDTO updateLocation = locationBL.update(UUID.fromString("40aa7198-063a-434c-a14d-53096977c05b"),
                locationUp);

        assertEquals(25, updateLocation.getLatitude(),0);
        assertEquals(25, updateLocation.getLongitude(),0);
    }
}
