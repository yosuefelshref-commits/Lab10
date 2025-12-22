package controller;

import controller.exceptions.*;
import model.*;
import view.*;

import java.io.IOException;

public class ControllerAdapter implements Viewable, Controllable {

    private final ControllerFacade controller;

    public ControllerAdapter(ControllerFacade controller) {
        this.controller = controller;
    }

    // Viewable

    @Override
    public Catalog getCatalog() {
        return controller.getCatalog();
    }

    @Override
    public Game getGame(DifficultyEnum level)
            throws NotFoundException {
        return controller.getGame(level);
    }

    @Override
    public void driveGames(Game sourceGame)
            throws SolutionInvalidException {
        controller.driveGames(sourceGame);
    }

    @Override
    public String verifyGame(Game game) {
        return controller.verifyGame(game);
    }

    @Override
    public int[] solveGame(Game game)
            throws InvalidGame {
        return controller.solveGame(game);
    }

    @Override
    public void logUserAction(String userAction)
            throws IOException {
        controller.logUserAction(userAction);
    }

    //  Controllable

    @Override
    public boolean[] getCatalogStatus() {
        return controller.getCatalogStatus();
    }

    @Override
    public int[][] getGame(char level)
            throws NotFoundException {
        return controller.getGame(level);
    }

    @Override
    public void driveGames(String sourcePath)
            throws SolutionInvalidException {
        controller.driveGames(sourcePath);
    }

    @Override
    public boolean[][] verifyGame(int[][] game) {
        return controller.verifyGame(game);
    }

    @Override
    public int[][] solveGame(int[][] game)
            throws InvalidGame {
        return controller.solveGame(game);
    }

    @Override
    public void logUserAction(UserAction userAction)
            throws IOException {
        controller.logUserAction(userAction);
    }

    //  Extra

    public int[][] getCurrentBoard() {
        return controller.getCurrentBoard();
    }

    public void makeMove(int row, int col, int value)
            throws IOException {
        controller.makeMove(row, col, value);
    }

    public boolean canUndo() {
        return controller.canUndo();
    }

    public UserAction undo()
            throws IOException {
        return controller.undo();
    }

    public void checkCompletion(int[][] board) {
        controller.checkCompletion(board);
    }

    public int[][] getCurrentGame()
            throws NotFoundException {
        return controller.getCurrentGame();
    }
}