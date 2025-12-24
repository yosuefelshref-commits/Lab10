package solver;

import model.Cell;
import verifier.SudokuVerifier;
import verifier.VerificationResult;
import controller.exceptions.InvalidGame;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class PermutationSolver {

    private final SudokuVerifier verifier = new SudokuVerifier();

    public int[] solve(int[][] board) throws InvalidGame {

        if (verifier.verify(board) == VerificationResult.INVALID) {
            throw new InvalidGame("Board is already invalid");
        }

        List<Cell> emptyCells = findEmptyCells(board);

        if (emptyCells.size() != 5) {
            throw new InvalidGame(
                    "Solver requires exactly 5 empty cells, found: " + emptyCells.size());
        }

        PermutationIterator iterator = new PermutationIterator(5);

        while (iterator.hasNext()) {
            int[] perm = iterator.next();

            if (applyAndCheck(board, emptyCells, perm)) {
                return buildSolution(emptyCells, perm);
            }
        }

        throw new InvalidGame("No solution found");
    }

    private boolean applyAndCheck(int[][] board,
                                  List<Cell> cells,
                                  int[] values) {

        for (int i = 0; i < 5; i++) {
            Cell c = cells.get(i);
            Digit d = Digit.Factory.get(values[i]);
            board[c.getRow()][c.getCol()] = d.getValue();
        }

        boolean valid =
                verifier.verify(board) == VerificationResult.VALID;

        for (Cell c : cells) {
            board[c.getRow()][c.getCol()] = 0;
        }

        return valid;
    }

    private int[] buildSolution(List<Cell> cells, int[] values) {
        int[] solution = new int[15];

        for (int i = 0; i < 5; i++) {
            Cell c = cells.get(i);
            solution[i * 3] = c.getRow();
            solution[i * 3 + 1] = c.getCol();
            solution[i * 3 + 2] = values[i];
        }

        return solution;
    }

    private List<Cell> findEmptyCells(int[][] board) {
        List<Cell> empty = new ArrayList<>();
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (board[r][c] == 0)
                    empty.add(new Cell(r, c));
        return empty;
    }
}


class PermutationIterator implements Iterator<int[]> {

    private final int size;
    private final int[] current;
    private boolean hasNext;

    public PermutationIterator(int size) {
        this.size = size;
        this.current = new int[size];

        for (int i = 0; i < size; i++) {
            current[i] = 1;
        }

        this.hasNext = true;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public int[] next() {
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        int[] result = current.clone();
        increment();

        return result;
    }

    private void increment() {
        int pos = size - 1;

        while (pos >= 0 && current[pos] == 9) {
            current[pos] = 1;
            pos--;
        }

        if (pos < 0) {
            hasNext = false;
        } else {
            current[pos]++;
        }
    }
}