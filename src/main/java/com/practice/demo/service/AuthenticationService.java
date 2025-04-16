package com.practice.demo.service;

import com.practice.demo.models.entities.Client;
import com.practice.demo.repos.entity_repos.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final ClientRepository clientRepository;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void register(Client client) {
        clientRepository.save(client);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                client.getUsername(), client.getPassword()
        );
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Registration successful for login {}", client.getUsername());
    }

    public void login(String username, String password) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                username, password
        );
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Authentication successful for login {}", username);
    }
}
