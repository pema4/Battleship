package battleship.basics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OceanTest {

    Ocean ocean;
    Ship[][] ships;

    @BeforeEach
    void setUp() {
        ocean = new Ocean();
        ocean.placeAllShipsRandomly();
        ships = ocean.getShipArray();
    }

    @Test
    void placeAllShipsRandomly() {
        // тут сложный код, просто проверяется, что соседние клетки или одного типа, или одна из них empty sea
        for (int i = 0; i < ships.length; ++i)
            for (int j = 0; j < ships[0].length; ++j)
                for (int x = -1; x < 2; ++x)
                    for (int y = -1; y < 2; ++y)
                        if (0 <= i + x && i + x < ships.length && 0 <= j + y && j + y < ships[0].length)
                            assertTrue(ships[i][j].getShipType().equals("empty sea") ||
                                    ships[i + x][j + y].getShipType().equals("empty sea") ||
                                    ships[i][j].getShipType().equals(ships[i + x][j + y].getShipType()));
    }

    @Test
    void isOccupied() {
        for (int i = 0; i < ships.length; ++i)
            for (int j = 0; j < ships[0].length; ++j)
                assertEquals(!ships[i][j].getShipType().equals("empty sea"), ocean.isOccupied(i, j));
    }

    @Test
    void shootAt() {
        for (int i = 0; i < ships.length; ++i)
            for (int j = 0; j < ships[0].length; ++j) {
                var ship = ships[i][j];
                var isEmtpySea = ship.getShipType().equals("empty sea");
                assertEquals(!isEmtpySea && !ship.isSunk(), ocean.shootAt(i, j));
                assertEquals(!isEmtpySea && !ship.isSunk(), ocean.shootAt(i, j));
            }

        for (int i = 0; i < ships.length; ++i)
            for (int j = 0; j < ships[0].length; ++j)
                assertFalse(ocean.shootAt(i, j));
    }

    @Test
    void getShipAt() {
        for (int i = 0; i < ships.length; ++i)
            for (int j = 0; j < ships[0].length; ++j)
                assertEquals(ships[i][j], ocean.getShipAt(i, j));
    }

    @Test
    void getShotsFired() {
        assertEquals(0, ocean.getShotsFired());
        for (int i = 0; i < ships.length; ++i)
            for (int j = 0; j < ships[0].length; ++j) {
                ocean.shootAt(i, j);
                ocean.shootAt(i, j);
            }
        assertEquals(ships.length * ships[0].length * 2, ocean.getShotsFired());
    }

    @Test
    void getHitCount() {
        assertEquals(0, ocean.getHitCount());
        for (int i = 0; i < ships.length; ++i)
            for (int j = 0; j < ships[0].length; ++j) {
                ocean.shootAt(i, j);
                ocean.shootAt(i, j);
            }
        assertEquals(2 * (4 * 1 + 3 * 2 + 2 * 3 + 1 * 4), ocean.getHitCount());
    }

    @Test
    void getShipsSunk() {
        assertEquals(0, ocean.getShipsSunk());
        for (int i = 0; i < ships.length; ++i)
            for (int j = 0; j < ships[0].length; ++j) {
                var wasSunk = ocean.getShipAt(i, j).isSunk();
                var prevShipsSunk = ocean.getShipsSunk();
                ocean.shootAt(i, j);
                if (ocean.getShipAt(i, j).isSunk() != wasSunk)
                    assertEquals(ocean.getShipsSunk(), prevShipsSunk + 1);
            }
    }

    @Test
    void isGameOver() {
        assertFalse(ocean.isGameOver());
        for (int i = 0; i < ships.length; ++i)
            for (int j = 0; j < ships[0].length; ++j) {
                ocean.shootAt(i, j);
                if (ocean.isGameOver()) {
                    assertEquals(10, ocean.getShipsSunk());
                    break;
                }
            }
    }

    @Test
    void isShoot() {
        for (int i = 0; i < ships.length; ++i)
            for (int j = 0; j < ships[0].length; ++j) {
                assertFalse(ocean.isShoot(i, j));
                ocean.shootAt(i, j);
                assertTrue(ocean.isShoot(i, j));
            }
    }

    @Test
    void getShipArray() {
        assertNotNull(ships);
        assertEquals(ships.length, Ocean.OCEAN_WIDTH);
        assertEquals(ships[0].length, Ocean.OCEAN_HEIGHT);
    }
}