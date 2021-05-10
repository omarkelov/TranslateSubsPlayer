package ru.nsu.fit.markelov.user.entities;

import java.util.List;

public class Translation {

    private final String main;
    private final List<Group> groups;

    public Translation(String main, List<Group> groups) {
        this.main = main;
        this.groups = groups;
    }
}
