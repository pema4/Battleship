package battleship.basics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipTest {

    @Test
    void okToPlaceShipAt() {
        var ocean = new Ocean();
        Ship battleship = new Battleship();

        for (int row = 0; row < Ocean.OCEAN_HEIGHT; ++row)
            for (int column = 0; column < Ocean.OCEAN_WIDTH; ++column) {
                var canPlaceHorizontally = battleship.okToPlaceShipAt(row, column, true, ocean);
                var canPlaceVertically = battleship.okToPlaceShipAt(row, column, false, ocean);

                if (column <= Ocean.OCEAN_WIDTH - battleship.getLength())
                    assertTrue(canPlaceHorizontally);
                else
                    assertFalse(canPlaceHorizontally);

                if (row <= Ocean.OCEAN_HEIGHT - battleship.getLength())
                    assertTrue(canPlaceVertically);
                else
                    assertFalse(canPlaceVertically);
            }

        battleship.placeShipAt(2, 2, true, ocean);
        Ship cruiser = new Cruiser();

        assertTrue(cruiser.okToPlaceShipAt(0, 0, true, ocean));
        assertTrue(cruiser.okToPlaceShipAt(0, 0, false, ocean));
        assertFalse(cruiser.okToPlaceShipAt(1, 0, true, ocean));
        assertFalse(cruiser.okToPlaceShipAt(0, 1, false, ocean));
        assertFalse(cruiser.okToPlaceShipAt(2, 2, true, ocean));
        assertFalse(cruiser.okToPlaceShipAt(2, 6, false, ocean));
        assertFalse(cruiser.okToPlaceShipAt(2, 6, true, ocean));
    }

    @Test
    void placeShipAt() {
        var ocean = new Ocean();
        var battleship = new Battleship();
        battleship.placeShipAt(2, 2, true, ocean);
//        for (int row = 2; row < 2 + battleship.getLength(); ++row)
//            assertSame(battleship, ocean.getShipAt(row, 2));

        for (int i = 0; i < Ocean.OCEAN_HEIGHT; ++i)
            for (int j = 0; j < Ocean.OCEAN_WIDTH; ++j)
                if (ocean.getShipAt(i, j) == battleship)
                    assertTrue(i == 2 && j >= 2 && j < 6);
    }

    @Test
    void shootAt() {
        var ocean = new Ocean();
        var destroyer = new Destroyer();
        destroyer.placeShipAt(2, 2, true, ocean);

        // player misses
        assertFalse(destroyer.shootAt(1, 2));
        assertFalse(destroyer.shootAt(2, 1));

        // player hits but ship is not sunk
        assertTrue(destroyer.shootAt(2, 2));
        assertTrue(destroyer.shootAt(2, 2));
        assertTrue(destroyer.shootAt(2, 3));
        assertTrue(destroyer.isSunk());

        // ship is sunk
        assertFalse(destroyer.shootAt(2, 2));
        assertFalse(destroyer.shootAt(2, 3));
    }

    @Test
    void isSunk() {
        var ocean = new Ocean();
        var destroyer = new Destroyer();
        destroyer.placeShipAt(2, 2, true, ocean);

        // player misses
        destroyer.shootAt(1, 2);
        assertFalse(destroyer.isSunk());

        // player hits but ship is not sunk
        destroyer.shootAt(2, 2);
        assertFalse(destroyer.isSunk());

        // player hits last cell and ship finally becomes sunk.
        destroyer.shootAt(2, 3);
        assertTrue(destroyer.isSunk());
    }
}