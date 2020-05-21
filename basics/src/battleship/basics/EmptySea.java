package battleship.basics;

/**
 * A ship which is used to show absence of a ship.
 */
public class EmptySea extends Ship {
    /**
     * Class constructor.
     */
    public EmptySea() {
        setLength(1);
    }

    /**
     * Returns a type of this ship.
     * Always returns {@code "empty sea"}.
     *
     * @return type of this ship (it is always {@code "empty sea"}).
     */
    @Override
    public String getShipType() {
        return "empty sea";
    }

    /**
     * Returns if a shot at given row and column is hit this ship.
     * Always returns {@code false} because empty sea is not a ship and can't be hit.
     *
     * @param row a row of the ocean where player shoots.
     * @param column a column of the ocean where player shoots.
     * @return if this ship is hit (it is always {@code false})
     */
    @Override
    public boolean shootAt(int row, int column) {
        return false;
    }

    /**
     * Returns if this ship is sunk.
     * Always returns {@code false} because empty sea is not a ship and can't be sunk.
     *
     * @return if this ship is sunk (it is always {@code false}).
     */
    @Override
    public boolean isSunk() {
        return false;
    }


    @Override
    public String toString() {
        return "-";
    }
}
