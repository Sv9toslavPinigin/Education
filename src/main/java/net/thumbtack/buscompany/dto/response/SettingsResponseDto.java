package net.thumbtack.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class SettingsResponseDto {
    private String maxNameLength;
    private String minPasswordLength;
}
