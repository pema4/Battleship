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

/**
 * Контроллер для экрана с настройками сервера
 */
public class ServerSettingsController implements Initializable {
    private final Ocean ocean;
    // Логика ожидания подключения вынесена в отдельный сервис, который будет выполняться в другом потоке
    private final AcceptConnectionService connection = new AcceptConnectionService();

    @FXML
    private Button connectButton;
    @FXML
    private ValidatedTextField portField;

    public ServerSettingsController(Ocean ocean) {
        this.ocean = ocean;
    }

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

    /**
     * Выводит сообщение об ошибке
     */
    private void showError() {
        new Alert(Alert.AlertType.ERROR, "can't serve", ButtonType.OK);
    }

    /**
     * Меняет текущий экран на экран с приветсвием
     * @param server сокет сервера
     * @param client сокет клиента
     */
    private void showGreetings(ServerSocket server, Socket client) {
        var stage = (Stage)connectButton.getScene().getWindow();

        // после закрытия приложения ресурсы будут освобождены
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

    /**
     * Обработчик нажатия кнопки
     *
     * @param actionEvent не используется
     */
    @FXML
    private void onConnect(ActionEvent actionEvent) {
        if (connection.isRunning())
            connection.cancel();
        else {
            connection.connect(Integer.parseInt(portField.getText()));
        }
    }
}
