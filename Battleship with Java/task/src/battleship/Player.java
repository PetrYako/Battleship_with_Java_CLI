package battleship;

import java.util.ArrayList;
import java.util.List;

class Player {
    private final String name;
    private final char[][] field;
    private final char[][] fogField;
    private final List<Ship> ships = new ArrayList<>();

    Player(String name, int size) {
        this.name = name;
        this.field = new char[size][size];
        this.fogField = new char[size][size];
    }

    public char[][] getField() {
        return field;
    }

    public char[][] getFogField() {
        return fogField;
    }

    public List<Ship> getShips() {
        return ships;
    }

    public String getName() {
        return name;
    }
}
