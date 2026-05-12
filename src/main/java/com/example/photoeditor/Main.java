package com.example.photoeditor;

import com.example.photoeditor.controller.BrowserController;
import com.example.photoeditor.controller.EditorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        showBrowser();
        stage.show();
    }

    public void showBrowser() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/photoeditor/view/browser-view.fxml")
        );
        Scene scene = new Scene(loader.load());

        BrowserController browserController = loader.getController();
        browserController.setMainApp(this);

        stage.setScene(scene);
    }

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
