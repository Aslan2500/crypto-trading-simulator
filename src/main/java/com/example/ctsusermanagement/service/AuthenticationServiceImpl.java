package com.example.ctsusermanagement.service;

import com.example.ctsusermanagement.exception.UserAlreadyExistsException;
import com.example.ctsusermanagement.jwt.JwtService;
import com.example.ctsusermanagement.model.Portfolio;
import com.example.ctsusermanagement.model.request.AuthenticationRequestDto;
import com.example.ctsusermanagement.model.response.AuthenticationResponseDto;
import com.example.ctsusermanagement.model.request.RegisterRequestDto;
import com.example.ctsusermanagement.model.constants.Role;
import com.example.ctsusermanagement.model.User;
import com.example.ctsusermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthenticationResponseDto register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already registered");
        }
        var portfolio = Portfolio.builder()
                .cashBalance((double) 0)
                .totalValue((double) 0)
                .build();
        var user = User.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .portfolio(portfolio)
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException(format("User %s not found", request.getEmail())));
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }
}
