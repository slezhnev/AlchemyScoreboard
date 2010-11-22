/*
 * Copyright (c) 2010. Сергей Лежнев
 */

package ru.librarian;

public class Player implements Comparable {

    private String playerName;
    private int score = 0;
    private int color = android.R.color.white;

    public String getPlayerName() {
        return playerName;
    }

    public Player setPlayerName(String playerName) {
        this.playerName = playerName;
        return this;
    }

    public int getScore() {
        return score;
    }

    public Player setScore(int score) {
        this.score = score;
        return this;
    }

    public int getColor() {
        return color;
    }

    public Player setColor(int color) {
        this.color = color;
        return this;
    }

    public int compareTo(Object o) {
        if (!(o instanceof Player)) {
            return -1;
        } else {
            Player comp = (Player)o;
            if (comp.getScore() == score) {
                if (playerName == null) return -1;
                else return (playerName.compareTo(comp.getPlayerName()));
            } else
                return (comp.getScore() - score);
        }
    }
}
