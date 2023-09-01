package ru.bogatov.buymetal.model.enums;

public enum ApplicationStatus {

    OPEN("OPEN"),
    RESERVED("RESERVED"),
    ARCHIVED("ARCHIVED");

    final String value;

    public String getValue() {
        return value;
    }
    ApplicationStatus(String value) {
        this.value = value;
    }
}
