/*
 * Copyright (c) 2010. Сергей Лежнев
 */

package ru.librarian;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Класс - хранилище player'ов.
 * Пока я что-то не разобрался - в каком именно порядке создаются компоненты.
 * Так что список игроков будем тупо хранить в статике.
 */
public class PlayersStorage {

    /**
     * Игроки
     * <b>ВНИМАНИЕ!</b> ПРЯМАЯ РАБОТА СО СПИСКОМ - ЗАПРЕЩЕНА!
     * Методы работы со списком - ниже. При работе напрямую - не будут вызываться
     * обновления адаптеров!
     */
    private static ArrayList<Player> players = new ArrayList<Player>();

    /**
     * Список adapter'ов, которые надо будет передернуть при обновлении списка игроков
     * Пока не понятно чего там с многопоточностью - сделаем его синхронизированным.
     */
    private static List<PlayerAdapter> playersAdapters = Collections.synchronizedList(new ArrayList<PlayerAdapter>());

    /**
     * Список нотификаторов, которые будут вызываться при изменениее текущего игрока
     */
    private static List<ChangeNotification> currentPlayerChangeNotificators = Collections.synchronizedList(new ArrayList<ChangeNotification>());

    /**
     * Текущий активный игрок
     */
    private static Player currentPlayer = null;

    public static ArrayList<Player> getPlayers() {
        return players;
    }

    static {
        players.add(new Player().setPlayerName("Игрок1").setColor(Color.parseColor("#CC00CC")));
        players.add(new Player().setPlayerName("Игрок2").setColor(Color.parseColor("#CC00CC")).setScore(-5));
        players.add(new Player().setPlayerName("Игрок3").setColor(Color.parseColor("#CC00CC")));
        players.add(new Player().setPlayerName("Игрок4").setColor(Color.parseColor("#CC00CC")));
        players.add(new Player().setPlayerName("Игрок5").setColor(Color.parseColor("#CC00CC")));
        players.add(new Player().setPlayerName("Игрок6").setColor(Color.parseColor("#CC00CC")));
        players.add(new Player().setPlayerName("Игрок7").setColor(Color.parseColor("#CC00CC")));
        players.add(new Player().setPlayerName("Игрок8").setColor(Color.parseColor("#CC00CC")));
        players.add(new Player().setPlayerName("Игрок9").setColor(Color.parseColor("#CC00CC")));
        players.add(new Player().setPlayerName("Игрок10").setColor(Color.parseColor("#CC00CC")));
        players.add(new Player().setPlayerName("Игрок11").setColor(Color.parseColor("#CC00CC")));
    }

    public static List<PlayerAdapter> getPlayersAdapters() {
        return playersAdapters;
    }

    /**
     * Рассылка нотификаций об изменений по адаптерам
     * А public он - потому что используется в PlayersActivity...
     */
    public static void playersChanged() {
        for (PlayerAdapter adapter : playersAdapters) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Добавление нового игрока
     *
     * @param player Новый игрок
     */
    public static void addPlayer(Player player) {
        if (player != null) {
            players.add(player);
            playersChanged();
        }
        // Если текущий пользователь не выбран - выбираем вот этого добавленного
        if (currentPlayer == null) {
            setCurrentPlayer(player);
        }
    }

    /**
     * Изменение счета игрока
     *
     * @param player    Игрок, у которого меняется счет
     * @param scoreDiff На сколько меняется счет
     */
    public static void addPlayerScore(Player player, int scoreDiff) {
        if ((player != null)&&(players.indexOf(player) != -1)) {
            player.setScore(player.getScore() + scoreDiff);
            playersChanged();
        }
    }

    /**
     * Установка имени и цвета игрока
     *
     * @param currPlayerName Текущее имя игрока
     * @param newPlayerName Новое имя игрока (может быть null - тогда не будет устанавливаться)
     * @param color Цвет выделения игрока
     */
    public static void setPlayerParams(String currPlayerName, String newPlayerName, int color) {
        if (currPlayerName != null) {
            for (Player player : players) {
                if (currPlayerName.equals(player.getPlayerName())) {
                    // Нашли - пошли апдейтить
                    if (newPlayerName != null) player.setPlayerName(newPlayerName);
                    player.setColor(color);
                    // Fire in the hole!
                    playersChanged();
                    if (currentPlayer == player) {
                        setCurrentPlayer(currentPlayer);
                    }
                    // Завершаем
                    break;
                }
            }
        }
    }

    /**
     * Переместить игрока в списке игроков ВНИЗ
     * @param player Игрок
     */
    public static void movePlayerDown ( Player player ) {
        int p_i = players.indexOf(player); 
        if ((player != null)&&(p_i != -1)&&(p_i != (players.size()-1))) {
            Player nextPlayer = players.get(p_i+1);
            players.set(p_i+1, player);
            players.set(p_i, nextPlayer);
            // Fire in the hole!
            playersChanged();
        }
    }

    /**
     * Переместить игрока в списке игроков ВВЕРХ
     * @param player Игрок
     */
    public static void movePlayerUp ( Player player ) {
        int p_i = players.indexOf(player);
        if ((player != null)&&(p_i != -1)&&(p_i != 0)) {
            Player nextPlayer = players.get(p_i-1);
            players.set(p_i-1, player);
            players.set(p_i, nextPlayer);
            // Fire in the hole!
            playersChanged();
        }
    }


    public static List<ChangeNotification> getCurrentPlayerChangeNotificators() {
        return currentPlayerChangeNotificators;
    }

    public static Player getCurrentPlayer() {
        return currentPlayer;
    }

    public static void setCurrentPlayer(Player currentPlayer) {
        // Выбирать мы можем только из игроков из общего списка
        if (players.indexOf(currentPlayer) != -1) {
            PlayersStorage.currentPlayer = currentPlayer;
            for (ChangeNotification notification : currentPlayerChangeNotificators) {
                notification.changeOccurs();
            }
        }
    }

    public static void removePlayer(String playerName) {
        if (playerName == null) return;
        for (Player player : players) {
            if (playerName.equals(player.getPlayerName())) {
                // Нашли. Поехали удалять, и прочее
                if (player == currentPlayer) {
                    if (players.size() > 1) {
                        // А в этом случае сбросим селектор на первый или второй (второй - если первый удалем)
                        if (players.get(0) == player) {
                            setCurrentPlayer(players.get(1));
                        } else {
                            setCurrentPlayer(players.get(0));                            
                        }
                    } else {
                        // Тут он всего один - он щас смело грохнется и все - список будет пустым
                        setCurrentPlayer(null);
                    }
                }
                // Удаляем
                players.remove(player);
                // fire in the hole!
                playersChanged();
                // Пошли отсель
                break;
            }
        }
    }

}
