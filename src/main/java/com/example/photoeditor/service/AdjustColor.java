package com.example.photoeditor.service;

/**
 * Stores basic color adjustment values.
 *
 * <p>The current editor uses these values for brightness, contrast,
 * saturation, and hue changes.</p>
 *
 * @param brightness brightness adjustment value
 * @param contrast contrast adjustment value
 * @param saturation saturation adjustment value
 * @param hue hue rotation value used to shift the HSV hue channel
 */
public record AdjustColor(
        double brightness,
        double contrast,
        double saturation,
        double hue
) {
}
