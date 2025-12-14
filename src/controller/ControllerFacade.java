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

public class ControllerFacade implements Viewable, Controllable {

    private final GameGenerator generator = new GameGenerator();
    private final SudokuVerifier verifier = new SudokuVerifier();

    private Game currentGame;

    @Override
    public Catalog getCatalog() {
        boolean hasCurrent = StorageManager.hasCurrentGame();
        boolean hasAll = StorageManager.hasAllDifficultyGames();
        return new Catalog(hasCurrent, hasAll);
    }


    @Override
    public boolean[] getCatalogStatues() {
        Catalog c = getCatalog();
        return new boolean[]{
                c.current(),
                c.allModesExist()
        };
    }

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

    @Override
    public Game getGame(DifficultyEnum level)
            throws NotFoundException {

        currentGame = StorageManager.loadGame(level);
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

    @Override
    public int[] solveGame(Game game)
            throws InvalidGame {
        throw new UnsupportedOperationException(
                "Solver not implemented yet");
    }

    @Override
    public int[][] solveGame(int[][] game)
            throws InvalidGame {
        throw new UnsupportedOperationException(
                "Solver not implemented yet");
    }

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
}
