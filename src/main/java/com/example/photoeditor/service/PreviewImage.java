package com.example.photoeditor.service;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class PreviewImage {

    public Image generate(Image source) {
        // Preview scales to 800px width only if the original is bigger
        int previewWidth = (int) Math.min(800, source.getWidth());
        double scale = source.getWidth() / previewWidth;
        // Calculate height of preview related to preview's width
        int previewHeight = (int) (source.getHeight() / scale);



        WritableImage output = new WritableImage(previewWidth, previewHeight);

        PixelReader reader = source.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < previewHeight; y++) {
            for (int x = 0; x < previewWidth; x++) {
                // Find the corresponding pixel in original
                int origX = (int) (x * scale);
                int origY = (int) (y * scale);

                // To safe that it is not out of bounds
                if (origX > source.getWidth())
                    origX = (int) (source.getWidth() - 1);
                if (origY > source.getHeight())
                    origY = (int) (source.getHeight() - 1);

                // Get a color of an origin pixel
                int color = reader.getArgb(origX, origY);
                // Write this into the preview matrix
                writer.setArgb(x, y, color);

            }
        }


        return output;
    }
}
