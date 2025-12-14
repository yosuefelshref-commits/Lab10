package verifier;

import java.util.HashSet;
import java.util.Set;

public class InvalidCells {

    private final Set<String> cells = new HashSet<>();

    public void add(int row, int col) {
        cells.add(row + "," + col);
    }

    public boolean contains(int row, int col) {
        return cells.contains(row + "," + col);
    }

    public boolean isEmpty() {
        return cells.isEmpty();
    }

    public Set<String> getAll() {
        return cells;
    }
}
