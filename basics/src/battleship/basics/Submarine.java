package battleship.basics;

/**
 * A ship of length 1.
 */
public class Submarine extends Ship {
    /**
     * Class constructor.
     */
    public Submarine() {
        setLength(1);
    }

    /**
     * Returns a type of this ship.
     * Always returns {@code "submarine"}.
     *
     * @return type of this ship (it is always {@code "submarine"}).
     */
    @Override
    public String getShipType() {
        return "submarine";
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
