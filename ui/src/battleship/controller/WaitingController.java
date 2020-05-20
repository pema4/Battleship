package battleship.controller;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WaitingController implements Initializable {
    private final Stage stage;

    public WaitingController(Stage stage) {
        this.stage = stage;
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
        var service = new WaitingForConnectionService();

        service.start();
    }

    private void showPreparingScene() {
        var loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
        loader.setControllerFactory((cls) -> new GameController());
        try {
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(true);
            stage.setMinWidth(310);
            stage.setMinHeight(440);
            stage.maxHeightProperty().bind(stage.widthProperty().add(130));
            stage.minHeightProperty().bind(stage.widthProperty().add(130));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private class WaitingForConnectionService extends Service<Object> {
        @Override
        protected Task<Object> createTask() {
            return new Task<>() {
                @Override
                protected Object call() throws Exception {
                    Thread.sleep(1000);
                    Platform.runLater(WaitingController.this::showPreparingScene);
                    return new Object();
                }
            };
        }
    }
}
