package battleship.models;

import java.io.Serializable;

/**
 * Сообщение, отправляемое игроком А игроку Б, когда А хочет выстрелить по полю Б
 */
public class ShotRequest implements Serializable {
    private static final long serialVersionUID = 6570813119888438577L;
    private final int x;
    private final int y;

    public ShotRequest(int x, int y) {
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
