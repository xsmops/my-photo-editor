package com.example.photoeditor.controller;

import com.example.photoeditor.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

/**
 * Controller for the image browser screen.
 *
 * <p>This controller opens a file chooser, validates the selected image file,
 * loads it as a JavaFX image, and asks the main application to open the editor
 * screen.</p>
 */
public class BrowserController {

    private Main mainApp;

    /**
     * Sets the main application reference used for screen navigation.
     *
     * @param mainApp main JavaFX application instance
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Opens a file chooser and loads the selected image.
     *
     * @param event button click event from the browser screen
     */
    public void browseFile(ActionEvent event) {

        if (mainApp == null) {
            throw new IllegalStateException("Main application reference is not set. Did you forget to call setMainApp()?");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pick an image");

        // Allow only common image file types.
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Images", "*.png", "*.jpeg", "*.jpg"
                )
        );

        Window window = ((Node) event.getSource())
                .getScene()
                .getWindow();

        File file = fileChooser.showOpenDialog(window);

        // User closed the dialog without choosing a file.
        if (file == null) return;

        try {
            if (!file.exists() || !file.canRead()) {
                throw new RuntimeException("File unavailable");
            }

            Image image = new Image(file.toURI().toString());
            mainApp.initializeEditor(image);

            System.out.println("File has been chosen:" + file.getAbsolutePath());
        } catch (Exception e) {
            showError();
        }
    }

    /**
     * Shows a simple error dialog when the image cannot be opened.
     */
    private void showError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("File can't be opened");
        alert.showAndWait();
    }
}
