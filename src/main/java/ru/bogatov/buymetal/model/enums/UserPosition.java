package ru.bogatov.buymetal.model.enums;

public enum UserPosition {

    CUSTOMER("CUSTOMER"),
    SUPPLIER("SUPPLIER");
    final String value;

    UserPosition(String value) {
        this.value = value;
    }
}
