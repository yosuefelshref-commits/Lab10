package model;

public class Catalog {

    private boolean currentGameExists;
    private boolean allModesExist;

    public Catalog(boolean currentGameExists, boolean allModesExist) {
        this.currentGameExists = currentGameExists;
        this.allModesExist = allModesExist;
    }

    public boolean current() {
        return currentGameExists;
    }

    public boolean allModesExist() {
        return allModesExist;
    }
}
