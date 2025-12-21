package view;

import controller.exceptions.*;

import java.io.IOException;

public interface Controllable {

    boolean[] getCatalogStatues();

    int[][] getGame(char level)
            throws NotFoundException;

    void driveGames(String sourcePath) throws SolutionInvalidException;

    boolean[][] verifyGame(int[][] game);

    int[][] solveGame(int[][] game)
            throws InvalidGame;

    void updateBoard(int row, int col, int value);

    void undo();

    void logUserAction(UserAction userAction)
       throws IOException;
}
