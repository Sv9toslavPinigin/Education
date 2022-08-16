package net.thumbtack.buscompany.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BuscompanyException extends Exception {

    private BuscompanyErrorCode errorCode;
    private String field;
}
