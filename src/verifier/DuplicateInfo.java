package verifier;

public class DuplicateInfo {

    private final int row;
    private final int col;
    private final int value;

    public DuplicateInfo(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getValue() {
        return value;
    }
}
