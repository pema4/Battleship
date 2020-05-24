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

public class GreetingsController implements Initializable {
    private final Ocean ocean;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private final boolean isMyTurn;
    private final ReceiveMessageService receiveMessageService;
    private final SendMessageService sendMessageService;
    private String partnerName;
    public Button okButton;

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

    private void onMessageReceived(Object message) {
        if (message instanceof String) {
            partnerName = (String)message;
            if (myName != null) {
                sendMessageService.send(new GameStart());
                return;
            }
        } else if (message instanceof GameStart) {
            showGame(myName, partnerName);
            return;
        }

        receiveMessageService.restart();
    }

    private void onMessageSent(Object message) {
        if (message instanceof String) {
            myName = (String)message;
        } else if (message instanceof GameStart) {
            showGame(myName, partnerName);
        }
    }

    private void onDisconnect(String cause) {
        var message = String.format("%s: Stop game! Ok?", cause);
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
        Platform.exit();
    }

    private void showGame(String myName, String partnerName) {
        receiveMessageService.cancel();

        var stage = (Stage)okButton.getScene().getWindow();
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

    public void onOkButtonAction(ActionEvent actionEvent) {
        okButton.disableProperty().unbind();
        okButton.setDisable(true);

        var name = nameField.getText();
        sendMessageService.send(name);
    }

    public void onCancelButtonAction(ActionEvent actionEvent) {
        receiveMessageService.setOnFailed(null);
        sendMessageService.setOnFailed(null);
        onDisconnect(myName == null ? "Me" : myName);
    }
}
