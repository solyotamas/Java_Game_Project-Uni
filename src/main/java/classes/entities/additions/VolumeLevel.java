package classes.entities.additions;

public enum VolumeLevel {
    OFF(0.0, "/images/off.png"), LOW(0.1, "/images/low.png"), MEDIUM(0.3, "/images/medium.png"), HIGH(0.6, "/images/high.png");

    private final double volume;
    private final String iconPath;

    VolumeLevel(double volume, String iconPath) {
        this.volume = volume;
        this.iconPath = iconPath;
    }

    public double getVolume() {
        return volume;
    }

    public String getIconPath() {
        return iconPath;
    }

    public static VolumeLevel fromVolume(double volume) {
        for (VolumeLevel level : values()) {
            if (level.volume == volume) {
                return level;
            }
        }
        return MEDIUM;
    }

    public VolumeLevel next() {
        return switch (this) {
            case OFF -> LOW;
            case LOW -> MEDIUM;
            case MEDIUM -> HIGH;
            case HIGH -> OFF;
        };
    }
}