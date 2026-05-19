package com.example.photoeditor.service;

/**
 * Precomputes adjustment values for hue, saturation, and brightness/value
 * changes.
 *
 * <p>LUT means "lookup table". Instead of recalculating the same adjustment
 * formulas for every pixel, this class calculates all possible input values
 * once for the current slider parameters.</p>
 *
 * <p>The saturation and value tables use 256 entries because HSV saturation
 * and value are normalized to 0..1, but image channels are commonly represented
 * as 0..255 steps. The hue table uses 360 entries because HSV hue is measured
 * in degrees.</p>
 *
 * <p>Instances are built once per image processing run and then shared between
 * worker threads. The lookup arrays are filled during construction and only
 * read afterwards, so the parallel image tasks can use the same instance.</p>
 */
public class AdjustmentLut {
    private final double[] value;
    private final double[] saturation;
    private final double[] hue;

    /**
     * Builds lookup tables for the given adjustment parameters.
     *
     * @param params brightness, contrast, saturation, and hue values
     */
    public AdjustmentLut(AdjustColor params) {
        this.saturation = buildSaturationLut(params);
        this.value = buildValueLut(params);
        this.hue = buildHueLut(params);
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
     * Applies the precomputed hue adjustment.
     *
     * <p>The input hue is rounded to the nearest integer degree and wrapped
     * with the modulo operator so an input of 360 maps back to index 0.</p>
     *
     * @param h original HSV hue in degrees (0..360)
     * @return adjusted hue in degrees; may fall outside 0..360 and will be
     *         wrapped by {@link com.example.photoeditor.Model.color.HsvColor#clamp()}
     */
    public double applyHue(double h) {
        int idx = ((int) Math.round(h)) % 360;
        return hue[idx];
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
     * Builds a table that maps every integer hue degree to its shifted value.
     *
     * <p>The slider value is interpreted as a rotation of up to ±180 degrees
     * around the HSV color wheel. The shift is added without wrap-around
     * because {@link com.example.photoeditor.Model.color.HsvColor#clamp()}
     * already wraps hue back into the 0..360 range after the pixel is built.</p>
     */
    private double[] buildHueLut(AdjustColor params) {
        double[] lut = new double[360];
        double shift = params.hue() * 180;

        for (int i = 0; i < 360; i++) {
            lut[i] = i + shift;
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
