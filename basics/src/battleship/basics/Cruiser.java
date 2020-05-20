package battleship.basics;

/**
 * A ship of length 3.
 */
public class Cruiser extends Ship {
    /**
     * Class constructor.
     */
    public Cruiser() {
        setLength(3);
    }

    /**
     * Returns a type of this ship.
     * Always returns {@code "cruiser"}.
     *
     * @return type of this ship (it is always {@code "cruiser"}).
     */
    @Override
    public String getShipType() {
        return "cruiser";
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
