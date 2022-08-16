package net.thumbtack.buscompany.dto.response;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class MyError {
    private String errorCode;
    private String field;
    private String message;
}
