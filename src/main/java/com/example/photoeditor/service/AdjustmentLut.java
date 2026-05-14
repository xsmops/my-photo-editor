package com.example.photoeditor.service;

/**
 * Precomputes adjustment values for saturation and brightness/value changes.
 *
 * <p>LUT means "lookup table". Instead of recalculating the same adjustment
 * formulas for every pixel, this class calculates all possible normalized
 * input values once for the current slider parameters.</p>
 *
 * <p>The tables use 256 entries because HSV saturation and value are normalized
 * to 0..1, but image channels are commonly represented as 0..255 steps.</p>
 *
 * <p>Instances are built once per image processing run and then shared between
 * worker threads. The lookup arrays are filled during construction and only
 * read afterwards, so the parallel image tasks can use the same instance.</p>
 */
public class AdjustmentLut {
    private final double[] value;
    private final double[] saturation;

    /**
     * Builds lookup tables for the given adjustment parameters.
     *
     * @param params brightness, contrast, and saturation values
     */
    public AdjustmentLut(AdjustColor params) {
        this.saturation = buildSaturationLut(params);
        this.value = buildValueLut(params);
    }

    /**
     * Applies the precomputed saturation adjustment.
     *
     * @param s original HSV saturation in the 0..1 range
     * @return adjusted saturation in the 0..1 range
     */
    public double applySaturation(double s) {
        int index = (int) Math.round(s * 255);
        return saturation[index];
    }

    /**
     * Applies the precomputed brightness and contrast adjustment.
     *
     * @param v original HSV value in the 0..1 range
     * @return adjusted value in the 0..1 range
     */
    public double applyValue(double v) {
        int index = (int) Math.round(v * 255);
        return value[index];
    }

    /**
     * Builds a table that maps every possible normalized saturation step to
     * its adjusted value.
     */
    private double[] buildSaturationLut(AdjustColor params) {
        double[] lut = new double[256];

        for (int i = 0; i < 256; i++){
            // Convert table index 0..255 into normalized saturation 0..1.
            double old = (double) i / 255;
            double newSaturation = old * (1 + params.saturation());

            lut[i] = Math.clamp(newSaturation, 0.0, 1.0);
        }

        return lut;
    }

    /**
     * Builds a table that maps every possible normalized value step to its
     * brightness and contrast adjusted value.
     */
    private double[] buildValueLut(AdjustColor params) {

        double[] lut = new double[256];
        double brightness = params.brightness();
        double contrast = params.contrast();

        for (int i = 0; i < 256; i++){
            // Convert table index 0..255 into normalized value 0..1.
            double old = (double) i / 255;
            double newValue = old + brightness;
            double contrastFactor = 1 + contrast;

            // Contrast changes the distance from the middle brightness value.
            newValue = (newValue - 0.5) * contrastFactor + 0.5;

            lut[i] = Math.clamp(newValue, 0.0, 1.0);
        }

        return lut;
    }
}
