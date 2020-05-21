package battleship;

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
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/start.fxml"));
        primaryStage.setTitle("Battleship");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        //primaryStage.initStyle(StageStyle.UTILITY);

        //primaryStage.maxHeightProperty().bind(primaryStage.widthProperty().add(130));
        //primaryStage.minHeightProperty().bind(primaryStage.widthProperty().add(130));
        // как я понял, в javafx нельзя оставить только растягивание по диагонали. приходится делать как-то так

        primaryStage.show();
    }
}
