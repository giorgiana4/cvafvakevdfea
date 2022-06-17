package com.project.backend.configurations;

import com.auth0.jwt.algorithms.Algorithm;
import com.project.backend.security.CustomAuthenticationFiIter;
import com.project.backend.security.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration @EnableWebSecurity @RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Algorithm algorithm;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/ws-notif/**");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS);

        //register
        http.authorizeRequests().antMatchers(POST, "/register").permitAll();

        // login
        http.authorizeRequests().antMatchers(POST, "/login").permitAll();

        //reset password
        http.authorizeRequests().antMatchers(POST, "/reset/**").permitAll();

        http.authorizeRequests().antMatchers(POST, "/reservations/availableIntervals").permitAll();
        http.authorizeRequests().antMatchers(POST, "/subscriptions/availableIntervals").permitAll();

        http.authorizeRequests().antMatchers(GET, "/locations/**").permitAll();
        http.authorizeRequests().antMatchers(GET, "/courts/**").permitAll();
        http.authorizeRequests().antMatchers(GET, "/tariffs/location/**").permitAll();


//        http.authorizeRequests().antMatchers( "/**").hasAnyAuthority("admin", "client");

        // users
        http.authorizeRequests().antMatchers(GET, "/users/token/refresh").denyAll();
        http.authorizeRequests().antMatchers(GET, "/users").denyAll();
        http.authorizeRequests().antMatchers(GET, "/users/name/**").authenticated();
        http.authorizeRequests().antMatchers(GET, "/users/**").denyAll();
        http.authorizeRequests().antMatchers(POST, "/users").denyAll();
        http.authorizeRequests().antMatchers(PUT, "/users/**").denyAll();
        http.authorizeRequests().antMatchers(DELETE, "/users/**").denyAll();
        http.authorizeRequests().antMatchers(DELETE, "/users/name/**").denyAll();

        // clients
        http.authorizeRequests().antMatchers(GET, "/clients").denyAll();
        http.authorizeRequests().antMatchers(GET, "/clients/details").denyAll();
        http.authorizeRequests().antMatchers(GET, "/clients/username/**").hasAnyAuthority("client");
        http.authorizeRequests().antMatchers(GET, "/clients/**").denyAll();
        http.authorizeRequests().antMatchers(POST, "/clients").denyAll();
        http.authorizeRequests().antMatchers(PUT, "/clients/**").hasAnyAuthority("client");
        http.authorizeRequests().antMatchers(DELETE, "/clients/**").denyAll();
        http.authorizeRequests().antMatchers(POST, "/clients/returnMoney").hasAnyAuthority("client");

        // tariffs
        http.authorizeRequests().antMatchers(GET, "/tariffs").denyAll();
        http.authorizeRequests().antMatchers(GET, "/tariffs/location/**").permitAll();
        http.authorizeRequests().antMatchers(GET, "/tariffs/**").denyAll();
        http.authorizeRequests().antMatchers(POST, "/tariffs").hasAnyAuthority("admin");
        http.authorizeRequests().antMatchers(PUT, "/tariffs/**").denyAll();
        http.authorizeRequests().antMatchers(DELETE, "/tariffs/**").denyAll();

        // locations
        http.authorizeRequests().antMatchers(GET, "/locations").permitAll();
        http.authorizeRequests().antMatchers(GET, "/locations/**").permitAll();
        http.authorizeRequests().antMatchers(POST, "/locations").hasAnyAuthority("admin");
        http.authorizeRequests().antMatchers(PUT, "/locations/**").hasAnyAuthority("admin");
        http.authorizeRequests().antMatchers(DELETE, "/locations/**").hasAnyAuthority("admin");

        // courts
        http.authorizeRequests().antMatchers(GET, "/courts").permitAll();
        http.authorizeRequests().antMatchers(GET, "/courts/**").permitAll();
        http.authorizeRequests().antMatchers(POST, "/courts").hasAnyAuthority("admin");
        http.authorizeRequests().antMatchers(PUT, "/courts/**").hasAnyAuthority("admin");
        http.authorizeRequests().antMatchers(DELETE, "/courts/**").hasAnyAuthority("admin");

        // reservations
        http.authorizeRequests().antMatchers(GET, "/reservations").hasAnyAuthority("client");
        http.authorizeRequests().antMatchers(GET, "/reservations/**").hasAnyAuthority("client");
        http.authorizeRequests().antMatchers(POST, "/reservations/availableIntervals").permitAll();
        http.authorizeRequests().antMatchers(POST, "/reservations/computePrice").hasAnyAuthority("client");
        http.authorizeRequests().antMatchers(POST, "/reservations").hasAnyAuthority("client");
        http.authorizeRequests().antMatchers(PUT, "/reservations/**").denyAll();
        http.authorizeRequests().antMatchers(DELETE, "/reservations/**").hasAnyAuthority("client");
        http.authorizeRequests().antMatchers(POST, "/reservations/search").hasAnyAuthority("client");
        http.authorizeRequests().antMatchers(POST, "/reservations/transfer").hasAnyAuthority("client");
        http.authorizeRequests().antMatchers(POST, "/reservations/changeClient").hasAnyAuthority("client");

        //subscriptions
        http.authorizeRequests().antMatchers(GET, "/subscriptions").hasAnyAuthority("client");
        http.authorizeRequests().antMatchers(GET, "/subscriptions/**").denyAll();
        http.authorizeRequests().antMatchers(POST, "/subscriptions/availableIntervals").permitAll();
        http.authorizeRequests().antMatchers(POST, "/subscriptions/computePrice").hasAnyAuthority("client");
        http.authorizeRequests().antMatchers(POST, "/subscriptions").hasAnyAuthority("client");
        http.authorizeRequests().antMatchers(PUT, "/subscriptions/**").denyAll();
        http.authorizeRequests().antMatchers(DELETE, "/subscriptions/**").denyAll();

        http.authorizeRequests().anyRequest().authenticated();
        http.addFilterBefore(new CustomAuthorizationFilter(algorithm), UsernamePasswordAuthenticationFilter.class);
        http.addFilter(new CustomAuthenticationFiIter(authenticationManagerBean(), algorithm));
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}