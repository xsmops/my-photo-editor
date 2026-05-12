package com.example.photoeditor.service;

import com.example.photoeditor.Model.color.HsvColor;
import com.example.photoeditor.Model.color.NormalizedRgbColor;
import com.example.photoeditor.Model.color.RgbColor;
import javafx.scene.image.PixelReader;

public class ColorConverter {
    public ColorConverter(double i1, double i2, double i3){}

    private HsvColor toHsv(RgbColor rgbColor) {
        double h;
        double s;
        double v;

        RgbColor safe = rgbColor.clamp();
        NormalizedRgbColor color = safe.normalize();

        double r = color.red();
        double g = color.green();
        double b = color.blue();

        double min = Math.min(color.red(), Math.min(color.green(), color.blue()));
        double max = Math.max(color.red(), Math.max(color.green(), color.blue()));
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

    //TODO
    // hsvEditing()
    // toRgb()
    
}
