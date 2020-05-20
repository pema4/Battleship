package battleship.basics;

import java.util.Scanner;

/**
 * A main game class.
 */
public class BattleshipGame {
    private static Scanner consoleInput = new Scanner(System.in);

    /**
     * Entrance point for the program.
     *
     * @param args console line arguments.
     */
    public static void main(String[] args) {
        Ocean ocean = new Ocean();
        ocean.placeAllShipsRandomly();

        do {
            ocean.print();
            readCoordinatesAndShoot(ocean);
        } while (!ocean.isGameOver());

        printStatistics(ocean);
    }

    /**
     * Prints the statistics for a finished game.
     *
     * @param ocean an ocean where player plays.
     */
    private static void printStatistics(Ocean ocean) {
        System.out.println("You won!");
        System.out.println("Statistics:");
        System.out.printf("Hits: %d\n", ocean.getHitCount());
        System.out.printf("Shots: %d\n", ocean.getShotsFired());
    }

    /**
     * Reads user input, shoots, prints if player hit any ship or not.
     *
     * @param ocean an ocean where player plays.
     */
    private static void readCoordinatesAndShoot(Ocean ocean) {
        System.out.print("Enter the row: ");
        int row = readValue();

        System.out.print("Enter the column: ");
        int column = readValue();

        var isSuccessfulShot = ocean.shootAt(row, column);
        if (isSuccessfulShot) {
            var ship = ocean.getShipAt(row, column);
            if (ship.isSunk())
                System.out.printf("You sank a %s, good job!\n", ship.getShipType());
            else
                System.out.println("You hit a ship!");
        } else {
            System.out.println("You missed, try again!");
        }
    }

    /**
     * Reads a value in range 0..9 from the console.
     *
     * @return Read integer.
     */
    private static int readValue() {
        while (true)
            try {
                String line = consoleInput.nextLine();
                int value = Integer.parseInt(line);
                if (value < 0)
                    System.out.print("Input must be non-negative! Try again: ");
                else if (value >= 10)
                    System.out.print("Input must be less than 10! Try again: ");
                else
                    return value;
            } catch (NumberFormatException ex) {
                System.out.print("Input is not an integer! Try again: ");
            }
    }
}
