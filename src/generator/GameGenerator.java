package generator;

import model.Game;
import model.DifficultyEnum;
import verifier.SudokuVerifier;
import verifier.VerificationResult;
import controller.exceptions.SolutionInvalidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameGenerator {

    private final SudokuVerifier verifier = new SudokuVerifier();
    private final RandomPairs randomPairs = new RandomPairs();

    public Map<DifficultyEnum, Game> generateFromSolved(int[][] solvedBoard)
            throws SolutionInvalidException {

        VerificationResult result = verifier.verify(solvedBoard);

        if (result != VerificationResult.VALID) {
            throw new SolutionInvalidException(
                    "Solution is invalid or incomplete");
        }

        Map<DifficultyEnum, Game> games = new HashMap<>();

        games.put(DifficultyEnum.EASY,
                new Game(createGameBoard(solvedBoard, 10)));

        games.put(DifficultyEnum.MEDIUM,
                new Game(createGameBoard(solvedBoard, 20)));

        games.put(DifficultyEnum.HARD,
                new Game(createGameBoard(solvedBoard, 25)));

        return games;
    }

    private int[][] createGameBoard(int[][] solved, int emptyCells) {

        int[][] board = copyBoard(solved);

        List<int[]> pairs =
                randomPairs.generateDistinctPairs(emptyCells);

        for (int[] p : pairs) {
            board[p[0]][p[1]] = 0;
        }

        return board;
    }

    private int[][] copyBoard(int[][] source) {
        int[][] copy = new int[9][9];
        for (int r = 0; r < 9; r++) {
            System.arraycopy(source[r], 0, copy[r], 0, 9);
        }
        return copy;
    }
}
