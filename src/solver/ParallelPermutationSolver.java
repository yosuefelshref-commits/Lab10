package solver;

import model.Cell;
import verifier.SudokuVerifier;
import verifier.VerificationResult;
import controller.exceptions.InvalidGame;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParallelPermutationSolver {

    private final SudokuVerifier verifier = new SudokuVerifier();
    private final AtomicBoolean solved = new AtomicBoolean(false);
    private volatile int[] solution;

    public int[] solve(int[][] board) throws InvalidGame {

        if (verifier.verify(board) == VerificationResult.INVALID)
            throw new InvalidGame("Board is invalid");

        EmptyCellIterator cellIterator =
                new EmptyCellIterator(board);

        List<Cell> emptyCells = cellIterator.getCells();

        if (emptyCells.size() != 5)
            throw new InvalidGame("Requires exactly 5 empty cells");

        PermutationIterator permIterator =
                new PermutationIterator(5);

        ExecutorService pool =
                Executors.newFixedThreadPool(
                        Runtime.getRuntime().availableProcessors());

        while (permIterator.hasNext() && !solved.get()) {
            int[] perm = permIterator.next();

            pool.submit(() -> {
                if (solved.get()) return;

                if (applyAndCheck(board, emptyCells, perm)) {
                    solution = buildSolution(emptyCells, perm);
                    solved.set(true);
                }
            });
        }

        pool.shutdown();

        try {
            pool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (solution == null)
            throw new InvalidGame("No solution found");

        return solution;
    }

    private boolean applyAndCheck(int[][] board,
                                  List<Cell> cells,
                                  int[] values) {

        int[][] test = copyBoard(board);

        for (int i = 0; i < 5; i++) {
            Cell c = cells.get(i);
            Digit d = Digit.Factory.get(values[i]);
            test[c.getRow()][c.getCol()] = d.getValue();
        }

        return verifier.verify(test) == VerificationResult.VALID;
    }

    private int[][] copyBoard(int[][] src) {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++)
            System.arraycopy(src[i], 0, copy[i], 0, 9);
        return copy;
    }

    private int[] buildSolution(List<Cell> cells, int[] values) {
        int[] sol = new int[15];
        for (int i = 0; i < 5; i++) {
            Cell c = cells.get(i);
            sol[i * 3] = c.getRow();
            sol[i * 3 + 1] = c.getCol();
            sol[i * 3 + 2] = values[i];
        }
        return sol;
    }
}
