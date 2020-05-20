package battleship.basics;

/**
 * A ship of length 2.
 */
public class Destroyer extends Ship {
    /**
     * CLass constructor.
     */
    public Destroyer() {
        setLength(2);
    }

    /**
     * Returns a type of this ship.
     * Always returns {@code "destroyer"}.
     *
     * @return type of this ship (it is always {@code "destroyer}).
     */
    @Override
    public String getShipType() {
        return "destroyer";
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
