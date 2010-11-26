/*
 * Copyright (c) 2010. Сергей Лежнев
 */

package ru.librarian;

/**
 * Один ход
 */
public class Turn {
    private Player player;
    private int scoreDiff;

    public Player getPlayer() {
        return player;
    }

    public Turn setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public int getScoreDiff() {
        return scoreDiff;
    }

    public Turn setScoreDiff(int scoreDiff) {
        this.scoreDiff = scoreDiff;
        return this;
    }
}
