package controller;

import model.*;
import generator.GameGenerator;
import storage.StorageManager;
import storage.UndoManager;
import verifier.*;
import controller.exceptions.*;
import view.Controllable;
import view.UserAction;
import solver.ParallelPermutationSolver;

import java.io.IOException;
import java.util.Map;

public class ControllerFacade implements Viewable, Controllable {

    private final GameGenerator generator = new GameGenerator();
    private final SudokuVerifier verifier = new SudokuVerifier();
    private final ParallelPermutationSolver solver = new ParallelPermutationSolver();
    private Game currentGame;
    private DifficultyEnum currentDifficulty;

    // ================== Catalog ==================

    @Override
    public Catalog getCatalog() {
        boolean hasCurrent = StorageManager.hasCurrentGame();
        boolean hasAll = StorageManager.hasAllDifficultyGames();
        return new Catalog(hasCurrent, hasAll);
    }

    @Override
    public boolean[] getCatalogStatus() {
        Catalog c = getCatalog();
        return new boolean[]{
                c.current(),
                c.allModesExist()
        };
    }

    // ================== Game Generation ==================

    @Override
    public void driveGames(String sourcePath)
            throws SolutionInvalidException {

        int[][] solvedBoard =
                StorageManager.loadSolvedBoard(sourcePath);

        driveGames(new Game(solvedBoard));
    }

    @Override
    public void driveGames(Game sourceGame)
            throws SolutionInvalidException {

        VerificationResult result =
                verifier.verify(sourceGame.getBoard());

        if (result != VerificationResult.VALID)
            throw new SolutionInvalidException(
                    "Provided solution is not valid");

        Map<DifficultyEnum, Game> games =
                generator.generateFromSolved(
                        sourceGame.getBoard());

        StorageManager.saveGeneratedGames(games);
    }

    // ================== Loading Games ==================

    @Override
    public Game getGame(DifficultyEnum level)
            throws NotFoundException {

        currentGame = StorageManager.loadGame(level);
        currentDifficulty = level;
        UndoManager.clearLog(); // Start fresh log
        return currentGame;
    }

    @Override
    public int[][] getGame(char level)
            throws NotFoundException {

        DifficultyEnum diff = switch (Character.toLowerCase(level)) {
            case 'e' -> DifficultyEnum.EASY;
            case 'm' -> DifficultyEnum.MEDIUM;
            case 'h' -> DifficultyEnum.HARD;
            default -> throw new IllegalArgumentException("Invalid level: " + level);
        };

        return getGame(diff).getBoard();
    }

    public int[][] getCurrentGame() throws NotFoundException {
        int[][] board = StorageManager.loadCurrentGame();
        currentGame = new Game(board);
        currentDifficulty = null; // Unknown for resumed games
        return board;
    }

    public int[][] getCurrentBoard() {
        return currentGame != null ? currentGame.getBoard() : null;
    }

    // ================== Verification ==================

    @Override
    public String verifyGame(Game game) {

        VerificationResult result =
                verifier.verify(game.getBoard());

        if (result == VerificationResult.VALID) {
            // Game completed successfully
            if (currentDifficulty != null) {
                StorageManager.deleteSolvedGame(currentDifficulty);
            }
            return "valid";
        }

        if (result == VerificationResult.INCOMPLETE)
            return "incomplete";

        InvalidCells invalid =
                verifier.findInvalidCells(
                        game.getBoard());

        StringBuilder sb =
                new StringBuilder("invalid");

        for (String cell : invalid.getAll())
            sb.append(" ").append(cell);

        return sb.toString();
    }

    @Override
    public boolean[][] verifyGame(int[][] board) {

        boolean[][] ok = new boolean[9][9];

        InvalidCells invalid =
                verifier.findInvalidCells(board);

        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                ok[r][c] = !invalid.contains(r, c);

        return ok;
    }

    public void checkCompletion(int[][] board) {
        VerificationResult result = verifier.verify(board);

        if (result == VerificationResult.VALID) {
            if (currentDifficulty != null) {
                StorageManager.deleteSolvedGame(currentDifficulty);
            }
            StorageManager.deleteCurrentGame();
        }
    }

    // ================== Solver ==================

    @Override
    public int[] solveGame(Game game)
            throws InvalidGame {

        if (game == null)
            throw new InvalidGame("Game is null");

        if (game.countEmptyCells() != 5)
            throw new InvalidGame(
                    "Solver works only with exactly 5 empty cells");

        return solver.solve(game.getBoard());
    }

    @Override
    public int[][] solveGame(int[][] board)
            throws InvalidGame {

        if (board == null)
            throw new InvalidGame("Board is null");

        int empty = 0;
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (board[r][c] == 0)
                    empty++;

        if (empty != 5)
            throw new InvalidGame(
                    "Solver works only with exactly 5 empty cells");

        int[] solution = solver.solve(board);

        int[][] result = new int[5][3];
        for (int i = 0; i < 5; i++) {
            result[i][0] = solution[i * 3];
            result[i][1] = solution[i * 3 + 1];
            result[i][2] = solution[i * 3 + 2];
        }

        return result;
    }

    // ================== Undo ==================

    public UserAction undo() throws IOException {
        return UndoManager.undoLastMove();
    }

    public boolean canUndo() {
        return UndoManager.canUndo();
    }

    // ================== Logging ==================

    @Override
    public void logUserAction(String userAction)
            throws IOException {
        StorageManager.log(userAction);
    }

    @Override
    public void logUserAction(UserAction userAction)
            throws IOException {
        StorageManager.log(userAction.toString());

        if (userAction.getType() == UserAction.ActionType.MOVE) {
            StorageManager.saveCurrentGame(currentGame.getBoard());
        }
    }

    // ================== Move Handling ==================
    public void makeMove(int row, int col, int value) throws IOException {
        int prevValue = currentGame.getBoard()[row][col];
        currentGame.getBoard()[row][col] = value;

        UserAction action = new UserAction(row, col, value, prevValue);
        logUserAction(action);
    }
}