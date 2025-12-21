package view;

import controller.ControllerAdapter;
import controller.ControllerFacade;
import controller.exceptions.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;

public class SudokuGUI extends JFrame {

    private final ControllerAdapter controller = new ControllerAdapter(new ControllerFacade());
    private JTextField[][] cells = new JTextField[9][9];
    private JButton verifyBtn, solveBtn, undoBtn;
    private JLabel statusLabel;
    private int[][] initialBoard;

    public SudokuGUI() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        initializeGame();

        add(createBoardPanel(), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeGame() {
        try {
            boolean[] catalog = controller.getCatalogStatus();
            boolean hasCurrent = catalog[0];
            boolean hasAllDifficulties = catalog[1];

            if (hasCurrent) {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Continue unfinished game?",
                        "Resume Game",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    controller.getCurrentGame();
                    copyInitialBoard();
                    return;
                }
            }

            if (hasAllDifficulties) {
                String[] options = {"Easy", "Medium", "Hard"};
                int choice = JOptionPane.showOptionDialog(
                        this,
                        "Choose difficulty:",
                        "New Game",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (choice >= 0) {
                    char level = options[choice].charAt(0);
                    controller.getGame(level);
                    copyInitialBoard();
                    return;
                }
            }

            // Need to generate games
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Solved Sudoku File");

            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                controller.driveGames(path);

                JOptionPane.showMessageDialog(
                        this,
                        "Games generated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Now choose difficulty
                String[] options = {"Easy", "Medium", "Hard"};
                int choice = JOptionPane.showOptionDialog(
                        this,
                        "Choose difficulty:",
                        "New Game",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (choice >= 0) {
                    char level = options[choice].charAt(0);
                    controller.getGame(level);
                    copyInitialBoard();
                }
            } else {
                System.exit(0);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * نسخ الـ board الأصلي عشان نعرف الخلايا الثابتة
     */
    private void copyInitialBoard() {
        int[][] currentBoard = getCurrentBoard();
        initialBoard = new int[9][9];
        for (int r = 0; r < 9; r++) {
            System.arraycopy(currentBoard[r], 0, initialBoard[r], 0, 9);
        }
    }

    /**
     * الحصول على الـ board الحالي من الـ controller
     */
    private int[][] getCurrentBoard() {
        return controller.getCurrentBoard();
    }

    private JPanel createBoardPanel() {
        JPanel panel = new JPanel(new GridLayout(9, 9, 2, 2));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.BLACK);

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                cells[r][c] = createCell(r, c);
                panel.add(cells[r][c]);
            }
        }

        return panel;
    }

    private JTextField createCell(int row, int col) {
        JTextField field = new JTextField();
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setFont(new Font("Arial", Font.BOLD, 20));

        // Color 3x3 boxes
        if ((row / 3 + col / 3) % 2 == 0) {
            field.setBackground(new Color(240, 240, 240));
        } else {
            field.setBackground(Color.WHITE);
        }

        int[][] board = getCurrentBoard();
        int value = board[row][col];

        // Set initial value
        if (value != 0) {
            field.setText(String.valueOf(value));

            // الخلايا الأصلية (من الـ initialBoard)
            if (initialBoard[row][col] != 0) {
                field.setEditable(false);
                field.setForeground(Color.BLACK);
                field.setFont(new Font("Arial", Font.BOLD, 22));
            } else {
                // خلايا اتملت من اللاعب
                field.setForeground(Color.BLUE);
            }
        } else {
            field.setForeground(Color.BLUE);
        }

        // Add border to 3x3 boxes
        updateCellBorder(field, row, col, false);

        // Add listener for moves
        field.addActionListener(e -> handleCellInput(row, col, field));

        // Add focus listener
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                handleCellInput(row, col, field);
            }
        });

        return field;
    }

    private void updateCellBorder(JTextField field, int row, int col, boolean isInvalid) {
        int top = (row % 3 == 0) ? 2 : 0;
        int left = (col % 3 == 0) ? 2 : 0;
        int bottom = (row % 3 == 2) ? 2 : 0;
        int right = (col % 3 == 2) ? 2 : 0;

        if (isInvalid) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Color.RED, 3),
                    BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK)
            ));
        } else {
            field.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
        }
    }

    private void handleCellInput(int row, int col, JTextField field) {
        // لو الخلية أصلية (غير قابلة للتعديل)
        if (initialBoard[row][col] != 0) {
            return;
        }

        String text = field.getText().trim();
        int[][] board = getCurrentBoard();

        try {
            int value;
            if (text.isEmpty()) {
                value = 0;
            } else {
                value = Integer.parseInt(text);
                if (value < 1 || value > 9) {
                    throw new NumberFormatException();
                }
            }

            int prevValue = board[row][col];

            if (value == prevValue) return; // No change

            controller.makeMove(row, col, value);
            field.setText(value == 0 ? "" : String.valueOf(value));
            field.setForeground(Color.BLUE);

            statusLabel.setText("Move applied: (" + row + ", " + col + ") = " + value);

            updateSolveButton();
            checkCompletion();

        } catch (NumberFormatException ex) {
            int currentValue = board[row][col];
            field.setText(currentValue == 0 ? "" : String.valueOf(currentValue));
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a number between 1-9 (or empty to clear)",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error logging move: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        verifyBtn = new JButton("Verify");
        verifyBtn.addActionListener(e -> handleVerify());

        solveBtn = new JButton("Solve");
        solveBtn.addActionListener(e -> handleSolve());
        updateSolveButton();

        undoBtn = new JButton("Undo");
        undoBtn.addActionListener(e -> handleUndo());

        btnPanel.add(verifyBtn);
        btnPanel.add(solveBtn);
        btnPanel.add(undoBtn);

        // Status label
        statusLabel = new JLabel("Ready");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(btnPanel, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateSolveButton() {
        int[][] board = getCurrentBoard();
        int empty = 0;
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (board[r][c] == 0)
                    empty++;

        solveBtn.setEnabled(empty == 5);

        if (empty == 5) {
            solveBtn.setToolTipText("Click to solve remaining 5 cells");
        } else {
            solveBtn.setToolTipText("Solver only works with exactly 5 empty cells (current: " + empty + ")");
        }
    }

    private void handleVerify() {
        int[][] board = getCurrentBoard();
        boolean[][] valid = controller.verifyGame(board);

        // Highlight invalid cells
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                updateCellBorder(cells[r][c], r, c, !valid[r][c]);
            }
        }

        // Count invalid cells
        int invalidCount = 0;
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (!valid[r][c])
                    invalidCount++;

        if (invalidCount == 0) {
            statusLabel.setText("✓ All cells are valid!");
            statusLabel.setForeground(new Color(0, 150, 0));
        } else {
            statusLabel.setText("✗ Found " + invalidCount + " invalid cell(s) - highlighted in red");
            statusLabel.setForeground(Color.RED);
        }

        // Reset color after 3 seconds
        Timer timer = new Timer(3000, e -> statusLabel.setForeground(Color.BLACK));
        timer.setRepeats(false);
        timer.start();
    }

    private void handleSolve() {
        try {
            int[][] board = getCurrentBoard();
            int[][] solution = controller.solveGame(board);

            StringBuilder sb = new StringBuilder("Solution found:\n\n");
            for (int[] cell : solution) {
                int r = cell[0];
                int c = cell[1];
                int val = cell[2];

                cells[r][c].setText(String.valueOf(val));
                cells[r][c].setForeground(new Color(0, 150, 0)); // Green for solved

                sb.append(String.format("Cell (%d, %d) = %d\n", r, c, val));
            }

            JOptionPane.showMessageDialog(
                    this,
                    sb.toString(),
                    "Solution Applied",
                    JOptionPane.INFORMATION_MESSAGE
            );

            statusLabel.setText("Solution applied successfully");
            updateSolveButton();
            checkCompletion();

        } catch (InvalidGame e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Cannot Solve",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void handleUndo() {
        if (!controller.canUndo()) {
            statusLabel.setText("Nothing to undo");
            return;
        }

        try {
            UserAction action = controller.undo();

            int r = action.getRow();
            int c = action.getCol();
            int val = action.getNewValue(); // القيمة اللي هنرجعها

            // Update GUI
            cells[r][c].setText(val == 0 ? "" : String.valueOf(val));
            cells[r][c].setForeground(Color.BLUE);

            // Clear any error borders
            updateCellBorder(cells[r][c], r, c, false);

            statusLabel.setText("Undone: Cell (" + r + ", " + c + ") restored to " + (val == 0 ? "empty" : val));
            updateSolveButton();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error during undo: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    private void checkCompletion() {
        int[][] board = getCurrentBoard();
        int empty = 0;
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (board[r][c] == 0)
                    empty++;

        if (empty == 0) {
            boolean[][] valid = controller.verifyGame(board);

            boolean allValid = true;
            for (int r = 0; r < 9; r++)
                for (int c = 0; c < 9; c++)
                    if (!valid[r][c])
                        allValid = false;

            if (allValid) {
                controller.checkCompletion(board);

                JOptionPane.showMessageDialog(
                        this,
                        "🎉 Congratulations! Puzzle solved correctly! 🎉",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Ask to play again
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Play another game?",
                        "Game Complete",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                    SwingUtilities.invokeLater(SudokuGUI::new);
                } else {
                    System.exit(0);
                }
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Board is full but contains errors!",
                        "Invalid Solution",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SudokuGUI::new);
    }
}