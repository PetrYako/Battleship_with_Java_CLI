package battleship;

import java.util.ArrayList;
import java.util.List;

class Ship {
    private final ShipType type;
    private final List<int[]> coordinates;

    public Ship(ShipType type) {
        this.type = type;
        this.coordinates = new ArrayList<>();
    }

    public ShipType getType() {
        return type;
    }

    public List<int[]> getCoordinates() {
        return coordinates;
    }

    public void addCoordinate(int row, int col) {
        coordinates.add(new int[]{row, col});
    }

    public boolean isSunk(char[][] field, char hitSymbol) {
        for (int[] coordinate : coordinates) {
            int row = coordinate[0];
            int col = coordinate[1];
            if (field[row][col] != hitSymbol) {
                return false;
            }
        }
        return true;
    }
}
