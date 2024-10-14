package battleship;

public enum ShipType {
    AIRCRAFT_CARRIER(5, "Aircraft Carrier"),
    BATTLESHIP(4, "Battleship"),
    SUBMARINE(3, "Submarine"),
    CRUISER(3, "Cruiser"),
    DESTROYER(2, "Destroyer");

    private final int cells;
    private final String name;

    ShipType(int cells, String name) {
        this.cells = cells;
        this.name = name;
    }

    public int getCells() {
        return cells;
    }

    public String getName() {
        return name;
    }
}
