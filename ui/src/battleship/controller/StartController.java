package battleship.controller;

import battleship.model.Role;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartController implements Initializable {
    private ObjectProperty<Role> selectedRole;

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

    }

    public void serverButtonOnAction(ActionEvent event) throws IOException {
        var stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        var loader = new FXMLLoader(getClass().getResource("/battleship/fxml/waiting.fxml"));
        // var controller = loader.<WaitingController>getController();
        stage.setScene(new Scene(loader.load()));
    }

    public void clientButtonOnAction(ActionEvent event) throws IOException {
        var stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        var loader = new FXMLLoader(getClass().getResource("/battleship/fxml/connection.fxml"));
        // var controller = loader.<WaitingController>getController();
        stage.setScene(new Scene(loader.load()));
    }
}
