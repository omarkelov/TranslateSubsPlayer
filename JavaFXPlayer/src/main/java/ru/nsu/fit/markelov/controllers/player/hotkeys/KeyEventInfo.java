package ru.nsu.fit.markelov.controllers.player.hotkeys;

public class KeyEventInfo {

    private final String name;
    private final String description;

    public KeyEventInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Returns name.
     *
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns description.
     *
     * @return description.
     */
    public String getDescription() {
        return description;
    }
}
