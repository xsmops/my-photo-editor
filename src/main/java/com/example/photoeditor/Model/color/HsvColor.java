package com.example.photoeditor.Model.color;

public record HsvColor (
    double hue,
    double saturation,
    double value
) {

    public boolean isValid(double hue,
                            double saturation,
                            double value) {
        return hue >= 0 && hue <= 360
                && saturation >= 0 && saturation <= 1
                && value >= 0 && value <= 1;
    }

    public HsvColor clamp() {
        double minSV = 0.0;
        double maxSV = 1.0;

        double newSaturation = Math.max(minSV, Math.min(maxSV, saturation()));
        double newValue = Math.max(minSV, Math.min(maxSV, value()));


        double maxHue = 360.0;
        double newHue = hue() % maxHue;

        if (newHue < 0) {
            newHue += maxHue;
        }


        return new HsvColor(newHue, newSaturation, newValue);
    }
}


