package com.example.photoeditor.Model.color;

public record RgbColor(
        double red,
        double green,
        double blue
) {

    public boolean isValid() {
        return red() >= 0.0 && red() <= 255.0
                && green() >= 0.0 && green() <= 255.0
                && blue() >= 0.0 && blue() <= 255.0;
    }

    public RgbColor clamp() {
        double min = 0.0;
        double max = 255.0;

        double newRed = Math.max(min, Math.min(max, red()));
        double newGreen = Math.max(min, Math.min(max, green()));
        double newBlue = Math.max(min, Math.min(max, blue()));

        return new RgbColor(newRed, newGreen, newBlue);
    }

    public NormalizedRgbColor normalize() {
        return new NormalizedRgbColor(
                red() / 255.0,
                green() / 255.0,
                blue() / 255.0
        );
    }
}
