package solver;

public class Digit {

    private final int value;

    private Digit(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    //  Flyweight Factory
    public static class Factory {
        private static final Digit[] pool = new Digit[10];

        public static Digit get(int value) {
            if (pool[value] == null)
                pool[value] = new Digit(value);
            return pool[value];
        }
    }
}