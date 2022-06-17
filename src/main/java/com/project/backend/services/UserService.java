package com.project.backend.services;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.backend.controllers.handlers.exceptions.ResourceNotFoundException;
import com.project.backend.dtos.UserDTO;
import com.project.backend.dtos.builders.UserBuilder;
import com.project.backend.entities.User;
import com.project.backend.repositories.UserRepo;
import com.project.backend.security.UtilsSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class UserService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final UserRepo userRepo;
    private final Algorithm algorithm;

    @Autowired
    public UserService(UserRepo userRepo, Algorithm algorithm) {
        this.userRepo = userRepo;
        this.algorithm = algorithm;
    }

    // needed for secured access from frontend
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("TennisApp_")) {
            try {
                DecodedJWT decodedJWT = UtilsSecurity.decodeToken(authorizationHeader, algorithm);
                String username = decodedJWT.getSubject();
                UserDetails userDetails = this.loadUserByUsername(username);

                String access_token = UtilsSecurity.createJavaWebToken(userDetails, request, algorithm,
                        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
                String refresh_token = authorizationHeader.substring("TennisApp_".length());
                UtilsSecurity.sendTokensToFrontend(response, access_token, refresh_token);
            } catch (Exception exception) {
                LOGGER.error("Error refreshing access token: {}", exception.getMessage());
                UtilsSecurity.sendForbiddenErrorToFrontend(response, exception);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    // needed for secured LOGIN
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = UserBuilder.toEntity(findUserByUsername(username));
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    public List<UserDTO> findUsers() {
        List<User> userList = userRepo.findAll();
        if (userList.isEmpty()) {
            LOGGER.error("No user found");
            throw new ResourceNotFoundException(User.class.getSimpleName());
        }
        return userList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO findUserById(UUID idUser) {
        Optional<User> userOptional = userRepo.findById(idUser);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", idUser);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + idUser);
        }
        return UserBuilder.toUserDTO(userOptional.get());
    }

    public UserDTO findUserByUsername(String username) {
        List<UserDTO> userDTOList= this.findUsers();
        for(UserDTO u:userDTOList){
            if (u.getUsername().equals(username))
                return u;
        }
        LOGGER.error("User with username {} was not found in db", username);
        throw new ResourceNotFoundException(User.class.getSimpleName() + " with username: " + username);
    }

    public UUID insert(UserDTO userDTO) {
        User user = UserBuilder.toEntity(userDTO);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user = userRepo.save(user);
        LOGGER.debug("User with id {} was inserted in db", user.getIdUser());
        return user.getIdUser();
    }

    public UserDTO update(UUID idUser, UserDTO userDTO){
        Optional<User> userOptional = userRepo.findById(idUser);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", idUser);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + idUser);
        }
        User user = UserBuilder.toEntity(userDTO);
        user.setIdUser(idUser);
        user = userRepo.save(user);
        LOGGER.debug("User with id {} was updated in db", user.getIdUser());
        return UserBuilder.toUserDTO(user);
    }

    public void deleteUserByUsername(String username) {
        UserDTO user = findUserByUsername(username);
        if (user==null) {
            LOGGER.error("User with username {} was not found in db", username);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with username: " + username);
        }
        userRepo.deleteById(user.getIdUser());
        LOGGER.debug("User with username {} was deleted from db", username);
    }

    public void deleteUserById(UUID idUser){
        Optional<User> userOptional = userRepo.findById(idUser);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", idUser);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + idUser);
        }
        userRepo.deleteById(idUser);
        LOGGER.debug("User with id {} was deleted from db", idUser);
    }
}
