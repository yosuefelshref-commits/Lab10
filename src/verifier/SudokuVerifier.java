package verifier;

public class SudokuVerifier {

    public VerificationResult verify(int[][] board) {
        boolean hasZero = false;

        // Rows
        for (int r = 0; r < 9; r++) {
            boolean[] seen = new boolean[10];
            for (int c = 0; c < 9; c++) {
                int v = board[r][c];
                if (v == 0) {
                    hasZero = true;
                    continue;
                }
                if (seen[v]) return VerificationResult.INVALID;
                seen[v] = true;
            }
        }

        // Cols
        for (int c = 0; c < 9; c++) {
            boolean[] seen = new boolean[10];
            for (int r = 0; r < 9; r++) {
                int v = board[r][c];
                if (v == 0) {
                    hasZero = true;
                    continue;
                }
                if (seen[v]) return VerificationResult.INVALID;
                seen[v] = true;
            }
        }

        // Boxes
        for (int br = 0; br < 3; br++) {
            for (int bc = 0; bc < 3; bc++) {
                boolean[] seen = new boolean[10];
                for (int r = br * 3; r < br * 3 + 3; r++) {
                    for (int c = bc * 3; c < bc * 3 + 3; c++) {
                        int v = board[r][c];
                        if (v == 0) {
                            hasZero = true;
                            continue;
                        }
                        if (seen[v]) return VerificationResult.INVALID;
                        seen[v] = true;
                    }
                }
            }
        }

        return hasZero ? VerificationResult.INCOMPLETE : VerificationResult.VALID;
    }
    public InvalidCells findInvalidCells(int[][] board) {

        InvalidCells invalid = new InvalidCells();

        // Rows
        for (int r = 0; r < 9; r++) {
            int[] count = new int[10];
            for (int c = 0; c < 9; c++)
                if (board[r][c] != 0)
                    count[board[r][c]]++;

            for (int c = 0; c < 9; c++) {
                int v = board[r][c];
                if (v != 0 && count[v] > 1)
                    invalid.add(r, c);
            }
        }

        // Cols
        for (int c = 0; c < 9; c++) {
            int[] count = new int[10];
            for (int r = 0; r < 9; r++)
                if (board[r][c] != 0)
                    count[board[r][c]]++;

            for (int r = 0; r < 9; r++) {
                int v = board[r][c];
                if (v != 0 && count[v] > 1)
                    invalid.add(r, c);
            }
        }

        // Boxes
        for (int br = 0; br < 3; br++) {
            for (int bc = 0; bc < 3; bc++) {

                int[] count = new int[10];
                for (int r = br * 3; r < br * 3 + 3; r++)
                    for (int c = bc * 3; c < bc * 3 + 3; c++)
                        if (board[r][c] != 0)
                            count[board[r][c]]++;

                for (int r = br * 3; r < br * 3 + 3; r++)
                    for (int c = bc * 3; c < bc * 3 + 3; c++) {
                        int v = board[r][c];
                        if (v != 0 && count[v] > 1)
                            invalid.add(r, c);
                    }
            }
        }

        return invalid;
    }

}
