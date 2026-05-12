package com.example.photoeditor.Model.color;

public record NormalizedRgbColor(
        double red,
        double green,
        double blue
) {


    public NormalizedRgbColor clamp() {
        return new NormalizedRgbColor(
                clamp01(red()),
                clamp01(green()),
                clamp01(blue())
        );
    }

    private double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    public RgbColor toRgb() {
        return new RgbColor(
                red() * 255.0,
                green() * 255.0,
                blue() * 255.0
        );
    }
}