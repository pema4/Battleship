package battleship.controls;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

public class Field extends GridPane {
    public static final Background UNKNOWN_CELL_BACKGROUND =
            new Background(new BackgroundFill(Color.LIGHTGRAY, null, null));
    public static final Background EMPTY_CELL_BACKGROUND =
            new Background(new BackgroundFill(Color.LIGHTGREEN, null, null));
    public static final Background DESTROYED_SHIP_PART_BACKGROUND =
            new Background(new BackgroundFill(Color.LIGHTBLUE, null, null));
    public static final Background DESTROYED_WHOLE_SHIP_BACKGROUND =
            new Background(new BackgroundFill(Color.LIGHTPINK, null, null));
    private static final BorderStrokeStyle STROKE_STYLE = new BorderStrokeStyle(StrokeType.INSIDE, null, null, 10, 0, null);
    public static final Border TRANSPARENT_BORDER = new Border(
            new BorderStroke(Color.TRANSPARENT, STROKE_STYLE, null, new BorderWidths(2)));
    public static final Border RED_BORDER = new Border(
            new BorderStroke(Color.RED, STROKE_STYLE, null, new BorderWidths(2)));

    private final int width;
    private final int height;
    private final Pane[][] cells;
    private final Label[] leftLabels;
    private final ObjectProperty<EventHandler<ShotEvent>> onShot = new ObjectPropertyBase<>() {
        @Override
        protected void invalidated() {
            setEventHandler(ShotEvent.SHOT, get());
        }

        @Override
        public Object getBean() {
            return Field.this;
        }

        @Override
        public String getName() {
            return "onShot";
        }
    };
    private final BooleanProperty readonly = new SimpleBooleanProperty(false);
    private int lastEnteredDigit = -1;

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new Pane[width][height];
        leftLabels = new Label[height];
        createGameField();
        initField();
    }

    public Field() {
        this(10, 10);
    }

    public boolean isReadonly() {
        return readonly.get();
    }

    public void setReadonly(boolean readonly) {
        this.readonly.set(readonly);
    }

    public BooleanProperty readonlyProperty() {
        return readonly;
    }

    /**
     * Created a game field and fills it with cells.
     */
    private void createGameField() {
        for (int i = 0; i < width + 1; ++i) {
            var row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            row.setFillHeight(true);
            getRowConstraints().add(row);
        }
        for (int i = 0; i < height + 1; ++i) {
            var column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS);
            column.setFillWidth(true);
            getColumnConstraints().add(column);
        }
        setHgap(2);
        setVgap(2);

        add(createGameFieldLabel("X\\Y"), 0, 0);

        for (int i = 0; i < width; ++i)
            add(createGameFieldLabel(String.valueOf(i)), i + 1, 0);

        for (int j = 0; j < height; ++j) {
            var label = createGameFieldLabel(String.valueOf(j));
            leftLabels[j] = label;
            add(label, 0, j + 1);

        }

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                Pane cell = createFieldCell();
                addCellHandlers(cell, i, j);
                cells[i][j] = cell;
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
    private Pane createFieldCell() {
        var cell = new Pane();

        // Add transparent border around the cell (when cell is focused, border becomes red).
        cell.setBorder(TRANSPARENT_BORDER);

        // Some fancy effect
        var effect = new InnerShadow();
        effect.setColor(Color.grayRgb(0, 0.1));
        cell.setEffect(effect);

        return cell;
    }

    /**
     * Sets needed handlers for given cell events.
     *  @param cell Rectangle, representing ocean cell.
     * @param x    first coordinate of the cell.
     * @param y    second coordinate of the cell.
     */
    private void addCellHandlers(Pane cell, int x, int y) {
        // Enable clicking on cells.
        cell.setOnMousePressed(mouseEvent -> {
            if (!readonly.get()) {
                fireEvent(new ShotEvent(x, y));
                cell.requestFocus();
            }
        });

        // Enable traversing with keys
        cell.focusTraversableProperty().bind(readonly.not());
        cell.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue)
                cell.setBorder(RED_BORDER);
            else
                cell.setBorder(TRANSPARENT_BORDER);
        });

        // Enable keyboard support
        cell.setOnKeyPressed(keyEvent -> {
            if (!readonly.get()) {
                var code = keyEvent.getCode();

                if (code.isDigitKey()) {
                    var digit = Integer.parseInt(code.getChar());
                    if (lastEnteredDigit != -1) {
                        System.out.printf("Entered Y coordinate: %d\n", digit);
                        fireEvent(new ShotEvent(digit, lastEnteredDigit));
                        cells[digit][lastEnteredDigit].requestFocus();
                        leftLabels[lastEnteredDigit].setTextFill(Color.BLACK);
                        lastEnteredDigit = -1;
                    } else {
                        System.out.printf("Entered X coordinate: %d\n", digit);
                        leftLabels[digit].setTextFill(Color.RED);
                        lastEnteredDigit = digit;
                    }
                } else {
                    if (lastEnteredDigit != -1)
                        leftLabels[lastEnteredDigit].setTextFill(Color.BLACK);
                    lastEnteredDigit = -1;
                }

                if (code.equals(KeyCode.SPACE) || code.equals(KeyCode.ENTER))
                    fireEvent(new ShotEvent(x, y));
            }
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

    public void paintCell(int x, int y, Background background) {
        cells[x][y].setBackground(background);
    }

    public void initField() {
        for (int i = 0; i < width; ++i)
            for (int j = 0; j < height; ++j)
                paintCell(i, j, UNKNOWN_CELL_BACKGROUND);
    }
}
