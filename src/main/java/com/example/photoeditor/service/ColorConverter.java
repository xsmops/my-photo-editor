package com.example.photoeditor.service;

import com.example.photoeditor.Model.color.HsvColor;
import com.example.photoeditor.Model.color.NormalizedRgbColor;
import com.example.photoeditor.Model.color.RgbColor;

/**
 * Converts colors between RGB and HSV color spaces.
 *
 * <p>This class does not store color values. It only contains conversion
 * logic. Color data is stored in {@link RgbColor}, {@link NormalizedRgbColor},
 * and {@link HsvColor}.</p>
 */
public class ColorConverter {
    public ColorConverter() {}

    /**
     * Converts an RGB color to HSV.
     *
     * <p>The input RGB color is clamped to the valid 0..255 range before
     * conversion. The returned HSV color is also clamped to its valid range.</p>
     *
     * @param rgbColor source RGB color
     * @return converted HSV color
     */
    public HsvColor rgbToHsv(RgbColor rgbColor) {
        double h;
        double s;
        double v;

        RgbColor safe = rgbColor.clamp();
        NormalizedRgbColor color = safe.normalize();

        double r = color.red();
        double g = color.green();
        double b = color.blue();

        double min = Math.min(r, Math.min(g, b));
        double max = Math.max(r, Math.max(g, b));
        double delta = max - min;

        // Hue depends on which RGB channel has the highest value.
        if (delta == 0) {
            h = 0;
        } else if (max == r) {
            h = 60 * (((g - b) / delta) % 6);
        } else if (max == g) {
            h = 60 * (((b - r) / delta) + 2);
        } else {
            h = 60 * (((r - g) / delta) + 4);
        }

        // Saturation is zero for black because there is no color intensity.
        if (max == 0) {
            s = 0;
        } else {
            s = delta / max;
        }

        // Value is the strongest normalized RGB channel.
        v = max;

        return new HsvColor(h, s, v).clamp();
    }
    
    /**
     * Converts an HSV color to RGB.
     *
     * <p>The input HSV color is clamped before conversion. The returned RGB
     * values are in the 0..255 range, but callers may still clamp the result
     * before packing it into an ARGB pixel.</p>
     *
     * @param hsvColor source HSV color
     * @return converted RGB color
     */
    public RgbColor hsvToRgb(HsvColor hsvColor) {
        double r = 0;
        double g = 0;
        double b = 0;

        HsvColor color = hsvColor.clamp();
        double h = color.hue();
        double s = color.saturation();
        double v = color.value();
        
        // Chroma is the color intensity without the final brightness offset.
        double c = v * s;

        // HSV hue is split into six 60-degree RGB sectors.
        int hSector = (int) (h / 60);

        // Intermediate channel used between two neighboring sectors.
        double x = c * (1 - Math.abs((h / 60) % 2 - 1));

        switch (hSector % 6) {
            case 0 -> { r = c; g = x; b = 0; }
            case 1 -> { r = x; g = c; b = 0; }
            case 2 -> { r = 0; g = c; b = x; }
            case 3 -> { r = 0; g = x; b = c; }
            case 4 -> { r = x; g = 0; b = c; }
            case 5 -> { r = c; g = 0; b = x; }
            default -> {}
        }

        // Add the brightness offset back to all channels.
        double m = v - c;
        r = r + m;
        g = g + m;
        b = b + m;

        // Convert normalized channels back to display RGB values.
        r = r * 255;
        g = g * 255;
        b = b * 255;


        return new RgbColor(r, g, b);
    }

}
