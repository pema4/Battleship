package battleship.controls;

import javafx.event.Event;
import javafx.event.EventType;

public class ShotEvent extends Event {
    public static final EventType<ShotEvent> SHOT = new EventType<>(Event.ANY, "SHOT");
    private final int x;
    private final int y;

    public ShotEvent(int x, int y) {
        super(SHOT);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
