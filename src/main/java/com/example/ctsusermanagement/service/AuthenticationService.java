package com.example.ctsusermanagement.service;

import com.example.ctsusermanagement.model.request.AuthenticationRequestDto;
import com.example.ctsusermanagement.model.response.AuthenticationResponseDto;
import com.example.ctsusermanagement.model.request.RegisterRequestDto;

public interface AuthenticationService {

    AuthenticationResponseDto register(RegisterRequestDto request);

    AuthenticationResponseDto authenticate(AuthenticationRequestDto request);
}
