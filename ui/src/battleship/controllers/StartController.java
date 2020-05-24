package battleship.controllers;

import battleship.models.Role;
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

/**
 * Контроллер стартового экрана
 * Этот экран показывается, если приложение не запущено с аргументом, указывающим роль
 */
public class StartController {
    private ObjectProperty<Role> selectedRole;

    public void serverButtonOnAction(ActionEvent event) throws IOException {
        var stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        startPreparationScene(stage, Role.SERVER);
    }

    public void clientButtonOnAction(ActionEvent event) throws IOException {
        var stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        startPreparationScene(stage, Role.CLIENT);
    }

    private void startPreparationScene(Stage stage, Role role) throws IOException {
        stage.setResizable(true);
        stage.setMinWidth(370);
        stage.setMinHeight(535);
        var loader = new FXMLLoader(getClass().getResource("/fxml/preparation.fxml"));
        loader.setControllerFactory(cls ->
                new PreparationController(role));
        stage.setScene(new Scene(loader.load()));
    }
}
