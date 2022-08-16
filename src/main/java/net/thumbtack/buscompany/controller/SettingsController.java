package net.thumbtack.buscompany.controller;

import net.thumbtack.buscompany.dto.response.SettingsResponseDto;
import net.thumbtack.buscompany.model.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SettingsController {

    @Value("${max_name_length}")
    private String maxNameLength;
    @Value("${min_password_length}")
    private String minPasswordLength;

    @PostMapping(value = "api/settings")
    private SettingsResponseDto getSettings(Authentication authentication) {
        SettingsResponseDto.SettingsResponseDtoBuilder responseDtoBuilder = SettingsResponseDto.builder();
        responseDtoBuilder
                .maxNameLength(maxNameLength)
                .minPasswordLength(minPasswordLength);
        if (authentication.getAuthorities().contains(Role.ROLE_ADMIN)) {
            return responseDtoBuilder.build();
        }
        if (authentication.getAuthorities().contains(Role.ROLE_CLIENT)) {
            return responseDtoBuilder.build();
        }
        return responseDtoBuilder.build();
    }
}
