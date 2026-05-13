package com.example.photoeditor.service;

import com.example.photoeditor.Model.color.HsvColor;
import com.example.photoeditor.Model.color.RgbColor;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Applies image filters to JavaFX images.
 *
 * <p>The processor reads pixels from the source image, converts each pixel
 * from RGB to HSV, applies adjustment parameters, converts the result back to
 * RGB, and writes it into a new output image.</p>
 *
 * <p>This class does not update the UI directly. The caller receives the
 * processed image and decides where to display it.</p>
 */
public class ImageProcessor {

    private final ColorConverter converter = new ColorConverter();

    /**
     * Processes the source image with the given filter parameters.
     *
     * @param sourceImage image used as the processing source
     * @param params filter parameters collected from the editor UI
     * @return a new image with all filters applied
     */
    public Image run(Image sourceImage, FilterParams params) {
        int width = (int) sourceImage.getWidth();
        int height = (int) sourceImage.getHeight();

        WritableImage outputImage = new WritableImage(width, height);

        PixelReader reader = sourceImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        AdjustmentLut lut = new AdjustmentLut(params.adjustColor());

        // Process every pixel independently and write it into the output image.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int colorOfPixel = reader.getArgb(x, y);

                int processedPixel = processPixel(colorOfPixel, lut);

                writer.setArgb(x, y, processedPixel);
            }
        }


        return outputImage;

    }

    /**
     * Applies the current filter pipeline to one ARGB pixel.
     */
    private int processPixel(int argb, AdjustmentLut lut) {
        int alpha = extractAlpha(argb);

        HsvColor hsvColor = converter.rgbToHsv(extractRgb(argb));

        HsvColor adjusted = applyAdjustments(hsvColor, lut);
        RgbColor rgbColor = converter.hsvToRgb(adjusted).clamp();

        int processedArgb = packArgb(alpha, rgbColor);


        return processedArgb;
    }

    /**
     * Extracts the alpha channel from a packed ARGB integer.
     */
    private int extractAlpha(int argb) {
        return (argb >> 24) & 0xFF;
    }

    /**
     * Extracts RGB channels from a packed ARGB integer.
     */
    private RgbColor extractRgb(int argb) {
        return new RgbColor(
                (argb >> 16) & 0xFF,
                (argb >> 8) & 0xFF,
                argb & 0xFF
        );
    }

    /**
     * Packs an alpha channel and RGB color into a JavaFX ARGB integer.
     */
    private int packArgb(int alpha, RgbColor rgb) {
        RgbColor safe = rgb.clamp();

        int red = (int) Math.round(safe.red());
        int green = (int) Math.round(safe.green());
        int blue = (int) Math.round(safe.blue());

        return (alpha << 24) |
                (red << 16) |
                (green << 8) |
                blue;
    }

    /**
     * Applies brightness, contrast, and saturation to an HSV color.
     */
    private HsvColor applyAdjustments(HsvColor hsv, AdjustmentLut lut) {
        double h = hsv.hue();

        double newH = h; //TODO implement hue changing
        double newS = lut.applySaturation(hsv.saturation());
        double newV = lut.applyValue(hsv.value());

        return new HsvColor(newH, newS, newV).clamp();

    }
}
