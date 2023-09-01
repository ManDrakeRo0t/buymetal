package ru.bogatov.buymetal.model.enums;

public enum OrderStatus {

    OPEN("OPEN"),
    APPROVAL("APPROVAL"),
    AGREED("AGREED"),
    REJECTED("REJECTED"),
    WAITING_PAYMENT("WAITING_PAYMENT"),
    PAID("PAID"),
    COMPLETED("COMPLETED");

    final String value;
    OrderStatus(String value) {
        this.value = value;
    }
}
