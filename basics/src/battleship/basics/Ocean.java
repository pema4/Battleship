package battleship.basics;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * An ocean containing different ships.
 */
public class Ocean {
    /**
     * The width of any ocean (a number of columns).
     */
    public static final int OCEAN_WIDTH = 10;

    /**
     * The height of any ocean (a number of rows).
     */
    public static final int OCEAN_HEIGHT = 10;

    private static Random rnd = new Random();
    private boolean[][] shots = new boolean[OCEAN_HEIGHT][OCEAN_WIDTH];
    private Ship[][] ships = new Ship[OCEAN_HEIGHT][OCEAN_WIDTH];
    private Set<Ship> hitShips = new HashSet<>();
    private int shotsFired = 0;
    private int hitCount = 0;
    private int shipsSunk = 0;

    /**
     * Class constructor, which fills the ocean with empty seas.
     */
    public Ocean() {
        for (var row : ships)
            for (int i = 0; i < row.length; ++i)
                row[i] = new EmptySea();
    }

    /**
     * Generates 1 battleship, 2 cruisers, 3 destroyers, and 4 submarines and places them randomly.
     */
    public void placeAllShipsRandomly() {
        Ship[] ships = new Ship[]{
                new Battleship(),
                new Cruiser(),
                new Cruiser(),
                new Destroyer(),
                new Destroyer(),
                new Destroyer(),
                new Submarine(),
                new Submarine(),
                new Submarine(),
                new Submarine(),
        };
        placeShips(ships);
    }

    /**
     * Places given ships randomly.
     *
     * @param ships ships to be placed.
     */
    private void placeShips(Ship[] ships) {
        for (var ship : ships) {
            int row;
            int column;
            boolean orientation;
            do {
                row = rnd.nextInt() % 10;
                column = rnd.nextInt() % 10;
                orientation = rnd.nextBoolean();
            } while (!ship.okToPlaceShipAt(row, column, orientation, this));
            ship.placeShipAt(row, column, orientation, this);
        }
    }

    /**
     * Returns if this ocean contains non-empty ship at specified position.
     *
     * @param row    a row in this ocean.
     * @param column a column in this ocean.
     * @return if this ocean contains non-empty ship.
     */
    public boolean isOccupied(int row, int column) {
        return !(ships[row][column] instanceof EmptySea);
    }

    /**
     * Shoots at specified position in this ocean.
     * Returns if any ship is hit.
     *
     * @param row    a row in this ocean.
     * @param column a column in this ocean.
     * @return if any ship is hit.
     */
    public boolean shootAt(int row, int column) {
        shots[row][column] = true;
        shotsFired += 1;
        if (!ships[row][column].getShipType().equals("empty sea"))
            hitCount += 1;

        var wasSunk = ships[row][column].isSunk();
        if (!ships[row][column].shootAt(row, column))
            return false;

        hitShips.add(ships[row][column]);
        var isSunk = ships[row][column].isSunk();

        if (!wasSunk && isSunk)
            ++shipsSunk;

        return true;
    }

    /**
     * Returns a ship (or empty sea) at specified position.
     *
     * @param row    a row in this ocean.
     * @param column a column in this ocean.
     * @return a ship (or empty sea) at specified position.
     */
    public Ship getShipAt(int row, int column) {
        return ships[row][column];
    }

    /**
     * Returns an amount of shots fired in this ocean.
     *
     * @return an amount of shots fired in this ocean.
     */
    public int getShotsFired() {
        return shotsFired;
    }

    /**
     * Returns an amount of hits in this ocean.
     *
     * @return an amount of hits in this ocean.
     */
    public int getHitCount() {
        return hitCount;
    }

    /**
     * Returns an amount of sunk ships in this ocean.
     *
     * @return an amount of sunk ships in this ocean.
     */
    public int getShipsSunk() {
        return shipsSunk;
    }

    /**
     * Returns an amount of hit ships in this ocean.
     *
     * @return an amount of hit ships in this ocean.
     */
    public int getShipsHit() {
        return hitShips.size();
    }

    /**
     * Returns if the game is over (all ships are sunk).
     *
     * @return if the game is over (all ships are sunk).
     */
    public boolean isGameOver() {
        return getShipsSunk() == 10;
    }

    /**
     * Returns if player have already shoot specified cell.
     *
     * @param row    a row in this ocean.
     * @param column a column in this ocean.
     * @return if specified cell was hit
     */
    public boolean isShoot(int row, int column) {
        return shots[row][column];
    }

    /**
     * Returns an internal ship array.
     *
     * @return an internal ship array.
     */
    public Ship[][] getShipArray() {
        return ships;
    }

    /**
     * Prints information about
     */
    public void print() {
        System.out.println("  0 1 2 3 4 5 6 7 8 9");
        for (int i = 0; i < 10; ++i) {
            System.out.printf("%d ", i);
            for (int j = 0; j < 10; ++j)
                if (!shots[i][j])
                    System.out.print(". ");
                else
                    System.out.printf("%s ", ships[i][j]);
            System.out.println();
        }
    }
}
