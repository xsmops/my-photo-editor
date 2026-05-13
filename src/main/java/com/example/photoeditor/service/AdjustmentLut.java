package com.example.photoeditor.service;

public class AdjustmentLut {
    private final double[] value;
    private final double[] saturation;

    public AdjustmentLut(AdjustColor params) {
        this.saturation = buildSaturationLut(params);
        this.value = buildValueLut(params);
    }

    public double applySaturation(double s) {
        int index = (int) Math.round(s * 255);
        return saturation[index];
    }

    public double applyValue(double v) {
        int index = (int) Math.round(v * 255);
        return value[index];
    }

    private double[] buildSaturationLut(AdjustColor params) {
        double[] lut = new double[256];

        for (int i = 0; i < 256; i++){
            double old = (double) i / 255;
            double newSaturation = old * (1 + params.saturation());

            lut[i] = Math.clamp(newSaturation, 0.0, 1.0);
        }

        return lut;
    }

    private double[] buildValueLut(AdjustColor params) {

        double[] lut = new double[256];
        double brightness = params.brightness();
        double contrast = params.contrast();

        for (int i = 0; i < 256; i++){
            double old = (double) i / 255;
            double newValue = old + brightness;
            double contrastFactor = 1 + contrast;
            newValue = (newValue - 0.5) * contrastFactor + 0.5;

            lut[i] = Math.clamp(newValue, 0.0, 1.0);
        }

        return lut;
    }
}
