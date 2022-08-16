package net.thumbtack.buscompany.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@Setter
@NoArgsConstructor
public class ErrorDto {
    List<MyError> errors;
}
