package battleship.controllers;

import battleship.basics.Ocean;
import battleship.controls.ValidatedTextField;
import battleship.services.ConnectionService;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientSettingsController implements Initializable {
    private final Ocean ocean;
    private final ConnectionService connection = new ConnectionService();
    @FXML
    private Button connectButton;
    @FXML
    private ValidatedTextField portField;
    @FXML
    private ValidatedTextField ipField;

    public ClientSettingsController(Ocean ocean) {
        this.ocean = ocean;
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectButton.disableProperty().bind(
                portField.validProperty().not().or(ipField.validProperty().not()));

        connection.setOnSucceeded(t -> showGreetings(connection.getValue()));
        connection.setOnFailed(t -> showError());
        ipField.disableProperty().bind(connection.runningProperty());
        portField.disableProperty().bind(connection.runningProperty());
        connectButton.textProperty().bind(
                Bindings.when(connection.runningProperty())
                        .then("Cancel")
                        .otherwise("Connect"));
    }

    private void showError() {
        new Alert(Alert.AlertType.ERROR, "can't connect", ButtonType.OK);
    }

    private void showGreetings(Socket server) {
        var stage = (Stage)connectButton.getScene().getWindow();
        stage.setOnHidden(e -> {
            try {
                server.close();
            } catch (IOException ignored) {
            }
        });

        var loader = new FXMLLoader(getClass().getResource("/fxml/greetings.fxml"));
        try {
            var inputStream = new ObjectInputStream(server.getInputStream());
            var outputStream = new ObjectOutputStream(server.getOutputStream());
            loader.setControllerFactory(cls ->
                    new GreetingsController(ocean, inputStream, outputStream, true));
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @FXML
    private void onConnect(ActionEvent actionEvent) {
        if (connection.isRunning())
            connection.cancel();
        else
            connection.connect(ipField.getText(), Integer.parseInt(portField.getText()));
    }
}
