package storage;

import model.DifficultyEnum;
import model.Game;
import controller.exceptions.NotFoundException;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

public class StorageManager {

    private static final String BASE = "storage";
    private static final String EASY = BASE + "/easy";
    private static final String MEDIUM = BASE + "/medium";
    private static final String HARD = BASE + "/hard";
    private static final String CURRENT = BASE + "/current";
    private static final String GAME_FILE = "game.txt";
    private static final String LOG_FILE = "log.txt";

    static {
        createDir(BASE);
        createDir(EASY);
        createDir(MEDIUM);
        createDir(HARD);
        createDir(CURRENT);
    }

    private static void createDir(String path) {
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + path);
        }
    }

    public static boolean hasCurrentGame() {
        return new File(CURRENT, GAME_FILE).exists();
    }

    public static boolean hasAllDifficultyGames() {
        return hasGame(EASY) && hasGame(MEDIUM) && hasGame(HARD);
    }

    private static boolean hasGame(String dir) {
        File folder = new File(dir);
        return folder.exists() && folder.listFiles().length > 0;
    }

    public static void saveGeneratedGames(
            Map<DifficultyEnum, Game> games) {

        games.forEach((level, game) -> {
            String path = switch (level) {
                case EASY -> EASY;
                case MEDIUM -> MEDIUM;
                case HARD -> HARD;
            };
            saveBoard(path + "/game_" + System.nanoTime() + ".txt",
                    game.getBoard());
        });
    }

    public static Game loadGame(DifficultyEnum level)
            throws NotFoundException {

        String dir = switch (level) {
            case EASY -> EASY;
            case MEDIUM -> MEDIUM;
            case HARD -> HARD;
        };

        File folder = new File(dir);
        File[] files = folder.listFiles();

        if (files == null || files.length == 0)
            throw new NotFoundException("No game found");

        int[][] board = loadBoard(files[0].getPath());
        saveCurrentGame(board);

        return new Game(board);
    }

    public static int[][] loadCurrentGame()
            throws NotFoundException {

        File file = new File(CURRENT, GAME_FILE);

        if (!file.exists())
            throw new NotFoundException("No current game");

        return loadBoard(file.getPath());
    }

    public static void saveCurrentGame(int[][] board) {
        saveBoard(CURRENT + "/" + GAME_FILE, board);
    }

    public static void deleteSolvedGame(DifficultyEnum level) {

        String dir = switch (level) {
            case EASY -> EASY;
            case MEDIUM -> MEDIUM;
            case HARD -> HARD;
        };

        File folder = new File(dir);
        for (File f : folder.listFiles())
            f.delete();

        deleteCurrentGame();
    }

    public static void deleteCurrentGame() {
        new File(CURRENT, GAME_FILE).delete();
        new File(CURRENT, LOG_FILE).delete();
    }

    private static void saveBoard(String path, int[][] board) {
        try (PrintWriter pw = new PrintWriter(path)) {
            for (int[] row : board) {
                for (int val : row)
                    pw.print(val + " ");
                pw.println();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save board");
        }
    }

    private static int[][] loadBoard(String path) {
        int[][] board = new int[9][9];
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            for (int i = 0; i < 9; i++) {
                String[] parts = br.readLine().split(" ");
                for (int j = 0; j < 9; j++)
                    board[i][j] = Integer.parseInt(parts[j]);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load board");
        }
        return board;
    }

    public static void log(String entry) throws IOException {
        try (FileWriter fw = new FileWriter(
                CURRENT + "/" + LOG_FILE, true)) {
            fw.write(entry + System.lineSeparator());
        }
    }
    public static int[][] loadSolvedBoard(String path) {

        int[][] board = new int[9][9];

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            for (int i = 0; i < 9; i++) {
                String[] parts = br.readLine().trim().split("\\s+");
                for (int j = 0; j < 9; j++) {
                    board[i][j] = Integer.parseInt(parts[j]);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load solved board from file");
        }

        return board;
    }
}

