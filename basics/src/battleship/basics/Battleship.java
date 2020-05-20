package battleship.basics;

/**
 * A ship of length 4.
 */
public class Battleship extends Ship {
    /**
     * Class constructor.
     */
    public Battleship() {
        setLength(4);
    }

    /**
     * Returns a type of this ship.
     * Always returns {@code "battleship"}.
     *
     * @return type of this ship (it is always {@code "battleship"}).
     */
    @Override
    public String getShipType() {
        return "battleship";
    }

    /**
     * Returns a string representation of this ship.
     * It is {@code "x"} if ship is sunk, otherwise {@code "S"}.
     *
     * @return string representation of this ship ({@code "x"} or {@code "S"}).
     */
    @Override
    public String toString() {
        if (isSunk())
            return "x";
        else
            return "S";
    }
}
