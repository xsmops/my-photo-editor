package com.example.photoeditor.Model.color;

/**
 * Represents an RGB color normalized to the mathematical range 0..1.
 *
 * <p>This format is useful for color conversion formulas because RGB display
 * values are usually stored as 0..255.</p>
 *
 * @param red normalized red channel
 * @param green normalized green channel
 * @param blue normalized blue channel
 */
public record NormalizedRgbColor(
        double red,
        double green,
        double blue
) {


    /**
     * Returns a copy of this color with all channels limited to 0..1.
     *
     * @return clamped normalized RGB color
     */
    public NormalizedRgbColor clamp() {
        return new NormalizedRgbColor(
                clamp01(red()),
                clamp01(green()),
                clamp01(blue())
        );
    }

    /**
     * Limits a value to the normalized 0..1 range.
     */
    private double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    /**
     * Converts this normalized RGB color back to display RGB values.
     *
     * @return RGB color in the 0..255 range
     */
    public RgbColor toRgb() {
        return new RgbColor(
                red() * 255.0,
                green() * 255.0,
                blue() * 255.0
        );
    }
}
