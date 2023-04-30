package com.example.ctsusermanagement.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;

@SecuritySchemes(value = @SecurityScheme(name = "TOKEN", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"))
public class OpenApiConfig {

}
