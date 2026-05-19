package com.example.photoeditor.Model.color;

/**
 * Represents a color in the HSV color space.
 *
 * <p>Hue is measured in degrees. Saturation and value use the normalized
 * 0..1 range.</p>
 *
 * @param hue hue angle in degrees
 * @param saturation color intensity in the 0..1 range
 * @param value brightness value in the 0..1 range
 */
public record HsvColor (
    double hue,
    double saturation,
    double value
) {

    /**
     * Checks whether the provided HSV values are inside the valid ranges.
     *
     * @param hue hue angle in degrees
     * @param saturation saturation value
     * @param value brightness value
     * @return true if all values are valid
     */
    public boolean isValid(double hue,
                            double saturation,
                            double value) {
        return hue >= 0 && hue <= 360
                && saturation >= 0 && saturation <= 1
                && value >= 0 && value <= 1;
    }

    /**
     * Returns a copy of this color with values limited to valid HSV ranges.
     *
     * <p>Hue wraps around the 0..360 range. Saturation and value are clamped to
     * 0..1.</p>
     *
     * @return clamped HSV color
     */
    public HsvColor clamp() {
        double minSV = 0.0;
        double maxSV = 1.0;

        double newSaturation = Math.clamp(saturation(), minSV, maxSV);
        double newValue = Math.clamp(value(), minSV, maxSV);


        double maxHue = 360.0;
        double newHue = hue() % maxHue;

        // Negative hue values should wrap back into the positive range.
        if (newHue < 0) {
            newHue += maxHue;
        }


        return new HsvColor(newHue, newSaturation, newValue);
    }
}

