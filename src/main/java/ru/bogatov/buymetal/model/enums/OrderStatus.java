package ru.bogatov.buymetal.model.enums;

public enum OrderStatus {

    OPEN("OPEN"),
    AGREED("AGREED"),
    REJECTED("REJECTED"),
    WAITING_PAYMENT("WAITING_PAYMENT"),
    DELIVERY("DELIVERY"),
    COMPLETED("COMPLETED");

    final String value;
    OrderStatus(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
