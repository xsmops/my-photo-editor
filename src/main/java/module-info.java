module com.example.photoeditor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    exports com.example.photoeditor;

    exports com.example.photoeditor.controller;
    opens com.example.photoeditor to javafx.fxml;
    opens com.example.photoeditor.controller to javafx.fxml;
}