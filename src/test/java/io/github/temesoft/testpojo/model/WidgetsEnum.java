package io.github.temesoft.testpojo.model;

public enum WidgetsEnum {

    WIDGET_1("Widget 1"),
    WIDGET_2("Widget 2"),
    WIDGET_3("Widget 3");

    private final String description;

    WidgetsEnum(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
