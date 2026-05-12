package com.example.photoeditor.controller;

import com.example.photoeditor.service.AdjustColor;
import com.example.photoeditor.service.FilterParams;
import com.example.photoeditor.service.ImageProcessor;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;

public class EditorController {
    final private ImageProcessor processor = new ImageProcessor();

    @FXML
    private ImageView imageView;

    @FXML
    private Slider brightnessSlider;

    @FXML
    private Slider contrastSlider;

    @FXML
    private Slider saturationSlider;

    @FXML
    public void initialize() {
        saturationSlider.valueProperty().addListener((obs, oldVal, newVal) -> onParameterChanged());
        brightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> onParameterChanged());
        contrastSlider.valueProperty().addListener((obs, oldVal, newVal) -> onParameterChanged());
    }


    public void setImage(Image image) {
        imageView.setImage(image);
    }

    private void onParameterChanged() {
        AdjustColor adjustColor = new AdjustColor(
                brightnessSlider.getValue(),
                contrastSlider.getValue(),
                saturationSlider.getValue()
        );
        FilterParams params = new FilterParams(adjustColor);
        processor.run(imageView.getImage(), params);

    }
}
