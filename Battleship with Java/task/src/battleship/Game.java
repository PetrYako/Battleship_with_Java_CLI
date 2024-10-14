package battleship;

import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {
    private final int size = 10;
    private final char fogSymbol = '~';
    private final char shipSymbol = 'O';
    private final char hitSymbol = 'X';
    private final char missSymbol = 'M';
    private final Pattern coordinatePattern = Pattern.compile("(?<row>\\w)(?<column>\\d+)");

    private final Player player1 = new Player("Player 1", size);
    private final Player player2 = new Player("Player 2", size);

    private Player currentPlayer = player1;
    private Player opponent = player2;

    private final Scanner scanner;

    public Game() {
        fillField(currentPlayer.getField());
        fillField(currentPlayer.getFogField());
        fillField(opponent.getField());
        fillField(opponent.getFogField());
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.printf("%s, place your ships on the game field%n%n", player1.getName());
        printField(player1.getField());
        System.out.println();
        placeShips(player1);

        passMove();

        System.out.println();
        System.out.printf("%s, place your ships on the game field%n%n", player2.getName());
        printField(player2.getField());
        System.out.println();
        placeShips(player2);

        passMove();

        System.out.println("The game starts!");
        while (!isGameOver()) {
            System.out.println();
            printField(currentPlayer.getFogField());
            System.out.println("---------------------");
            printField(currentPlayer.getField());
            System.out.println(currentPlayer.getName() + ", it's your turn:");
            makeShot();
            passMove();
        }
    }

    private void passMove() {
        System.out.println("Press Enter and pass the move to another player");
        scanner.nextLine();
        if (currentPlayer == player1) {
            currentPlayer = player2;
            opponent = player1;
        } else {
            currentPlayer = player1;
            opponent = player2;
        }

    }

    private void placeShips(Player player) {
        for (ShipType ship : ShipType.values()) {
            System.out.printf("Enter the coordinates of the %s (%d cells):%n", ship.getName(), ship.getCells());
            placeShip(player, ship);
            printField(player.getField());
            System.out.println();
        }
    }

    private boolean isGameOver() {
        return currentPlayer.getShips().isEmpty();
    }

    private void makeShot() {
        System.out.println();
        String[] coordinate = scanner.nextLine().split(" ");
        Matcher matcher = coordinatePattern.matcher(coordinate[0]);
        if (!matcher.matches() || coordinate.length > 1) {
            System.out.println("Error: invalid input");
            makeShot();
            return;
        }
        char letter = matcher.group("row").charAt(0);
        int rowIndex = letter - 'A';
        int colIndex = Integer.parseInt(matcher.group("column")) - 1;

        if (!isValidBoundaries(currentPlayer, rowIndex, colIndex)) {
            System.out.println("Error: out of bounds");
            makeShot();
            return;
        }

        boolean isHit = opponent.getField()[rowIndex][colIndex] == shipSymbol ||
                opponent.getField()[rowIndex][colIndex] == hitSymbol;
        if (isHit) {
            currentPlayer.getFogField()[rowIndex][colIndex] = hitSymbol;
            opponent.getField()[rowIndex][colIndex] = hitSymbol;
            opponent.getFogField()[rowIndex][colIndex] = hitSymbol;
            printField(currentPlayer.getFogField());
            System.out.println();

            boolean isSunk = false;
            for (Ship ship : opponent.getShips()) {
                if (ship.isSunk(opponent.getField(), hitSymbol)) {
                    opponent.getShips().remove(ship);
                    isSunk = true;
                    if (opponent.getShips().isEmpty()) {
                        System.out.println("You sank the last ship. You won. Congratulations!");
                        return;
                    } else {
                        System.out.println("You sank a ship!");
                        System.out.println();
                    }
                    break;
                }
            }
            if (!isSunk) {
                System.out.println("You hit a ship!");
                System.out.println();
            }
        } else {
            currentPlayer.getFogField()[rowIndex][colIndex] = missSymbol;
            printField(currentPlayer.getFogField());
            System.out.println();
            System.out.println("You missed!");
            System.out.println();
        }
    }

    private void placeShip(Player player, ShipType shipType) {
        System.out.println();
        String[] coordinates = scanner.nextLine().split(" ");

        Matcher startMatcher = coordinatePattern.matcher(coordinates[0]);
        Matcher endMatcher = coordinatePattern.matcher(coordinates[1]);

        if (!startMatcher.matches() || !endMatcher.matches()) {
            System.out.println("Error: invalid input");
            placeShip(player, shipType);
            return;
        }

        char startLetter = startMatcher.group("row").charAt(0);
        char endLetter = endMatcher.group("row").charAt(0);
        int startRowIndex = startLetter - 'A';
        int endRowIndex = endLetter - 'A';
        int startColIndex = Integer.parseInt(startMatcher.group("column")) - 1;
        int endColIndex = Integer.parseInt(endMatcher.group("column")) - 1;

        if (!isCoordinateValid(player, startRowIndex, startColIndex, endRowIndex, endColIndex, shipType)) {
            placeShip(player, shipType);
            return;
        }

        Ship ship = new Ship(shipType);

        if (startRowIndex == endRowIndex) {
            placeHorizontally(player, startColIndex, endColIndex, startRowIndex, ship);
        } else {
            placeVertically(player, startRowIndex, endRowIndex, startColIndex, ship);
        }
        player.getShips().add(ship);
        System.out.println();
    }

    private void placeVertically(Player player, int startRowIndex, int endRowIndex, int startColIndex, Ship ship) {
        if (startRowIndex <= endRowIndex) {
            for (int i = startRowIndex; i <= endRowIndex; i++) {
                player.getField()[i][startColIndex] = shipSymbol;
                ship.addCoordinate(i, startColIndex);
            }
        } else {
            for (int i = startRowIndex; i >= endRowIndex; i--) {
                player.getField()[i][startColIndex] = shipSymbol;
                ship.addCoordinate(i, startColIndex);
            }
        }
    }

    private void placeHorizontally(Player player, int startColIndex, int endColIndex, int startRowIndex, Ship ship) {
        if (startColIndex <= endColIndex) {
            for (int i = startColIndex; i <= endColIndex; i++) {
                player.getField()[startRowIndex][i] = shipSymbol;
                ship.addCoordinate(startRowIndex, i);
            }
        } else {
            for (int i = startColIndex; i >= endColIndex; i--) {
                player.getField()[startRowIndex][i] = shipSymbol;
                ship.addCoordinate(startRowIndex, i);
            }
        }
    }

    private boolean isCoordinateValid(Player player, int startRowIndex, int startColIndex, int endRowIndex, int endColIndex, ShipType ship) {
        if (!isValidBoundaries(player, startRowIndex, startColIndex) || !isValidBoundaries(player, endRowIndex, endColIndex)) {
            System.out.println("Error: out of bounds");
            return false;
        }
        if (startRowIndex != endRowIndex) {
            if (startColIndex != endColIndex) {
                System.out.println("Error: not on the same line");
                return false;
            }
            if (Math.abs(startRowIndex - endRowIndex) != ship.getCells() - 1) {
                System.out.println("Error: wrong size");
                return false;
            }
        } else {
            if (Math.abs(startColIndex - endColIndex) != ship.getCells() - 1) {
                System.out.println("Error: wrong size");
                return false;
            }
        }
        return isValidAdjacent(player, startRowIndex, startColIndex, endRowIndex, endColIndex, ship);
    }

    private boolean isValidBoundaries(Player player, int rowIndex, int colIndex) {
        return rowIndex >= 0 && rowIndex < player.getField().length && colIndex >= 0 && colIndex < player.getField()[0].length;
    }

    private boolean isValidAdjacent(Player player, int startRowIndex, int startColIndex, int endRowIndex, int endColIndex, ShipType ship) {
        int rowStart = Math.min(startRowIndex, endRowIndex);
        int rowEnd = Math.max(startRowIndex, endRowIndex);
        int colStart = Math.min(startColIndex, endColIndex);
        int colEnd = Math.max(startColIndex, endColIndex);

        for (int i = rowStart - 1; i <= rowEnd + 1; i++) {
            for (int j = colStart - 1; j <= colEnd + 1; j++) {
                if (i < 0 || i >= player.getField().length || j < 0 || j >= player.getField()[0].length) {
                    continue;
                }

                if (i >= rowStart && i <= rowEnd && j >= colStart && j <= colEnd) {
                    continue;
                }

                if (player.getField()[i][j] == shipSymbol) {
                    System.out.println("Error: adjacent ship");
                    return false;
                }
            }
        }
        return true;
    }

    private void printField(char[][] field) {
        System.out.print("  ");
        for (int i = 1; i <= field.length; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        for (int i = 0; i < field.length; i++) {
            char letter = (char) ('A' + i);
            System.out.print(letter + " ");
            for (char c : field[i]) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    private void fillField(char[][] field) {
        for (char[] chars : field) {
            Arrays.fill(chars, fogSymbol);
        }
    }
}
