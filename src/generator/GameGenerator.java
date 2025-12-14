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

    public Map<DifficultyEnum, Game> generateFromSolved(int[][] solvedBoard)
            throws SolutionInvalidException {

        VerificationResult result = verifier.verify(solvedBoard);

        if (result != VerificationResult.VALID) {
            throw new SolutionInvalidException("Solution is invalid or incomplete");
        }

        Map<DifficultyEnum, Game> games = new HashMap<>();

        games.put(DifficultyEnum.EASY,
                new Game(removeCells(solvedBoard, 10)));

        games.put(DifficultyEnum.MEDIUM,
                new Game(removeCells(solvedBoard, 20)));

        games.put(DifficultyEnum.HARD,
                new Game(removeCells(solvedBoard, 25)));

        return games;
    }

    private int[][] removeCells(int[][] source, int count) {

        int[][] board = source.clone(); // shallow copy (rows shared)

        RandomPairs randomPairs = new RandomPairs();
        List<int[]> pairs = randomPairs.generateDistinctPairs(count);

        for (int[] pair : pairs) {
            int row = pair[0];
            int col = pair[1];
            board[row][col] = 0;
        }

        return board;
    }
}
