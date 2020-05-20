package battleship.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class FieldBase extends GridPane {
    public static final Paint UNKNOWN_CELL_COLOR = Color.LIGHTGRAY;
    public static final Paint EMPTY_CELL_COLOR = Color.LIGHTGREEN;
    public static final Paint DESTROYED_SHIP_PART_COLOR = Color.LIGHTBLUE;
    public static final Paint DESTROYED_WHOLE_SHIP_COLOR = Color.LIGHTPINK;
    private final int width;
    private final int height;
    private final Rectangle[][] oceanCells;
    private final ObjectProperty<EventHandler<ShotEvent>> onShot = new ObjectPropertyBase<>() {
        @Override
        protected void invalidated() {
            setEventHandler(ShotEvent.SHOT, get());
        }

        @Override
        public Object getBean() {
            return FieldBase.this;
        }

        @Override
        public String getName() {
            return "onShot";
        }
    };

    private int lastEnteredDigit = -1;

    public FieldBase(int width, int height) {
        //alignment="CENTER" hgap="2" maxWidth="${gameField.height}" vgap="2"
        this.width = width;
        this.height = height;
        oceanCells = new Rectangle[width][height];
        createGameField();
    }

    public FieldBase() {
        this(10, 10);
    }

    /**
     * Created a game field and fills it with cells.
     */
    private void createGameField() {
        add(createGameFieldLabel("X\\Y"), 0, 0);

        for (int i = 0; i < width; ++i)
            add(createGameFieldLabel(String.valueOf(i)), i + 1, 0);

        for (int j = 0; j < height; ++j)
            add(createGameFieldLabel(String.valueOf(j)), 0, j + 1);

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                Rectangle cell = createFieldCell();
                addCellHandlers(cell, i, j);
                oceanCells[i][j] = cell;
                add(cell, i + 1, j + 1);
            }
        }
    }

    /**
     * Creates a label with given text, centered in parent.
     *
     * @param value text value.
     * @return centered label.
     */
    private Label createGameFieldLabel(String value) {
        var label = new Label(value);
        GridPane.setValignment(label, VPos.CENTER);
        GridPane.setHalignment(label, HPos.CENTER);
        return label;
    }

    /**
     * Creates a Rectangle, representing ocean cell.
     *
     * @return rectangle, representing ocean cell.
     */
    private Rectangle createFieldCell() {
        var cell = new Rectangle();

        // Add transparent border around the cell (when cell is focused, border becomes red).
        cell.setStrokeWidth(2);
        cell.setStroke(Color.TRANSPARENT);

        // Some fancy effect
        var effect = new InnerShadow();
        effect.setColor(Color.grayRgb(0, 0.1));
        cell.setEffect(effect);

        // Set size (a lot of костыли there)
        cell.widthProperty().bind(widthProperty().divide(width).subtract(10));
        cell.heightProperty().bind(heightProperty().divide(height).subtract(10));
        cell.widthProperty().bind(cell.heightProperty());

        return cell;
    }

    /**
     * Sets needed handlers for given cell events.
     *
     * @param cell Rectangle, representing ocean cell.
     * @param x    first coordinate of the cell.
     * @param y    second coordinate of the cell.
     */
    private void addCellHandlers(Rectangle cell, int x, int y) {
        // Enable clicking on cells.
        cell.setOnMousePressed(mouseEvent -> {
            fireEvent(new ShotEvent(x, y));
            cell.requestFocus();
        });

        // Enable traversing with keys
        cell.setFocusTraversable(true);
        cell.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue)
                cell.setStroke(Color.RED);
            else
                cell.setStroke(Color.TRANSPARENT);
        });

        // Enable keyboard support
        cell.setOnKeyPressed(keyEvent -> {
            var code = keyEvent.getCode();

            if (code.isDigitKey()) {
                var digit = Integer.parseInt(code.getChar());
                if (lastEnteredDigit != -1) {
                    System.out.printf("Entered Y coordinate: %d\n", digit);
                    fireEvent(new ShotEvent(digit, lastEnteredDigit));
                    oceanCells[digit][lastEnteredDigit].requestFocus();
                    lastEnteredDigit = -1;
                } else {
                    System.out.printf("Entered X coordinate: %d\n", digit);
                    lastEnteredDigit = digit;
                }
            } else
                lastEnteredDigit = -1;

            if (code.equals(KeyCode.SPACE) || code.equals(KeyCode.ENTER))
                fireEvent(new ShotEvent(x, y));
        });
    }

    public EventHandler<ShotEvent> getOnShot() {
        return onShot.get();
    }

    public void setOnShot(EventHandler<ShotEvent> handler) {
        onShot.set(handler);
    }

    public ObjectProperty<EventHandler<ShotEvent>> onShotProperty() {
        return onShot;
    }

    public void paintCell(int x, int y, Paint paint) {
        oceanCells[x][y].setFill(paint);
    }
}
