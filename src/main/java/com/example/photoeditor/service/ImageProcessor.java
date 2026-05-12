package com.example.photoeditor.service;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

public class ImageProcessor {
    public void run(Image image, FilterParams params) {
        double brightness = params.adjustColor().brightness();
        double contrast = params.adjustColor().contrast();
        double saturation = params.adjustColor().saturation();
        System.out.println(
                "B: " + brightness +
                "C: " + contrast +
                "S: " + saturation
        );
    }

    private void applyEffect(Image img) {
        int WIDTH = (int) img.getWidth();
        int HEIGHT = (int) img.getHeight();

        PixelReader reader = img.getPixelReader();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int colorOfPixel = reader.getArgb(x, y);

                int red = (colorOfPixel >> 16) & 0xFF;
                int green = (colorOfPixel >> 8) & 0xFF;
                int blue = colorOfPixel & 0xFF;




            }
        }

    }


}
