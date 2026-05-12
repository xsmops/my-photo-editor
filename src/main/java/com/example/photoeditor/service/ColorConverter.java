package com.example.photoeditor.service;

import com.example.photoeditor.Model.color.HsvColor;
import com.example.photoeditor.Model.color.NormalizedRgbColor;
import com.example.photoeditor.Model.color.RgbColor;
import javafx.scene.image.PixelReader;

public class ColorConverter {
    public ColorConverter(double i1, double i2, double i3){}

    private HsvColor rgbToHsv(RgbColor rgbColor) {
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

        // Calculate Hue h:
        if (delta == 0) {
            h = 0;
        } else if (max == r) {
            h = 60 * (((g - b) / delta) % 6);
        } else if (max == g) {
            h = 60 * (((b - r) / delta) + 2);
        } else {
            h = 60 * (((r - g) / delta) + 4);
        }

        // Calculate Saturation s:
        if (max == 0) {
            s = 0;
        } else {
            s = delta / max;
        }

        // Calculate Value v:
        v = max;

        // Compile and return HSV Color:
        return new HsvColor(h, s, v).clamp();
    }
    
    private RgbColor hsvToRgb(HsvColor hsvColor) {
        // Initialize RGB channels
        double r = 0; // red
        double g = 0; // green
        double b = 0; // blue

        // Color clamp and split
        HsvColor color = hsvColor.clamp();
        double h = color.hue();
        double s = color.saturation();
        double v = color.value();
        
        // Calculate Chroma
        double c = v * s;

        // Calculate Hue sector
        int hSector = (int) (h / 60);

        // Calculate intermediate value
        double x = c * (1 - Math.abs((hSector % 2) - 1));

        // Determine RGB values based on Sector hSector
        switch (hSector % 6) {
            case 0 -> { r = c; g = x; b = 0; }
            case 1 -> { r = x; g = c; b = 0; }
            case 2 -> { r = 0; g = c; b = x; }
            case 3 -> { r = 0; g = x; b = c; }
            case 4 -> { r = x; g = 0; b = c; }
            case 5 -> { r = c; g = 0; b = x; }
            default -> {}
        }

        // Add matching component
        double m = v - c;

        // Match the channel
        r = r + m;
        g = g + m;
        b = b + m;

        // Get r, b, b in 0-255 range
        r = r * 255;
        g = g * 255;
        b = b * 255;


        return new RgbColor(r, g, b);
    }

    //TODO
    // hsvEditing()
    // toRgb()
    
}
