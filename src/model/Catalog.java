package model;

public class Catalog {

    private boolean currentGameExists;
    private boolean allModesExist;

    public Catalog(boolean currentGameExists, boolean allModesExist) {
        this.currentGameExists = currentGameExists;
        this.allModesExist = allModesExist;
    }

    // بدل hasUnfinished()
    public boolean current() {
        return currentGameExists;
    }

    // بدل hasEasyMediumHard()
    public boolean allModesExist() {
        return allModesExist;
    }
}
