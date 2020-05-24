package battleship.controllers;

import battleship.basics.EmptySea;
import battleship.basics.Ocean;
import battleship.controls.Field;
import battleship.controls.ShotEvent;
import battleship.models.ShotRequest;
import battleship.models.ShotResponse;
import battleship.services.ReceiveMessageService;
import battleship.services.SendMessageService;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Blend;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Контроллер для экрана игры
 */
public class GameController implements Initializable {
    private final Ocean ocean;
    private final String myName;
    private final String partnerName;
    private final SendMessageService sendMessageService;
    private final ReceiveMessageService receiveMessageService;
    private final boolean[][] alreadyShot = new boolean[Ocean.OCEAN_WIDTH][Ocean.OCEAN_HEIGHT];
    private boolean isMyTurn;
    private int myShots = 0;

    @FXML
    private Blend redBlendEffect;

    @FXML
    private Field myField;

    @FXML
    private Field partnerField;

    @FXML
    private TextArea logTextArea;

    public GameController(Ocean ocean, String myName, String partnerName, ObjectInputStream inputStream, ObjectOutputStream outputStream, boolean isMyTurn) {
        this.ocean = ocean;
        this.myName = myName;
        this.partnerName = partnerName;
        this.isMyTurn = isMyTurn;
        receiveMessageService = new ReceiveMessageService(inputStream);
        sendMessageService = new SendMessageService(outputStream);
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindOutput();
        receiveMessageService.setOnSucceeded(t -> onMessageReceived(receiveMessageService.getValue()));
        sendMessageService.setOnSucceeded(t -> onMessageSent(sendMessageService.getValue()));
        receiveMessageService.setOnFailed(t -> onDisconnect(partnerName));
        sendMessageService.setOnFailed(t -> onDisconnect(partnerName));
        for (int i = 0; i < Ocean.OCEAN_WIDTH; ++i)
            for (int j = 0; j < Ocean.OCEAN_HEIGHT; ++j)
                if (ocean.getShipAt(i, j) instanceof EmptySea)
                    myField.paintCell(i, j, Field.UNKNOWN_CELL_BACKGROUND);
                else
                    myField.paintCell(i, j, Field.DESTROYED_SHIP_PART_BACKGROUND);
        receiveMessageService.start();
    }

    /**
     * Метод, вызываемый после успешной отправки сообщения
     *
     * @param message отправленное сообщение
     */
    private void onMessageSent(Object message) {
        if (message instanceof ShotResponse) {
            // обновляем своё игровое поле
            onShotRequestReceived((ShotResponse) message);
        }
    }

    /**
     * Метод, вызываемый после получения сообщения
     *
     * @param message полученное сообщение
     */
    private void onMessageReceived(Object message) {
        if (message instanceof ShotRequest) {
            // вычисляем ответ и отправляем его
            var response = shootAt((ShotRequest) message);
            sendMessageService.send(response);

            // если игра закончилась - больше не принимаем никакие запросы
            if (response.getType() == ShotResponse.Type.SUNK_ALL)
                return;
        } else if (message instanceof ShotResponse) {
            // получаем ответ и обновляем игровое поле противника
            var response = (ShotResponse) message;
            onShotResponseReceived(response);

            // если игра закончилась - больше не принимаем сообщения
            if (response.getType() == ShotResponse.Type.SUNK_ALL)
                return;
        }

        receiveMessageService.restart();
    }

    /**
     * Метод, вызываемый после получения ответа на выстрел по полю противника
     *
     * @param response ответ противника
     */
    private void onShotResponseReceived(ShotResponse response) {
        myShots += 1;
        int x = response.getRequest().getX();
        int y = response.getRequest().getY();
        switch (response.getType()) {
            case MISS:
                partnerField.paintCell(x, y, Field.EMPTY_CELL_BACKGROUND);
                break;
            case HIT:
                partnerField.paintCell(x, y, Field.DESTROYED_SHIP_PART_BACKGROUND);
                break;
            case SUNK:
            case SUNK_ALL:
                var ship = response.getSunkShip();
                for (int i = 0; i < (ship.isHorizontal() ? 1 : ship.getLength()); ++i)
                    for (int j = 0; j < (ship.isHorizontal() ? ship.getLength() : 1); ++j)
                        partnerField.paintCell(ship.getBowRow() + i, ship.getBowColumn() + j, Field.DESTROYED_WHOLE_SHIP_BACKGROUND);
        }

        System.out.println(String.format("%s: (%d, %d) = %s", partnerName, y, x, response.getType()));

        // Если игра закончилась - показываем экран с результатами игры
        if (response.getType() == ShotResponse.Type.SUNK_ALL)
            showGameResults(myName);
    }

    /**
     * Метод вызываемый после отправки ответа на выстрел по нашему полю
     *
     * @param response наш ответ
     */
    private void onShotRequestReceived(ShotResponse response) {
        int x = response.getRequest().getX();
        int y = response.getRequest().getY();
        switch (response.getType()) {
            case MISS:
                myField.paintCell(x, y, Field.EMPTY_CELL_BACKGROUND);
                break;
            case HIT:
            case SUNK:
            case SUNK_ALL:
                myField.paintCell(x, y, Field.DESTROYED_WHOLE_SHIP_BACKGROUND);
                break;
        }

        System.out.println(String.format("%s: (%d, %d) = %s", myName, y, x, response.getType()));

        if (response.getType() == ShotResponse.Type.SUNK_ALL)
            showGameResults(partnerName);
        else
            isMyTurn = true;
    }

    /**
     * Метод, стреляющий по нашему объекту Ocean и возвращающий результат
     *
     * @param request куда стрелять
     * @return ответ на выстрел
     */
    private ShotResponse shootAt(ShotRequest request) {
        int x = request.getX();
        int y = request.getY();

        var isSuccessfulShot = ocean.shootAt(x, y);
        var shotShip = ocean.getShipAt(x, y);
        if (ocean.isGameOver())
            return ShotResponse.sunkAll(request, shotShip);
        else if (ocean.getShipAt(x, y).isSunk())
            return ShotResponse.sunk(request, shotShip);
        else if (isSuccessfulShot)
            return ShotResponse.hit(request);
        else
            return ShotResponse.miss(request);
    }

    /**
     * Перенаправляет stdout в TextField
     */
    private void bindOutput() {
        var out = new OutputStream() {
            private final StringBuilder currentLine = new StringBuilder();

            @Override
            public void write(int i) {
                currentLine.appendCodePoint((char) i);
                if (i == '\n') {
                    logTextArea.appendText(currentLine.toString());
                    currentLine.setLength(0);
                }
            }
        };
        System.setOut(new PrintStream(out, true));
    }

    /**
     * Проигрывает анимацию ошибки
     */
    private void highlightError() {
        var animation = new Timeline(
                new KeyFrame(Duration.seconds(0.05), new KeyValue(redBlendEffect.opacityProperty(), 0.3)),
                new KeyFrame(Duration.seconds(0.7), new KeyValue(redBlendEffect.opacityProperty(), 0))
        );
        animation.play();
    }

    /**
     * Вызывается при выстреле по полю противника
     *
     * @param shotEvent куда произведён выстрел
     */
    public void onShotHandler(ShotEvent shotEvent) {
        int x = shotEvent.getX();
        int y = shotEvent.getY();
        if (isMyTurn && !alreadyShot[x][y]) {
            isMyTurn = false;
            alreadyShot[x][y] = true;
            sendMessageService.send(new ShotRequest(x, y));
        } else
            highlightError();
    }

    /**
     * Показывает экран с результатами игры
     *
     * @param winnerName имя победителя
     */
    private void showGameResults(String winnerName) {
        var stage = (Stage) myField.getScene().getWindow();
        var loader = new FXMLLoader(getClass().getResource("/fxml/results.fxml"));
        try {
            loader.setControllerFactory(cls ->
                    new ResultsController(winnerName, myShots, ocean.getShotsFired()));
            stage.setScene(new Scene(loader.load()));
            stage.sizeToScene();
            stage.setResizable(false);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Обработчик нажатия кнопки отмена
     *
     * @param actionEvent не используется
     */
    public void onCancelAction(ActionEvent actionEvent) {
        // эти обработчики вызывать не нужно
        receiveMessageService.setOnFailed(null);
        sendMessageService.setOnFailed(null);
        onDisconnect(myName);
    }
}
