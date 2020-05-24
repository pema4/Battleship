package battleship.controllers;

import battleship.basics.Ocean;
import battleship.controls.ValidatedTextField;
import battleship.models.GameStart;
import battleship.services.ReceiveMessageService;
import battleship.services.SendMessageService;
import javafx.application.Platform;
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
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Контроллер для экрана приветствия
 */
public class GreetingsController implements Initializable {
    private final Ocean ocean;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private final boolean isMyTurn;
    private final ReceiveMessageService receiveMessageService;
    private final SendMessageService sendMessageService;
    public Button okButton;
    private String partnerName;
    @FXML
    private ValidatedTextField nameField;
    private String myName;

    public GreetingsController(Ocean ocean, ObjectInputStream inputStream, ObjectOutputStream outputStream, boolean isMyTurn) {
        this.ocean = ocean;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.isMyTurn = isMyTurn;
        receiveMessageService = new ReceiveMessageService(inputStream);
        sendMessageService = new SendMessageService(outputStream);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        okButton.disableProperty().bind(nameField.validProperty().not());
        receiveMessageService.setOnSucceeded(t -> onMessageReceived(receiveMessageService.getValue()));
        sendMessageService.setOnSucceeded(t -> onMessageSent(sendMessageService.getValue()));
        receiveMessageService.setOnFailed(t ->
                onDisconnect(partnerName == null ? "Partner" : partnerName));
        sendMessageService.setOnFailed(t ->
                onDisconnect(partnerName == null ? "Partner" : partnerName));
        receiveMessageService.start();
    }

    /**
     * Метод, вызываемый при получении сообщения от другого игрока
     *
     * @param message полученное сообщение
     */
    private void onMessageReceived(Object message) {
        // Если пришла строка, запоминаем её - это имя соперника
        if (message instanceof String) {
            partnerName = (String) message;
            if (myName != null) {
                sendMessageService.send(new GameStart());
                return;
            }
            // Если пришёл GameStart - можно начинать игру, игрок и соперник знают имена друг друга
        } else if (message instanceof GameStart) {
            showGame(myName, partnerName);
            return;
        }

        // Запускаем сервис заново, чтобы слушать другие сообщения
        receiveMessageService.restart();
    }

    /**
     * Метод, вызываемый после успешной отправки сообщения сопернику
     *
     * @param message отправленное сообщение
     */
    private void onMessageSent(Object message) {
        // Запоминаем наше имя, которое получил соперник
        if (message instanceof String) {
            myName = (String) message;
        // После успешной отправки GameStart можно начинать игру
        } else if (message instanceof GameStart) {
            showGame(myName, partnerName);
        }
    }

    /**
     * Выводит на экран сообщение о закрытии игры и закрывает приложение
     * После этого освободятся ресурсы (классы ClientSettingsController и ServerSettingsController
     * добавили к используемому объекту Stage обработчики закрытия приложения)
     *
     * @param cause имя игрока, инициировавшего закрытие приложения
     */
    private void onDisconnect(String cause) {
        var message = String.format("%s: Stop game! Ok?", cause);
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
        Platform.exit();
    }

    /**
     * Метод, меняющий текущую сцену на экран игры
     *
     * @param myName наше имя
     * @param partnerName имя партнёра
     */
    private void showGame(String myName, String partnerName) {
        receiveMessageService.cancel();

        var stage = (Stage) okButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
        loader.setControllerFactory(cls ->
                new GameController(ocean, myName, partnerName, inputStream, outputStream, isMyTurn));
        try {
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(true);
            stage.sizeToScene();
            stage.setMinHeight(stage.getHeight());
            stage.minWidthProperty().bind(stage.heightProperty().multiply(1.0).add(50));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Обработчик нажатия кнопки ОК
     * После нажатия сопернику отправляется наше имя
     *
     * @param actionEvent не используется
     */
    public void onOkButtonAction(ActionEvent actionEvent) {
        // кнопку больше нельзя нажать
        okButton.disableProperty().unbind();
        okButton.setDisable(true);

        var name = nameField.getText();
        sendMessageService.send(name);
    }

    /**
     * Обработчик нажатия кнопки Cancel
     * После нажатия показывается диалоговое окно и приложение закрывается
     *
     * @param actionEvent не используется
     */
    public void onCancelButtonAction(ActionEvent actionEvent) {
        receiveMessageService.setOnFailed(null);
        sendMessageService.setOnFailed(null);
        onDisconnect(myName == null ? "Me" : myName);
    }
}
