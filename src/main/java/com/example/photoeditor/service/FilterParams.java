package com.example.photoeditor.service;

/**
 * Stores all filter parameters used by the image processing pipeline.
 *
 * <p>For now it contains only basic color adjustments. More filter groups can
 * be added later without changing the method signatures in the processor.</p>
 *
 * @param adjustColor brightness, contrast, saturation, and hue parameters
 */
public record FilterParams(
        AdjustColor adjustColor
) {


}
