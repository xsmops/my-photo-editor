package com.example.photoeditor;

import com.example.photoeditor.controller.BrowserController;
import com.example.photoeditor.controller.EditorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main JavaFX application class.
 *
 * <p>This class starts the application and switches between the browser screen
 * and the editor screen. It also passes the selected image from the browser
 * controller to the editor controller.</p>
 */
public class Main extends Application {

    private Stage stage;

    /**
     * Starts the JavaFX application.
     *
     * @param stage primary application window
     * @throws Exception if the initial FXML screen cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        showBrowser();
        stage.show();
    }

    /**
     * Loads and displays the browser screen.
     *
     * @throws Exception if the browser FXML file cannot be loaded
     */
    public void showBrowser() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/photoeditor/view/browser-view.fxml")
        );
        Scene scene = new Scene(loader.load());

        BrowserController browserController = loader.getController();
        browserController.setMainApp(this);

        stage.setScene(scene);
    }

    /**
     * Loads the editor screen and passes the selected image to it.
     *
     * @param image image selected by the user
     * @throws Exception if the editor FXML file cannot be loaded
     */
    public void initializeEditor(Image image) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/photoeditor/view/editor-view.fxml")
        );
        Scene scene = new Scene(loader.load());

        EditorController editorController = loader.getController();
        editorController.setImage(image);

        stage.setScene(scene);
    }
}
