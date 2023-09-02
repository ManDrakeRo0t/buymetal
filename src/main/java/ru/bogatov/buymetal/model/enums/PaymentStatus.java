package ru.bogatov.buymetal.model.enums;

public enum PaymentStatus {

    PAID("PAID"),
    CANCELLED("CANCELLED"),
    WAITING("WAITING");

    final String value;
    PaymentStatus(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
