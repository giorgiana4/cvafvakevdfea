package com.project.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class UtilsSecurity {

    public static final DecodedJWT decodeToken(String authorizationHeader, Algorithm algorithm) {
        String token = authorizationHeader.substring("TennisApp_".length());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public static final String createJavaWebToken(UserDetails userDetails, HttpServletRequest request,
                                                  Algorithm algorithm, List withClaim_roles) {
        if(withClaim_roles != null && !withClaim_roles.isEmpty()) {
            return JWT.create()
                    .withSubject(userDetails.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis()
                            + TimeUnit.MINUTES.toMillis(120)))
                    .withIssuer(request.getRequestURL().toString())
                    .withClaim("roles", userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                    .sign(algorithm);
        }
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()
                        + TimeUnit.MINUTES.toMillis(240)))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
    }

    public static final void sendTokensToFrontend(HttpServletResponse response, String access_token,
                                                  String refresh_token) throws IOException {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    public static final void sendForbiddenErrorToFrontend(HttpServletResponse response,
                                                          Exception exception) throws IOException {
        response.setHeader("error", exception.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String, String> error = new HashMap<>();
        error.put("error_message", exception.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);

    }
}
