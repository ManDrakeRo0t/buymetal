package ru.bogatov.buymetal.model.enums;

public enum MetalForm {
    STRIP("Полоса"),
    CIRCLE("Круг"),
    SQUARE("Квадрат"),
    WIRE("Проволка"),
    HEXAGON("Шестигранник"),
    CHANNEL("Швеллер"),
    I_BEAM("Двутавр"),
    CORNER("Уголок"),
    PIPE("Труба"),
    SHEET("Лист"),
    ARMATURE("Арматура");


    final String value;

    MetalForm(String value) {
        this.value = value;
    }
}
