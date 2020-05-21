package battleship.controller;

import battleship.basics.Ocean;
import battleship.basics.Ship;
import battleship.controls.Field;
import battleship.controls.ShotEvent;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Blend;
import javafx.scene.effect.ColorInput;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Game view
 */
public class GameController implements Initializable {
    @FXML
    private Field gameField;
    /**
     * This is our model - a class from the first homework.
     */
    private Ocean ocean;

    @FXML
    private ColorInput logBlendEffectColor;

    @FXML
    private Blend logBlendEffect;

    @FXML
    private TextArea logTextArea;

    @FXML
    private Label shotsCountLabel;

    @FXML
    private Label notFoundCountLabel;

    @FXML
    private Label hitCountLabel;

    @FXML
    private Label destroyedCountLabel;

    /**
     * Tries to shoot at cell (x, y)
     *
     * @param x first coordinate of a cell
     * @param y second coordinate of a cell
     */
    private void shootAt(int x, int y) {
        if (ocean.isGameOver()) {
            startNewGame();
            return;
        }

        if (ocean.isShoot(x, y)) {
            System.out.printf("Error. You've already shot cell (%d, %d).\n", y, x);
            highlightGameLog(Color.RED);
            return;
        }

        System.out.printf("You shoot at (%d, %d)\n", y, x);

        // update gui and labels
        var isSuccessfulShot = ocean.shootAt(x, y);
        if (ocean.isGameOver())
            handleGameEnd(x, y);
        else if (ocean.getShipAt(x, y).isSunk())
            handleShipDestroyed(x, y);
        else if (isSuccessfulShot)
            handleShipHit(x, y);
        else
            handleShipNotHit(x, y);
        updateLabels();
    }

    /**
     * Prints message about game end and updates a field.
     *
     * @param x first coordinate of last hit cell
     * @param y second coordinate of last hit cell
     */
    private void handleGameEnd(int x, int y) {
        System.out.println("Response: you win. Shoot anywhere to start new game.");
        var ship = ocean.getShipAt(x, y);
        markShipDestroyed(ship);
        highlightGameLog(Color.GREEN);
    }

    /**
     * Updates UI to show that nothing at (x, y) was hit.
     *
     * @param x first coordinate of last hit cell
     * @param y second coordinate of last hit cell
     */
    private void handleShipNotHit(int x, int y) {
        System.out.println("Response: hit nothing.");
        gameField.paintCell(x, y, Field.EMPTY_CELL_COLOR);
    }

    /**
     * Updates UI to show that ship at (x, y) was hit.
     *
     * @param x first coordinate of last hit cell
     * @param y second coordinate of last hit cell
     */
    private void handleShipHit(int x, int y) {
        System.out.println("Response: hit ship.");
        gameField.paintCell(x, y, Field.DESTROYED_SHIP_PART_COLOR);
    }

    /**
     * Repaints corresponding cells in a game field with red color.
     *
     * @param ship ship that was destroyed.
     */
    private void markShipDestroyed(Ship ship) {
        for (int i = 0; i < (ship.isHorizontal() ? 1 : ship.getLength()); ++i)
            for (int j = 0; j < (ship.isHorizontal() ? ship.getLength() : 1); ++j) {
                int x = ship.getBowRow() + i;
                int y = ship.getBowColumn() + j;
                gameField.paintCell(x, y, Field.DESTROYED_WHOLE_SHIP_COLOR);
            }
    }

    /**
     * Updates UI to show that ship at (x, y) was destroyed.
     *
     * @param x first coordinate of last hit cell
     * @param y second coordinate of last hit cell
     */
    private void handleShipDestroyed(int x, int y) {
        var ship = ocean.getShipAt(x, y);
        System.out.printf("Response: destroyed a %s.\n", ship.getShipType());
        markShipDestroyed(ship);
    }

    /**
     * Method called from FXMLoader
     *
     * @param url            not used
     * @param resourceBundle not used
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindOutput();
        startNewGame();
    }

    /**
     * Binds stdout to TextArea.
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
     * Updates labels with their values.
     */
    private void updateLabels() {
        shotsCountLabel.setText(String.valueOf(ocean.getShotsFired()));
        notFoundCountLabel.setText(String.valueOf(10 - ocean.getShipsHit()));
        hitCountLabel.setText(String.valueOf(ocean.getShipsHit() - ocean.getShipsSunk()));
        destroyedCountLabel.setText(String.valueOf(ocean.getShipsSunk()));
    }

    /**
     * Handles "new game" menu button press.
     *
     * @param actionEvent mouse events.
     */
    public void startNewGameHandler(MouseEvent actionEvent) {
        if (ocean.getShipsSunk() == 10)
            startNewGame();

        var confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("New game");
        confirmation.setHeaderText("Are you sure you want to start a new game?");
        var result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK)
            startNewGame();
    }

    /**
     * Resets model and starts a new game.
     */
    private void startNewGame() {
        ocean = new Ocean();
        ocean.placeAllShipsRandomly();

        for (int i = 0; i < 10; ++i)
            for (int j = 0; j < 10; ++j)
                gameField.paintCell(i, j, Field.UNKNOWN_CELL_COLOR);

        logTextArea.clear();
        System.out.println("New game started");

        updateLabels();
    }

    /**
     * Handles "help" button press.
     *
     * @param actionEvent button press actionEvent
     */
    public void showHelpHandler(MouseEvent actionEvent) {
        var dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Welcome to \"Battleship\" game!");
        dialog.setHeaderText("Ways of shooting: ");
        dialog.setContentText("1. Click any cell to shoot;\n" +
                "2. Select cell with arrow keys and shoot with SPACE or ENTER (press ESCAPE if selection doesn't move);\n" +
                "3. Enter X and Y coordinate of a cell you want to shoot at.");
        dialog.setHeight(300);
        dialog.showAndWait();
    }

    /**
     * Highlights log area with given color.
     *
     * @param paint color to highlight with.
     */
    private void highlightGameLog(Paint paint) {
        logBlendEffectColor.setPaint(paint);
        var animation = new Timeline(
                new KeyFrame(Duration.seconds(0.05), new KeyValue(logBlendEffect.opacityProperty(), 0.3)),
                new KeyFrame(Duration.seconds(0.7), new KeyValue(logBlendEffect.opacityProperty(), 0))
        );
        animation.play();
    }

    public void onShotHandler(ShotEvent shotEvent) {
        shootAt(shotEvent.getX(), shotEvent.getY());
    }
}
