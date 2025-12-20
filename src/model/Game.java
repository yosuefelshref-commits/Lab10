package model;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final int[][] board;

    public Game(int[][] board) {
        this.board = board;
    }

    public int[][] getBoard() {
        return board;
    }

    public List<Cell> getEmptyCells() {
        List<Cell> empty = new ArrayList<>();
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (board[r][c] == 0)
                    empty.add(new Cell(r, c));
        return empty;
    }

    public int countEmptyCells() {
        return getEmptyCells().size();
    }
}
