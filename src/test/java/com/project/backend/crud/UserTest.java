package com.project.backend.crud;

import com.auth0.jwt.algorithms.Algorithm;
import com.project.backend.dtos.UserDTO;
import com.project.backend.dtos.builders.UserBuilder;
import com.project.backend.entities.User;
import com.project.backend.repositories.UserRepo;
import com.project.backend.services.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserTest {
    private final List<UserDTO> users = new ArrayList<>();

    @Mock
    private UserService userService = mock(UserService.class);
    private UserRepo userRepo = mock(UserRepo.class);
    private UserDTO userDTO = mock(UserDTO.class);
    private Algorithm algorithm = mock(Algorithm.class);

    @Before
    public void setupFindAll() {
        UserDTO user1 = new UserDTO();
        user1.setIdUser(UUID.randomUUID());
        user1.setUsername("Ion");
        user1.setPassword("TotIon");
        user1.setEmail("ion@ion@gmail.com");
        user1.setRole("client");

        UserDTO user2 = new UserDTO();
        user2.setIdUser(UUID.randomUUID());
        user2.setUsername("Ion2");
        user2.setPassword("TotIon2");
        user2.setEmail("ion2@ion2@gmail.com");
        user2.setRole("client");

        users.add(user1);
        users.add(user2);
    }

    @Before
    public void setupFindById(){
        userDTO.setEmail("george@gerorgica@gmail.com");
        userDTO.setUsername("georgesubacoperire");
        userDTO.setPassword("inviszibila");
        userDTO.setRole("client");
    }

    @Test
    public void findAllTest(){
        Mockito.when(userService.findUsers()).thenReturn(users);

        List<UserDTO> usersFindAll = userService.findUsers();
        assertNotNull(usersFindAll);
        assertEquals( 2, usersFindAll.size());
    }

    @Test
    public void findByIdUser(){
        Mockito.when(userService.findUserById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(userDTO);

        UserDTO userDTOTest = userService.findUserById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        assertEquals(userDTO, userDTOTest);
    }

    @Test
    public void findByUsername(){
        Mockito.when(userService.findUserByUsername("georgesubacoperire"))
                .thenReturn(userDTO);

        UserDTO userDTOTest = userService.findUserByUsername("georgesubacoperire");
        assertEquals(userDTO, userDTOTest);
    }

    @Test
    public void testInsertUser(){
        assertNotNull(userRepo);
        User insertUser = new User("ana", "ana123", "client", "ana@ana.com");

        Mockito.when(userService.insert(UserBuilder.toUserDTO(insertUser)))
                .thenReturn(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        Mockito.when(userService.findUserById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(UserBuilder.toUserDTO(insertUser));

        UUID id = userService.insert(UserBuilder.toUserDTO(insertUser));
        Assert.assertNotNull(id);

        UserDTO userDTOTest = userService.findUserById(id);

        assertEquals("ana", userDTOTest.getUsername());
        assertEquals("ana123", userDTOTest.getPassword());
    }

    @Test
    public void testDeleteUser(){
        assertNotNull(userRepo);
        User user1 = new User("ana", "ana", "client", "ana@gmai");

        Mockito.when(userService.insert(UserBuilder.toUserDTO(user1)))
                .thenReturn(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5"));
        Mockito.when(userRepo.findById(UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5")))
                .thenReturn(Optional.of(user1));

        UserService customerBL=new UserService(userRepo, algorithm);
        UUID id = UUID.fromString("d576bb88-7229-4753-8ad8-65319862a2d5");
        customerBL.deleteUserById(id);
        verify(userRepo, times(1)).deleteById(id);
    }

    @Test
    public void testUpdateUser(){
        assertNotNull(userRepo);
        User user1 = new User("ana", "ana", "client", "ana@gmai");
        UserDTO userUpDto = new UserDTO(null, "ana2", "ana2", "client", "ana2@gmai");
        User userUp = new User("ana2", "ana2", "client", "ana2@gmai");

        Mockito.when(userRepo.findById(UUID.fromString("40aa7198-063a-434c-a14d-53096977c05b")))
                .thenReturn(Optional.of(user1));
        Mockito.when(userRepo.save(Mockito.any(User.class))).thenReturn(userUp);

        UserService userBL=new UserService(userRepo, algorithm);
        UserDTO updateUser = userBL.update(UUID.fromString("40aa7198-063a-434c-a14d-53096977c05b"),
                userUpDto);

        assertEquals("ana2", updateUser.getUsername());
        assertEquals("ana2@gmai", updateUser.getEmail());
    }
}