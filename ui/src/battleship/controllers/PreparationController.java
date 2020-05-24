package battleship.controllers;

import battleship.basics.*;
import battleship.controls.Field;
import battleship.controls.ShotEvent;
import battleship.models.Role;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Контроллер для экрана расстановки кораблей
 */
public class PreparationController implements Initializable {
    private final Role startingRole;
    private final ShipFactory shipFactory = new ShipFactory();
    public Label battleshipCountLabel;
    public Label cruiserCountLabel;
    public Label destroyerCountLabel;
    public Label submarineCountLabel;
    @FXML
    private StackPane fieldContainer;
    private Ocean ocean = new Ocean();
    private int prevShotX = -1;
    private int prevShotY = -1;
    @FXML
    private Blend redBlendEffect;
    @FXML
    private CheckBox isServerCheckbox;
    @FXML
    private Field field;
    @FXML
    private Button playButton;

    public PreparationController(Role startingRole) {
        this.startingRole = startingRole;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isServerCheckbox.setSelected(startingRole == Role.SERVER);
        battleshipCountLabel.textProperty().bind(
                new SimpleStringProperty("Battleships: ").concat(shipFactory.battleshipCount.asString()));
        cruiserCountLabel.textProperty().bind(
                new SimpleStringProperty("Cruisers: ").concat(shipFactory.cruiserCount.asString()));
        destroyerCountLabel.textProperty().bind(
                new SimpleStringProperty("Destroyers: ").concat(shipFactory.destroyerCount.asString()));
        submarineCountLabel.textProperty().bind(
                new SimpleStringProperty("Submarines: ").concat(shipFactory.submarineCount.asString()));

        var a = shipFactory.battleshipCount.isNotEqualTo(0);
        var b = shipFactory.cruiserCount.isNotEqualTo(0);
        var c = shipFactory.destroyerCount.isNotEqualTo(0);
        var d = shipFactory.submarineCount.isNotEqualTo(0);
        playButton.disableProperty().bind(a.or(b).or(c).or(d));

        var maxLength = Bindings.min(fieldContainer.maxHeightProperty(), fieldContainer.maxWidthProperty());
        field.maxHeightProperty().bind(maxLength);
        field.minHeightProperty().bind(maxLength);
        field.maxWidthProperty().bind(maxLength);
        field.minWidthProperty().bind(maxLength);
    }

    /**
     * Очищает поле от кораблей.
     *
     * @param actionEvent не используется
     */
    public void onAllocateButtonAction(ActionEvent actionEvent) {
        ocean = new Ocean();
        field.initField();
        shipFactory.init();
    }

    public void onPlayButtonAction(ActionEvent actionEvent) throws IOException {
        var scene = ((Node) actionEvent.getSource()).getScene();
        var stage = (Stage) scene.getWindow();
        Scene newScene;
        if (isServerCheckbox.isSelected()) {
            var loader = new FXMLLoader(getClass().getResource("/fxml/serverSettings.fxml"));
            loader.setControllerFactory(cls ->
                    new ServerSettingsController(ocean));
            newScene = new Scene(loader.load());
        } else {
            var loader = new FXMLLoader(getClass().getResource("/fxml/clientSettings.fxml"));
            loader.setControllerFactory(cls ->
                    new ClientSettingsController(ocean));
            newScene = new Scene(loader.load());
        }
        stage.setScene(newScene);
        stage.setMinHeight(0);
        stage.setMinWidth(0);
        stage.sizeToScene();
        stage.setResizable(false);
    }

    /**
     * Обработчик "выстрела" по полю
     * Выстрел по кораблю "стирает" его
     * Первый выстрел задаёт один конец корабля, второй выстрел - другой конец корабля
     *
     * @param event куда выстрелил пользователь
     */
    public void onFieldShot(ShotEvent event) {
        if (prevShotX == -1) {
            if (ocean.getShipAt(event.getX(), event.getY()) instanceof EmptySea) {
                prevShotX = event.getX();
                prevShotY = event.getY();
                field.paintCell(prevShotX, prevShotY, Field.EMPTY_CELL_BACKGROUND);
            } else {
                removeShip(event.getX(), event.getY());
            }
        } else {
            try {
                placeShip(prevShotX, prevShotY, event.getX(), event.getY());
            } catch (IllegalArgumentException ex) {
                playErrorAnimation();
                field.paintCell(prevShotX, prevShotY, Field.UNKNOWN_CELL_BACKGROUND);
            }
            prevShotX = -1;
            prevShotY = -1;
        }
    }

    /**
     * Вызывается при удалении корабля, расположенного по заданным координатам
     *
     * @param x номер строки
     * @param y номер столбца
     */
    private void removeShip(int x, int y) {
        var ship = ocean.getShipAt(x, y);
        var row = ship.getBowRow();
        var column = ship.getBowColumn();
        var length = ship.getLength();
        for (int i = 0; i < (ship.isHorizontal() ? 1 : length); ++i)
            for (int j = 0; j < (ship.isHorizontal() ? length : 1); ++j) {
                var emptySea = new EmptySea();
                emptySea.placeShipAt(row + i, column + j, true, ocean);
                field.paintCell(row + i, column + j, Field.UNKNOWN_CELL_BACKGROUND);
            }
        shipFactory.updateCounters(length, 1);
    }

    /**
     * Вызывается для расположения корабля по заданным координатам
     *
     * @param x1 номер строки начала корабля
     * @param y1 номер столбца начала корабля
     * @param x2 номер строки конца корабля
     * @param y2 номер столбца конца корабля
     */
    private void placeShip(int x1, int y1, int x2, int y2) {
        var isHorizontal = y1 != y2;
        var isVertical = x1 != x2;
        if (isHorizontal && isVertical)
            throw new IllegalArgumentException("Diagonal ships do not exist");

        var length = Math.abs(x1 - x2) + Math.abs(y1 - y2) + 1;
        var ship = shipFactory.createShip(length);
        var row = Math.min(x1, x2);
        var column = Math.min(y1, y2);
        if (!ship.okToPlaceShipAt(row, column, isHorizontal, ocean))
            throw new IllegalArgumentException("Cannot place ship here");

        ship.placeShipAt(row, column, isHorizontal, ocean);
        for (int i = 0; i < (isHorizontal ? 1 : length); ++i)
            for (int j = 0; j < (isVertical ? 1 : length); ++j)
                field.paintCell(row + i, column + j, Field.DESTROYED_SHIP_PART_BACKGROUND);

        shipFactory.updateCounters(length, -1);
    }

    /**
     * Метод, воспроизводящий анимацию ошибки (поле подсвечивается красным цветом)
     */
    private void playErrorAnimation() {
        var animation = new Timeline(
                new KeyFrame(Duration.seconds(0.00), new KeyValue(redBlendEffect.opacityProperty(), 0.2)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(redBlendEffect.opacityProperty(), 0))
        );
        animation.play();
    }

    /**
     * Заполняет поле случайным образом
     *
     * @param actionEvent не используется
     */
    public void onRandomButtonAction(ActionEvent actionEvent) {
        ocean = new Ocean();
        ocean.placeAllShipsRandomly();
        redrawField();
        shipFactory.initEmpty();
    }

    /**
     * Перерисовывает поле для текущего объекта Ocean
     */
    private void redrawField() {
        for (int i = 0; i < Ocean.OCEAN_WIDTH; ++i)
            for (int j = 0; j < Ocean.OCEAN_HEIGHT; ++j)
                if (ocean.getShipAt(i, j) instanceof EmptySea)
                    field.paintCell(i, j, Field.UNKNOWN_CELL_BACKGROUND);
                else
                    field.paintCell(i, j, Field.DESTROYED_SHIP_PART_BACKGROUND);
    }

    /**
     * Вспомогательный класс, используемый для создания кораблей заданного размера
     */
    private static class ShipFactory {
        private final IntegerProperty battleshipCount = new SimpleIntegerProperty(1);
        private final IntegerProperty cruiserCount = new SimpleIntegerProperty(2);
        private final IntegerProperty destroyerCount = new SimpleIntegerProperty(3);
        private final IntegerProperty submarineCount = new SimpleIntegerProperty(4);

        /**
         * Сбрасывает счётчики неиспользуемых кораблей
         */
        public void init() {
            battleshipCount.set(1);
            cruiserCount.set(2);
            destroyerCount.set(3);
            submarineCount.set(4);
        }

        /**
         * Обнуляет счётчики неиспользованных кораблей
         */
        public void initEmpty() {
            battleshipCount.set(0);
            cruiserCount.set(0);
            destroyerCount.set(0);
            submarineCount.set(0);
        }

        /**
         * Создаёт корабль заданного размера
         * Этот метод не изменяет счётчики
         *
         * @param length размер корабля
         * @return корабль нужного размера
         */
        public Ship createShip(int length) {
            switch (length) {
                case 1:
                    if (submarineCount.get() > 0)
                        return new Submarine();
                    break;
                case 2:
                    if (destroyerCount.get() > 0)
                        return new Destroyer();
                    break;
                case 3:
                    if (cruiserCount.get() > 0)
                        return new Cruiser();
                    break;
                case 4:
                    if (battleshipCount.get() > 0)
                        return new Battleship();
                    break;
                default:
                    throw new IllegalArgumentException(
                            String.format("Cannot create ship with length %s", length));
            }
            throw new IllegalArgumentException("Unable to create ship");
        }

        /**
         * Изменяет счётчик кораблей заданного размера на заданную величину
         *
         * @param length размер корабля
         * @param delta на сколько изменить счётчик
         */
        public void updateCounters(int length, int delta) {
            switch (length) {
                case 1:
                    submarineCount.set(submarineCount.get() + delta);
                    break;
                case 2:
                    destroyerCount.set(destroyerCount.get() + delta);
                    break;
                case 3:
                    cruiserCount.set(cruiserCount.get() + delta);
                    break;
                case 4:
                    battleshipCount.set(battleshipCount.get() + delta);
                    break;
                default:
                    throw new IllegalArgumentException("Such ship doesn't exist");
            }
        }
    }
}
