package solver;

import model.Cell;
import verifier.SudokuVerifier;
import verifier.VerificationResult;
import controller.exceptions.InvalidGame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Permutation-based Sudoku solver for exactly 5 empty cells.
 * Uses Iterator pattern to generate permutations on-the-fly.
 * Uses Flyweight pattern to avoid memory overhead.
 */
public class PermutationSolver {

    private final SudokuVerifier verifier = new SudokuVerifier();

    /**
     * Solves a Sudoku board with exactly 5 empty cells.
     * @return int[] containing: [x1, y1, val1, x2, y2, val2, ...]
     */
    public int[] solve(int[][] board) throws InvalidGame {

        // 1. Validate board state
        if (verifier.verify(board) == VerificationResult.INVALID) {
            throw new InvalidGame("Board is already invalid");
        }

        // 2. Find empty cells
        List<Cell> emptyCells = findEmptyCells(board);

        if (emptyCells.size() != 5) {
            throw new InvalidGame("Solver requires exactly 5 empty cells, found: " + emptyCells.size());
        }

        // 3. Create permutation iterator (9^5 combinations)
        PermutationIterator permIterator = new PermutationIterator(5);

        // 4. Try each permutation
        while (permIterator.hasNext()) {
            int[] perm = permIterator.next();

            // Use Flyweight pattern - don't modify original board
            if (isValidPermutation(board, emptyCells, perm)) {
                return buildSolution(emptyCells, perm);
            }
        }

        throw new InvalidGame("No solution found");
    }

    /**
     * Check if permutation makes the board valid WITHOUT modifying original.
     * This is the Flyweight pattern application - we create temporary view.
     */
    private boolean isValidPermutation(int[][] board,
                                       List<Cell> cells,
                                       int[] values) {

        // Create a COPY for verification (Flyweight: reuse Digit objects)
        int[][] testBoard = copyBoard(board);

        // Apply permutation to copy
        for (int i = 0; i < 5; i++) {
            Cell c = cells.get(i);
            testBoard[c.getRow()][c.getCol()] = values[i];
        }

        // Verify the copy
        VerificationResult result = verifier.verify(testBoard);

        return result == VerificationResult.VALID;
    }

    /**
     * Create a shallow copy of board for testing
     */
    private int[][] copyBoard(int[][] source) {
        int[][] copy = new int[9][9];
        for (int r = 0; r < 9; r++) {
            System.arraycopy(source[r], 0, copy[r], 0, 9);
        }
        return copy;
    }

    private int[] buildSolution(List<Cell> cells, int[] values) {
        int[] solution = new int[15]; // 5 cells × 3 (x, y, value)

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
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] == 0) {
                    empty.add(new Cell(r, c));
                }
            }
        }
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

        // Initialize first permutation: [1, 1, 1, 1, 1]
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

        // Return copy of current permutation
        int[] result = current.clone();

        // Generate next permutation (like odometer)
        increment();

        return result;
    }

    private void increment() {
        int pos = size - 1;

        // Find position to increment
        while (pos >= 0 && current[pos] == 9) {
            current[pos] = 1; // Reset to 1
            pos--;
        }

        if (pos < 0) {
            hasNext = false; // Reached [9,9,9,9,9]
        } else {
            current[pos]++;
        }
    }
}