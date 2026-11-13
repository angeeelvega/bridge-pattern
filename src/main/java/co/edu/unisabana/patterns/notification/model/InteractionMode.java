package co.edu.unisabana.patterns.notification.model;

public enum InteractionMode {
    INFORMATIVE("informative"),
    PERSISTENT("persistent"),
    BLOCKING("blocking");

    private final String label;

    InteractionMode(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
