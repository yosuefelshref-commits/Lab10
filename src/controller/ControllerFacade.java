package controller;

import model.*;
import generator.GameGenerator;
import storage.StorageManager;
import verifier.*;
import controller.exceptions.*;
import view.Controllable;
import view.UserAction;

import java.io.IOException;
import java.util.Map;
import java.util.Stack;

public class ControllerFacade implements Viewable, Controllable {

    private final GameGenerator generator = new GameGenerator();
    private final SudokuVerifier verifier = new SudokuVerifier();
    private final Stack<Move> history = new Stack<>();

    private Game currentGame;

    // ================== Catalog ==================

    @Override
    public Catalog getCatalog() {
        boolean hasCurrent = StorageManager.hasCurrentGame();
        boolean hasAll = StorageManager.hasAllDifficultyGames();
        return new Catalog(hasCurrent, hasAll);
    }

    // FIX: typo (Statues -> Status)
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
        history.clear();
        return currentGame;
    }

    @Override
    public int[][] getGame(char level)
            throws NotFoundException {

        DifficultyEnum diff =
                DifficultyEnum.valueOf(
                        String.valueOf(level).toUpperCase());

        return getGame(diff).getBoard();
    }

    // ================== Verification ==================

    @Override
    public String verifyGame(Game game) {

        VerificationResult result =
                verifier.verify(game.getBoard());

        if (result == VerificationResult.VALID)
            return "valid";

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

    // ================== Solver (Guards Only) ==================

    @Override
    public int[] solveGame(Game game)
            throws InvalidGame {

        if (game == null)
            throw new InvalidGame("Game is null");

        if (game.countEmptyCells() != 5)
            throw new InvalidGame(
                    "Solver works only with exactly 5 empty cells");

        throw new UnsupportedOperationException(
                "Solver not implemented yet");
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

        throw new UnsupportedOperationException(
                "Solver not implemented yet");
    }

    // ================== Game Play ==================

    @Override
    public void updateBoard(int row, int col, int value) {
        if (currentGame == null) return;
        int[][] board = currentGame.getBoard();
        int oldValue = board[row][col];
        board[row][col] = value;
        history.push(new Move(row, col, oldValue, value));
    }

    @Override
    public void undo() {
        if (history.isEmpty() || currentGame == null) return;
        Move move = history.pop();
        currentGame.getBoard()[move.getRow()][move.getCol()] = move.getOldValue();
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
    }

    @Override
    public boolean[] getCatalogStatues() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCatalogStatues'");
    }
}
