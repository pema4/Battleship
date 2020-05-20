package battleship.basics;

/**
 * An abstract ship.
 */
public abstract class Ship {
    private boolean horizontal;
    private boolean[] hit = new boolean[4];
    private int bowRow;
    private int bowColumn;
    private int length;

    /**
     * Returns a length of this ship.
     *
     * @return a length of this ship
     */
    public int getLength() {
        return length;
    }

    /**
     * Sets a length of this ship.
     *
     * @param length a length to set.
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Returns a row of the bow of this ship.
     *
     * @return a row of the bow of this ship.
     */
    public int getBowRow() {
        return bowRow;
    }

    /**
     * Set a row of the bow of this ship.
     *
     * @param bowRow a row to set.
     */
    public void setBowRow(int bowRow) {
        this.bowRow = bowRow;
    }

    /**
     * Returns a column of the bow of this ship.
     *
     * @return a column of the bow of this ship.
     */
    public int getBowColumn() {
        return bowColumn;
    }

    /**
     * Sets a column of the bow of this ship.
     *
     * @param bowColumn a column to set.
     */
    public void setBowColumn(int bowColumn) {
        this.bowColumn = bowColumn;
    }

    /**
     * Returns if this ship is placed horizontally.
     *
     * @return if this ship is placed horizontally.
     */
    public boolean isHorizontal() {
        return horizontal;
    }

    /**
     * Sets if this ship is placed horizontally.
     *
     * @param horizontal if new placement of this ship is horizontal.
     */
    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * Returns a type of this ship.
     *
     * @return a type of this ship.
     */
    public abstract String getShipType();

    /**
     * Returns if this ship doesn't overlap with others when
     * placed at given row, column with specified orientation.
     *
     * @param row        a row of the bow of this ship.
     * @param column     a column of the bow of this ship.
     * @param horizontal if ship is placed horizontally.
     * @param ocean      an Ocean object where this ship is going to be placed.
     * @return if it's okay to place ship at given position.
     */
    public boolean okToPlaceShipAt(int row, int column, boolean horizontal, Ocean ocean) {
        int width = horizontal ? getLength() : 1;
        int height = !horizontal ? getLength() : 1;

        // Check if this ship is in the ocean.
        if (row < 0 || row + height > Ocean.OCEAN_HEIGHT || column < 0 || column + width > Ocean.OCEAN_WIDTH)
            return false;

        // Check if the ocean doesn't have other ships around.
        for (int i = row - 1; i < row + 1 + height; ++i)
            for (int j = column - 1; j < column + 1 + width; ++j)
                if (i >= 0 && i < Ocean.OCEAN_HEIGHT && j >= 0 && j < Ocean.OCEAN_WIDTH && ocean.isOccupied(i, j))
                    return false;

        // Otherwise, return true.
        return true;
    }

    /**
     * Places this ship at given row, column with specified orientation.
     *
     * @param row        a row where the ship will be placed.
     * @param column     a row where the ship will be placed.
     * @param horizontal shows the direction of the ship.
     * @param ocean      represents an ocean.
     */
    public void placeShipAt(int row, int column, boolean horizontal, Ocean ocean) {
        setBowRow(row);
        setBowColumn(column);
        setHorizontal(horizontal);

        if (horizontal)
            for (int idx = 0; idx < getLength(); ++idx)
                ocean.getShipArray()[row][column + idx] = this;
        else
            for (int idx = 0; idx < getLength(); ++idx)
                ocean.getShipArray()[row + idx][column] = this;
    }

    /**
     * Returns if a shot at given row and column hit this ship.
     *
     * @param row    a row of the ocean where player shoots.
     * @param column a column of the ocean where player shoots.
     * @return if this ship is hit.
     */
    public boolean shootAt(int row, int column) {
        if (isSunk())
            return false;

        if (isHorizontal())
            return shotHorizontalShipAt(row, column);
        else
            return shotVerticalShipAt(row, column);
    }

    /**
     * Returns if a shot at given row and column is hit this ship assuming this ship is oriented horizontally.
     *
     * @param row    a row of the ocean where player shoots.
     * @param column a column of the ocean where player shoots.
     * @return if this ship is hit.
     */
    private boolean shotHorizontalShipAt(int row, int column) {
        if (getBowRow() != row)
            return false;

        int hitShipPart = column - getBowColumn();
        if (hitShipPart < 0 || hitShipPart >= getLength())
            return false;

        hit[hitShipPart] = true;
        return true;
    }

    /**
     * Returns if a shot at given row and column is hit this ship assuming this ship is oriented vertically.
     *
     * @param row    a row of the ocean where player shoots.
     * @param column a column of the ocean where player shoots.
     * @return if this ship is hit.
     */
    private boolean shotVerticalShipAt(int row, int column) {
        if (getBowColumn() != column)
            return false;

        int hitShipPart = row - getBowRow();
        if (hitShipPart < 0 || hitShipPart >= getLength())
            return false;

        hit[hitShipPart] = true;
        return true;
    }

    /**
     * Returns if this ship is sunk.
     *
     * @return if this ship is sunk.
     */
    public boolean isSunk() {
        for (int i = 0; i < getLength(); ++i)
            if (!hit[i])
                return false;
        return true;
    }
}
