package co.edu.unisabana.patterns.notification.model;

public enum UrgencyLevel {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    private final String label;

    UrgencyLevel(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
