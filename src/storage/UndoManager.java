package storage;

import view.UserAction;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


public class UndoManager {

    private static final String LOG_PATH = "storage/current/log.txt";

    public static UserAction undoLastMove() throws IOException {

        File logFile = new File(LOG_PATH);

        if (!logFile.exists()) {
            throw new IOException("No log file found");
        }

        // Read all lines
        List<String> lines = Files.readAllLines(logFile.toPath());

        if (lines.isEmpty()) {
            throw new IOException("No moves to undo");
        }

        // Get last line
        String lastLine = lines.get(lines.size() - 1);

        // Parse action
        UserAction lastAction = UserAction.fromString(lastLine);

        if (lastAction.getType() != UserAction.ActionType.MOVE) {
            throw new IOException("Cannot undo non-MOVE action");
        }

        // Remove last line from file
        lines.remove(lines.size() - 1);
        Files.write(logFile.toPath(), lines);

        // Return inverse action (restore previous value)
        return new UserAction(
                lastAction.getRow(),
                lastAction.getCol(),
                lastAction.getPrevValue(), // Restore previous value
                lastAction.getNewValue()
        );
    }

    public static boolean canUndo() {
        File logFile = new File(LOG_PATH);

        if (!logFile.exists()) {
            return false;
        }

        try {
            List<String> lines = Files.readAllLines(logFile.toPath());
            return !lines.isEmpty();
        } catch (IOException e) {
            return false;
        }
    }

    public static void clearLog() {
        File logFile = new File(LOG_PATH);

        if (logFile.exists()) {
            logFile.delete();
        }
    }
}