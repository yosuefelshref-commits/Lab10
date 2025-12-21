package solver;

import model.Cell;

import java.util.ArrayList;
import java.util.List;

public class EmptyCellIterator implements CellIterator {

    private final List<Cell> cells = new ArrayList<>();
    private int index = 0;

    public EmptyCellIterator(int[][] board) {
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (board[r][c] == 0)
                    cells.add(new Cell(r, c));
    }

    @Override
    public boolean hasNext() {
        return index < cells.size();
    }

    @Override
    public Cell next() {
        return cells.get(index++);
    }
}
