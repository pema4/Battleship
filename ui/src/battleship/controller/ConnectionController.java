package battleship.controller;

import battleship.basics.Ocean;
import battleship.controls.ValidatedTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class ConnectionController implements Initializable {
    @FXML
    private Button connectButton;
    @FXML
    private ValidatedTextField portField;
    @FXML
    private ValidatedTextField ipField;

    public ConnectionController(Ocean ocean) {

    }

    public void onConnect(ActionEvent actionEvent) {

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
    }
}
