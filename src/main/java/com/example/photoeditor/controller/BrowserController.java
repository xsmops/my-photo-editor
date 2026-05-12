package com.example.photoeditor.controller;

import com.example.photoeditor.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class BrowserController {

    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void browseFile(ActionEvent event) {

        if (mainApp == null) {
            throw new IllegalStateException("Main application reference is not set. Did you forget to call setMainApp()?");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pick an image");

        // Filter : Only images
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Images", "*.png", "*.jpeg", "*.jpg"
                )
        );

        Window window = ((Node) event.getSource())
                .getScene()
                .getWindow();

        File file = fileChooser.showOpenDialog(window);

        // Check up for the chosen file
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

    private void showError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("File can't be opened");
        alert.showAndWait();
    }
}
