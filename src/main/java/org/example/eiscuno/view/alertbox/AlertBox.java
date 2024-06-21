package org.example.eiscuno.view.alertbox;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AlertBox {
    /**
     * Shows a message in an alert box with customizable width and height.
     * @param title The title of the alert box.
     * @param content The content text of the alert box.
     */
    public void showMessage(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public void showMessageError(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
