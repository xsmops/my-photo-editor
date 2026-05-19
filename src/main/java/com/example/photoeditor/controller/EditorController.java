package com.example.photoeditor.controller;

import com.example.photoeditor.service.*;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


/**
 * Controller for the editor screen.
 *
 * <p>This controller owns the editor UI state: it reads slider values, builds
 * immutable filter parameter snapshots, decides when preview or full-size
 * rendering should be requested, and displays completed render results.</p>
 *
 * <p>Actual image rendering is delegated to {@link RenderService}. The original
 * image is stored separately so full-size filters are always applied to the
 * same source image. This prevents repeated processing from damaging the image
 * when a slider is moved many times.</p>
 */
public class EditorController {
    final private PreviewImage previewGenerator = new PreviewImage();
    final private RenderService renderService = new RenderService();

    private Image originalImage;
    private Image previewImage;
    private FilterParams currentParams;



    private final Timeline previewTimeline = new Timeline(
            new KeyFrame(Duration.millis(50), event -> renderPreview())
    );
    private boolean previewIsDirty = false;


    private final PauseTransition fullSizeCooldown =
            new PauseTransition(Duration.millis(200));

    @FXML
    private ImageView imageView;

    @FXML
    private Slider brightnessSlider;

    @FXML
    private Slider contrastSlider;

    @FXML
    private Slider saturationSlider;

    @FXML
    private Slider hueSlider;

    /**
     * Initializes editor controls after the FXML file is loaded.
     *
     * <p>Slider value changes update the current filter snapshot. While the user
     * is dragging a slider, preview rendering is throttled by a timeline. After
     * slider movement stops, full-size rendering is delayed by a short cooldown
     * so quick consecutive changes do not immediately start expensive work.</p>
     */
    @FXML
    public void initialize() {
        previewTimeline.setCycleCount(Animation.INDEFINITE);
        previewTimeline.setOnFinished(event -> renderPreview());

        fullSizeCooldown.setOnFinished(event -> renderFullSize());

        saturationSlider.valueProperty().addListener((obs, oldVal, newVal) -> onParameterChanged());
        brightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> onParameterChanged());
        contrastSlider.valueProperty().addListener((obs, oldVal, newVal) -> onParameterChanged());
        hueSlider.valueProperty().addListener((obs, oldVal, newVal) -> onParameterChanged());

        saturationSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                onSliderReleased();
            }
        });
        brightnessSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                onSliderReleased();
            }
        });
        contrastSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                onSliderReleased();
            }
        });
        hueSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                onSliderReleased();
            }
        });
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
        this.currentParams = buildSnapshot();

        imageView.setImage(previewImage);
    }

    /**
     * Builds filter parameters from the current slider values and refreshes
     * preview or full-size rendering depending on the slider interaction state.
     */
    private void onParameterChanged() {
        currentParams = buildSnapshot();

        fullSizeCooldown.stop();

        if (isAnySliderChanging()) {
            previewIsDirty = true;

            if (previewTimeline.getStatus() != Animation.Status.RUNNING) {
                previewTimeline.play();
            }
        } else {
            previewTimeline.stop();
            fullSizeCooldown.stop();
            fullSizeCooldown.playFromStart();
        }

    }

    /**
     * Handles the end of slider interaction.
     *
     * <p>When all sliders are released, preview rendering stops and a delayed
     * full-size render is scheduled with the latest filter parameters.</p>
     */
    private void onSliderReleased() {
        if (!isAnySliderChanging()) {
            previewTimeline.stop();

            currentParams = buildSnapshot();

            fullSizeCooldown.stop();
            fullSizeCooldown.playFromStart();
        }
    }

    /**
     * Checks whether any editor slider is currently being dragged by the user.
     *
     * @return true while at least one slider is in an active drag operation
     */
    private boolean isAnySliderChanging() {
        return brightnessSlider.isValueChanging()
                || contrastSlider.isValueChanging()
                || saturationSlider.isValueChanging()
                || hueSlider.isValueChanging();
    }

    /**
     * Requests a throttled render of the downscaled preview image.
     *
     * <p>The dirty flag prevents the timeline from submitting duplicate preview
     * renders when slider values have not changed since the previous preview
     * request.</p>
     */
    private void renderPreview() {
        if (!previewIsDirty) return;

        previewIsDirty = false;

        renderService.render(
                previewImage,
                currentParams,
                this::displayRenderedImage);

    }

    /**
     * Requests a render of the original full-size image using the latest filter
     * parameters.
     */
    private void renderFullSize() {
        renderService.render(
                originalImage,
                currentParams,
                this::displayRenderedImage);

    }

    /**
     * Displays a completed render result without changing the original image or
     * rebuilding the preview source.
     *
     * @param image processed image returned by the render service
     */
    private void displayRenderedImage(Image image) {
        imageView.setImage(image);
    }

    /**
     * Builds an immutable snapshot of the current slider values.
     *
     * @return filter parameters for a single render request
     */
    private FilterParams buildSnapshot() {
        AdjustColor adjustColor = new AdjustColor(
                brightnessSlider.getValue(),
                contrastSlider.getValue(),
                saturationSlider.getValue(),
                hueSlider.getValue()
        );
        return new FilterParams(adjustColor);
    }
}
