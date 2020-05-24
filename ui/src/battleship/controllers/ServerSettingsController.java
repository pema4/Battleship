package battleship.controllers;

import battleship.basics.Ocean;
import battleship.controls.ValidatedTextField;
import battleship.services.AcceptConnectionService;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerSettingsController implements Initializable {
    private final Ocean ocean;
    private final AcceptConnectionService connection = new AcceptConnectionService();

    @FXML
    private Button connectButton;
    @FXML
    private ValidatedTextField portField;

    public ServerSettingsController(Ocean ocean) {
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
        connectButton.disableProperty().bind(portField.validProperty().not());
        connection.setOnSucceeded(t -> showGreetings(connection.getServerSocket(), connection.getValue()));
        connection.setOnFailed(t -> showError());
        portField.disableProperty().bind(connection.runningProperty());
        connectButton.textProperty().bind(
                Bindings.when(connection.runningProperty())
                        .then("Cancel")
                        .otherwise("Serve"));
    }

    private void showError() {
        new Alert(Alert.AlertType.ERROR, "can't serve", ButtonType.OK);
    }

    private void showGreetings(ServerSocket server, Socket client) {
        var stage = (Stage)connectButton.getScene().getWindow();
        stage.setOnHidden(e -> {
            try {
                server.close();
                client.close();
            } catch (IOException ignored) {
            }
        });

        var loader = new FXMLLoader(getClass().getResource("/fxml/greetings.fxml"));
        try {
            var outputStream = new ObjectOutputStream(client.getOutputStream());
            var inputStream = new ObjectInputStream(client.getInputStream());
            loader.setControllerFactory(cls ->
                new GreetingsController(ocean, inputStream, outputStream, false));
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @FXML
    private void onConnect(ActionEvent actionEvent) {
        if (connection.isRunning())
            connection.cancel();
        else {
            connection.connect(Integer.parseInt(portField.getText()));
        }
    }
}
