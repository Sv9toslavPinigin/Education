package net.thumbtack.buscompany.exception;

public enum BuscompanyErrorCode {
    LOGIN_ALREADY_EXISTS("User with this login already exists"),
    LAST_ADMIN("Must have at least one admin"),
    INVALID_OLD_PASSWORD("Wrong old password"),
    INVALID_SCHEDULE_PERIOD_FORMAT("Schedule period format must be: daily, odd, even," +
            " days of week (Sun, Mon, Thu... etc) or day numbers"),
    TRIP_NOT_FOUND("Trip with this id not found"),
    BUS_NOT_FOUND("Bus with this name not found"),
    DATE_NOT_FOUND("No trip for this date"),
    NO_FREE_PLACES("Not enough free places"),
    ORDER_NOT_FOUND("Order with this id not found"),
    ORDER_ACCESS_DENIED("Order with this id is not yours"),
    PLACE_ALREADY_SELECT("Place is already taken"),
    PASSENGER_NOT_FOUND("Passenger with this passport not found"),
    TRIP_NOT_APPROVED("Trip with this id is not approved"),
    TRIP_EDIT_FORBIDDEN("Forbidden to edit approved trips"),
    PLACE_NOT_FOUND("Place with this number is not found");

    private final String errorMessage;

    public String getErrorString() {
        return errorMessage;
    }

    BuscompanyErrorCode(String errorString) {
        this.errorMessage = errorString;
    }

}
