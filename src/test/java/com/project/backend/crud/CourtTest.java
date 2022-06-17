package com.project.backend.crud;

import com.project.backend.dtos.CourtDetailsDTO;
import com.project.backend.dtos.builders.CourtBuilder;
import com.project.backend.entities.Court;
import com.project.backend.entities.Location;
import com.project.backend.repositories.CourtRepo;
import com.project.backend.repositories.LocationRepo;
import com.project.backend.services.CourtService;
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

public class CourtTest {
    private final List<CourtDetailsDTO> courts = new ArrayList<>();
    private final UtilsValidator utilsValidator = new UtilsValidator();

    @Mock
    private CourtService courtService = mock(CourtService.class);
    private CourtRepo courtRepo = mock(CourtRepo.class);
    private CourtDetailsDTO courtDTO = mock(CourtDetailsDTO.class);
    private LocationRepo locationRepo = mock(LocationRepo.class);

    @Before
    public void setupFindAll() {
        CourtDetailsDTO court1 = new CourtDetailsDTO();
        court1.setIdCourt(UUID.randomUUID());
        court1.setCourtNumber(1);
        court1.setType("Grass");

        CourtDetailsDTO court2 = new CourtDetailsDTO();
        court2.setIdCourt(UUID.randomUUID());
        court2.setCourtNumber(2);
        court2.setType("Hard");

        courts.add(court1);
        courts.add(court2);
    }

    @Before
    public void setupFindById(){
        courtDTO.setCourtNumber(4);
        courtDTO.setType("Clay");
    }

    @Test
    public void findAllTest(){
        Mockito.when(courtService.findCourts()).thenReturn(courts);

        List<CourtDetailsDTO> courtsFindAll = courtService.findCourts();
        assertNotNull(courtsFindAll);
        assertEquals( 2, courtsFindAll.size());
    }

    @Test
    public void findByIdCourt(){
        Mockito.when(courtService.findCourtById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(courtDTO);

        CourtDetailsDTO courtDTOTest = courtService.findCourtById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        assertEquals(courtDTO, courtDTOTest);
    }


    @Test
    public void testInsertCourt(){
        assertNotNull(courtRepo);
        Court insertCourt = new Court(4,"Grass", "Perfect");

        Mockito.when(courtService.insert(CourtBuilder.toCourtDetailsDTO(insertCourt,"address")))
                .thenReturn(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        Mockito.when(courtService.findCourtById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(CourtBuilder.toCourtDetailsDTO(insertCourt,"address"));

        UUID id = courtService.insert(CourtBuilder.toCourtDetailsDTO(insertCourt,"address"));
        Assert.assertNotNull(id);

        CourtDetailsDTO courtDTOTest = courtService.findCourtById(id);

        assertEquals(4, courtDTOTest.getCourtNumber());
        assertEquals("Grass", courtDTOTest.getType());
    }

    @Test
    public void testDeleteCourt(){
        assertNotNull(courtRepo);
        Court court1 = new Court(4,"Grass", "Ideal");

        Mockito.when(courtService.insert(CourtBuilder.toCourtDetailsDTO(court1,"address")))
                .thenReturn(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        Mockito.when(courtRepo.findById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(Optional.of(court1));

        CourtService courtBL=new CourtService(courtRepo,locationRepo, utilsValidator);
        UUID id = UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5");
        courtBL.deleteCourtById(id);
        verify(courtRepo, times(1)).deleteById(id);
    }

    @Test
    public void testUpdateCourt(){
        assertNotNull(courtRepo);
        Location location=new Location(20,20,"address","location",2,4);
        Court court1 = new Court(2,"Grass", "Very nice");
        court1.setLocation(location);
        Court courtUp = new Court(3,"Clay", "Also very nice");
        courtUp.setLocation(location);
        Mockito.when(courtRepo.findById(UUID.fromString("40aa7198-063a-434c-a14d-53096977c05b")))
                .thenReturn(Optional.of(court1));
        Mockito.when(courtRepo.save(Mockito.any(Court.class))).thenReturn(courtUp);

        CourtService courtBL=new CourtService(courtRepo,locationRepo, utilsValidator);
        CourtDetailsDTO updateCourt = courtBL.update(UUID.fromString("40aa7198-063a-434c-a14d-53096977c05b"),
                CourtBuilder.toCourtDetailsDTO(courtUp,"address"));

        assertEquals(3, updateCourt.getCourtNumber());
        assertEquals("Clay", updateCourt.getType());
    }
}
