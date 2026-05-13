package com.example.photoeditor.Model.color;

/**
 * Represents an RGB color in the display range 0..255.
 *
 * @param red red channel value
 * @param green green channel value
 * @param blue blue channel value
 */
public record RgbColor(
        double red,
        double green,
        double blue
) {

    /**
     * Checks whether all RGB channels are inside the valid 0..255 range.
     *
     * @return true if all channels are valid
     */
    public boolean isValid() {
        return red() >= 0.0 && red() <= 255.0
                && green() >= 0.0 && green() <= 255.0
                && blue() >= 0.0 && blue() <= 255.0;
    }

    /**
     * Returns a copy of this color with all channels limited to 0..255.
     *
     * @return clamped RGB color
     */
    public RgbColor clamp() {
        double min = 0.0;
        double max = 255.0;

        double newRed = Math.max(min, Math.min(max, red()));
        double newGreen = Math.max(min, Math.min(max, green()));
        double newBlue = Math.max(min, Math.min(max, blue()));

        return new RgbColor(newRed, newGreen, newBlue);
    }

    /**
     * Converts this RGB color to normalized RGB values in the 0..1 range.
     *
     * @return normalized RGB color
     */
    public NormalizedRgbColor normalize() {
        return new NormalizedRgbColor(
                red() / 255.0,
                green() / 255.0,
                blue() / 255.0
        );
    }
}
