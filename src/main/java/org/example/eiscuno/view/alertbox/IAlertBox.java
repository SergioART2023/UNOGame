package org.example.eiscuno.view.alertbox;

public interface IAlertBox{
    /**
     * Shows a message in an alert box.
     * @param title The title of the alert box.
     * @param header The header text of the alert box.
     * @param content The content text of the alert box.
     */
    void showMessage(String title, String header, String content);
}
