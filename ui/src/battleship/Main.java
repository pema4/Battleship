package battleship;

import battleship.controllers.PreparationController;
import battleship.models.Role;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        var parameters = getParameters().getRaw();
        Parent root;
        if (parameters.size() == 1) {
            var parameter = parameters.get(0).toLowerCase();
            switch (parameter) {
                case "server":
                    stage.setResizable(true);
                    stage.setMinWidth(370);
                    stage.setMinHeight(535);
                    root = loadPreparationScene(Role.SERVER);
                    break;
                case "client":
                    stage.setResizable(true);
                    stage.setMinWidth(370);
                    stage.setMinHeight(535);
                    root = loadPreparationScene(Role.CLIENT);
                    break;
                default:
                    System.out.println("Wrong argument");
                    return;
            }
        } else {
            root = FXMLLoader.load(getClass().getResource("/fxml/start.fxml"));
        }

        stage.setTitle("Battleship");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    private Parent loadPreparationScene(Role role) throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/fxml/preparation.fxml"));
        loader.setControllerFactory(cls ->
                new PreparationController(role));
        return loader.load();
    }
}
