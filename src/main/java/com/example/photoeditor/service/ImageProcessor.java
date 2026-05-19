package com.example.photoeditor.service;

import com.example.photoeditor.Model.color.HsvColor;
import com.example.photoeditor.Model.color.RgbColor;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Applies image filters to JavaFX images.
 *
 * <p>The processor reads pixels from the source image, converts each pixel
 * from RGB to HSV, applies adjustment parameters through lookup tables,
 * converts the result back to RGB, and writes it into a new output image.</p>
 *
 * <p>Pixel calculation is parallelized by splitting the image into horizontal
 * row ranges. Each worker thread reads from the shared source pixel array and
 * writes to its own row range in the result pixel array.</p>
 *
 * <p>JavaFX image access is kept outside the worker threads: pixels are read
 * into a plain integer array before parallel processing, and the processed
 * array is written back to {@link WritableImage} after all workers finish.</p>
 *
 * <p>This class does not update the UI directly. The caller receives the
 * processed image and decides where to display it.</p>
 */
public class ImageProcessor {

    private final ColorConverter converter = new ColorConverter();

    /**
     * Processes the source image with the given filter parameters.
     *
     * <p>The method uses three stages:</p>
     * <ol>
     *     <li>Read source image pixels into an integer array.</li>
     *     <li>Process separate row ranges in parallel worker tasks.</li>
     *     <li>Write the processed pixel array into a new JavaFX image.</li>
     * </ol>
     *
     * <p>Worker task failures are converted to runtime exceptions so UI
     * controllers do not need to handle concurrency-specific checked
     * exceptions.</p>
     *
     * @param sourceImage image used as the processing source
     * @param params filter parameters collected from the editor UI
     * @return a new image with all filters applied
     */
    public Image run(Image sourceImage, FilterParams params) {
        int width = (int) sourceImage.getWidth();
        int height = (int) sourceImage.getHeight();

        int[] sourcePixels = new int[width * height];
        int[] resultPixels = new int[width * height];

        WritableImage outputImage = new WritableImage(width, height);

        PixelReader reader = sourceImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        // Build lookup tables once per image processing run.
        AdjustmentLut lut = new AdjustmentLut(params.adjustColor());

        // Copy JavaFX image pixels into an array before parallel processing.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sourcePixels[x + y * width] = reader.getArgb(x, y);
            }
        }

        int threadCount = Runtime.getRuntime().availableProcessors();
        int rowsPerThread = height / threadCount;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> tasks = new ArrayList<>();


        for (int i = 0; i < threadCount; i++) {
            // Each task owns one horizontal row range of the image.
            int yStart = rowsPerThread * i;
            int yEnd = (i == threadCount - 1)
                    ? height
                    : rowsPerThread * (i + 1);

            Future<?> task = executor.submit(() -> {
                for (int y = yStart; y < yEnd; y++) {
                    for (int x = 0; x < width; x++) {
                        // The index maps a two-dimensional pixel position into
                        // the one-dimensional source and result arrays.
                        int processedPixel = processPixel(sourcePixels[x + y * width], lut);
                        resultPixels[x + y * width] = processedPixel;
                    }
                }
            });

            tasks.add(task);

        }

        try {
            // Wait until every worker finishes before writing the output image.
            for (Future<?> task : tasks) {
                task.get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Image processing was interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Image processing failed", e);
        } finally {
            executor.shutdown();
        }



        // Write processed pixels back into a JavaFX image after all tasks finish.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setArgb(x, y, resultPixels[x + y * width]);
            }
        }

        return outputImage;

    }

    /**
     * Applies the current filter pipeline to one ARGB pixel.
     *
     * <p>The lookup table is passed in from the image loop so it is not rebuilt
     * for every pixel.</p>
     */
    private int processPixel(int argb, AdjustmentLut lut) {
        int alpha = extractAlpha(argb);

        HsvColor hsvColor = converter.rgbToHsv(extractRgb(argb));

        HsvColor adjusted = applyAdjustments(hsvColor, lut);
        RgbColor rgbColor = converter.hsvToRgb(adjusted).clamp();


        return packArgb(alpha, rgbColor);
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
     * Applies brightness, contrast, and saturation to an HSV color through
     * precomputed lookup tables.
     */
    private HsvColor applyAdjustments(HsvColor hsv, AdjustmentLut lut) {
        double newH = lut.applyHue(hsv.hue());
        double newS = lut.applySaturation(hsv.saturation());
        double newV = lut.applyValue(hsv.value());

        return new HsvColor(newH, newS, newV).clamp();

    }
}
