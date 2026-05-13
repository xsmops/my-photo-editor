package com.example.photoeditor.controller;

import com.example.photoeditor.service.AdjustColor;
import com.example.photoeditor.service.FilterParams;
import com.example.photoeditor.service.ImageProcessor;

import com.example.photoeditor.service.PreviewImage;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller for the editor screen.
 *
 * <p>This controller reads slider values from the UI, creates filter
 * parameters, sends the original image to {@link ImageProcessor}, and displays
 * the processed result in the image view.</p>
 *
 * <p>The original image is stored separately so filters are always applied to
 * the same source image. This prevents repeated processing from damaging the
 * image when a slider is moved many times.</p>
 */
public class EditorController {
    final private ImageProcessor processor = new ImageProcessor();
    final private PreviewImage previewGenerator = new PreviewImage();

    private Image originalImage;
    private Image previewImage;

    @FXML
    private ImageView imageView;

    @FXML
    private Slider brightnessSlider;

    @FXML
    private Slider contrastSlider;

    @FXML
    private Slider saturationSlider;

    /**
     * Initializes editor controls after the FXML file is loaded.
     *
     * <p>Every slider change triggers a new image processing run.</p>
     */
    @FXML
    public void initialize() {
        saturationSlider.valueProperty().addListener((obs, oldVal, newVal) -> onParameterChanged());
        brightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> onParameterChanged());
        contrastSlider.valueProperty().addListener((obs, oldVal, newVal) -> onParameterChanged());
    }


    /**
     * Sets the image that will be edited.
     *
     * <p>This method should be called once when the editor screen opens. The
     * image is stored as the original source for all future processing.</p>
     *
     * @param image image selected by the user
     */
    public void setImage(Image image) {
        this.originalImage = image;
        this.previewImage = previewGenerator.generate(image);
        imageView.setImage(previewImage);
    }

    /**
     * Builds filter parameters from the current slider values and refreshes
     * the displayed image.
     */
    private void onParameterChanged() {
        AdjustColor adjustColor = new AdjustColor(
                brightnessSlider.getValue(),
                contrastSlider.getValue(),
                saturationSlider.getValue()
        );
        FilterParams params = new FilterParams(adjustColor);

        // Always process the original image, not the already displayed result.
        Image outputImage = processor.run(previewImage, params);

        imageView.setImage(outputImage);

    }
}
