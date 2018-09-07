package com.smarthouserental.model;

public class Replay {
    private String replay;
    private String name;

    public Replay(String replay, String name) {
        this.replay = replay;
        this.name = name;
    }

    public Replay() {
    }

    public String getReplay() {
        return replay;
    }

    public String getName() {
        return name;
    }
}
