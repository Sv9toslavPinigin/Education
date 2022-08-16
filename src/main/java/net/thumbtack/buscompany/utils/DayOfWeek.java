package net.thumbtack.buscompany.utils;

public enum DayOfWeek {
    Mon,
    Tue,
    Wed,
    Thu,
    Fri,
    Sat,
    Sun;

    private static final DayOfWeek[] ENUMS = DayOfWeek.values();

    public int getValue() {
        return ordinal() + 1;
    }
}
