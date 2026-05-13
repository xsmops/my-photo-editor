package com.example.photoeditor.service;

/**
 * Stores basic color adjustment values.
 *
 * <p>The current editor uses these values for brightness, contrast, and
 * saturation changes.</p>
 *
 * @param brightness brightness adjustment value
 * @param contrast contrast adjustment value
 * @param saturation saturation adjustment value
 */
public record AdjustColor(
        double brightness,
        double contrast,
        double saturation
) {
}
