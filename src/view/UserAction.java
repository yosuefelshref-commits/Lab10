package view;

public class UserAction {

    private final ActionType type;
    private final int row;
    private final int col;
    private final int newValue;
    private final int prevValue;

    public enum ActionType {
        MOVE,
        VERIFY,
        SOLVE,
        UNDO,
        LOAD_GAME,
        EXIT
    }

    // Constructor for MOVE action
    public UserAction(int row, int col, int newValue, int prevValue) {
        this.type = ActionType.MOVE;
        this.row = row;
        this.col = col;
        this.newValue = newValue;
        this.prevValue = prevValue;
    }

    // Constructor for other actions
    public UserAction(ActionType type) {
        this.type = type;
        this.row = -1;
        this.col = -1;
        this.newValue = -1;
        this.prevValue = -1;
    }

    public ActionType getType() {
        return type;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getNewValue() {
        return newValue;
    }

    public int getPrevValue() {
        return prevValue;
    }

    @Override
    public String toString() {
        if (type == ActionType.MOVE) {
            return String.format("(%d,%d,%d,%d)",
                    row, col, newValue, prevValue);
        }
        return type.name();
    }

    public static UserAction fromString(String line) {
        if (line.startsWith("(")) {
            // Parse "(x,y,val,prev)"
            String content = line.substring(1, line.length() - 1);
            String[] parts = content.split(",");

            return new UserAction(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3])
            );
        }

        return new UserAction(ActionType.valueOf(line));
    }
}