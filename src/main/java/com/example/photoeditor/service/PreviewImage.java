package com.example.photoeditor.service;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Creates a smaller image used for fast editor previews.
 *
 * <p>The preview keeps the original aspect ratio and limits width to 800 pixels.
 * Smaller images keep their original dimensions. The generated image is used
 * while sliders are being dragged so expensive full-size rendering can be
 * delayed until the user stops changing parameters.</p>
 */
public class PreviewImage {

    /**
     * Generates a downscaled preview copy of the source image.
     *
     * <p>The method uses nearest-neighbor sampling: each preview pixel is copied
     * from the corresponding source pixel. This keeps the implementation simple
     * and fast enough for the current editor pipeline.</p>
     *
     * @param source original image selected by the user
     * @return preview image with the same aspect ratio as the source
     */
    public Image generate(Image source) {
        int previewWidth = (int) Math.min(800, source.getWidth());
        double scale = source.getWidth() / previewWidth;
        int previewHeight = (int) (source.getHeight() / scale);



        WritableImage output = new WritableImage(previewWidth, previewHeight);

        PixelReader reader = source.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < previewHeight; y++) {
            for (int x = 0; x < previewWidth; x++) {
                // Map the preview pixel back to the nearest source pixel.
                int origX = (int) (x * scale);
                int origY = (int) (y * scale);

                // Clamp coordinates to avoid reading outside the source image.
                if (origX > source.getWidth())
                    origX = (int) (source.getWidth() - 1);
                if (origY > source.getHeight())
                    origY = (int) (source.getHeight() - 1);

                int color = reader.getArgb(origX, origY);
                writer.setArgb(x, y, color);

            }
        }


        return output;
    }
}
