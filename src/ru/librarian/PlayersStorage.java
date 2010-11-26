/*
 * Copyright (c) 2010. Сергей Лежнев
 */

package ru.librarian;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import java.util.ArrayList;
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
     * обновления адаптеров! Да и сохранятся ничего не будет
     */
    //TODO Переделать на TreeMap - что-то я тут как-то ступил несколько
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

    /**
     * Список ходов
     */
    private static List<Turn> turns = new ArrayList<Turn>();

    /**
     * Список нотификаторов изменения списка ходов
     */
    private static List<TurnsChangeNotification> turnsChnageNotificators = Collections.synchronizedList(new ArrayList<TurnsChangeNotification>());

    //

    public static ArrayList<Player> getPlayers() {
        return players;
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
     * Изменение счета игрока
     *
     * @param activity  Activity для сохранения игрока
     * @param player    Игрок, у которого меняется счет
     * @param scoreDiff На сколько меняется счет
     */
    public static void addPlayerScore(Activity activity, Player player, int scoreDiff) {
        if ((player != null) && (players.indexOf(player) != -1)) {
            player.setScore(player.getScore() + scoreDiff);
            playersChanged();
            savePlayer(activity, player);
            // Создаем turn!
            Turn turn = new Turn();
            turn.setPlayer(player).setScoreDiff(scoreDiff);
            turns.add(turn);
            saveTurn(activity, turn, turns.size() - 1);
            turnsChanged();
        }
    }

    /**
     * Установка имени и цвета игрока.
     * Если currPlayerName == "", то игрок добавляется
     *
     * @param activity       Activity для сохранения параметров игркоа
     * @param currPlayerName Текущее имя игрока
     * @param newPlayerName  Новое имя игрока (может быть null - тогда не будет устанавливаться)
     * @param color          Цвет выделения игрока
     */
    public static void setPlayerParams(Activity activity, String currPlayerName, String newPlayerName, int color) {
        if ((currPlayerName != null) && (newPlayerName != null)) {
            // Вначале проверим - а нет ли у нас уже игрока с таким именем?
            boolean duplPlayerFound = false;
            for (Player player : players) {
                if (newPlayerName.equals(player.getPlayerName())) {
                    duplPlayerFound = true;
                    break;
                }
            }
            if (!duplPlayerFound) {
                if (currPlayerName.length() == 0) {
                    // А тут мы на самом деле добавляем
                    Player player = new Player();
                    player.setPlayerName(newPlayerName);
                    player.setColor(color);
                    players.add(player);
                    playersChanged();
                    //
                    savePlayer(activity, player, players.size() - 1);
                    //
                    // Если текущий пользователь не выбран - выбираем вот этого добавленного
                    if (currentPlayer == null) {
                        setCurrentPlayer(activity, player);
                    }
                } else {
                    for (Player player : players) {
                        if (currPlayerName.equals(player.getPlayerName())) {
                            // Нашли - пошли апдейтить
                            player.setPlayerName(newPlayerName);
                            player.setColor(color);
                            // Fire in the hole!
                            playersChanged();
                            if (currentPlayer == player) {
                                setCurrentPlayer(activity, currentPlayer);
                            }
                            // Завершаем
                            savePlayer(activity, player);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Переместить игрока в списке игроков ВНИЗ
     *
     * @param activity Activity для сохранения параметров игркоа
     * @param player   Игрок
     */
    public static void movePlayerDown(Activity activity, Player player) {
        int p_i = players.indexOf(player);
        if ((player != null) && (p_i != -1) && (p_i != (players.size() - 1))) {
            Player nextPlayer = players.get(p_i + 1);
            players.set(p_i + 1, player);
            players.set(p_i, nextPlayer);
            // Fire in the hole!
            playersChanged();
            //
            savePlayers(activity);
        }
    }

    /**
     * Переместить игрока в списке игроков ВВЕРХ
     *
     * @param activity Activity для сохранения параметров игркоа
     * @param player   Игрок
     */
    public static void movePlayerUp(Activity activity, Player player) {
        int p_i = players.indexOf(player);
        if ((player != null) && (p_i != -1) && (p_i != 0)) {
            Player nextPlayer = players.get(p_i - 1);
            players.set(p_i - 1, player);
            players.set(p_i, nextPlayer);
            // Fire in the hole!
            playersChanged();
            //
            savePlayers(activity);
        }
    }


    public static List<ChangeNotification> getCurrentPlayerChangeNotificators() {
        return currentPlayerChangeNotificators;
    }

    public static Player getCurrentPlayer() {
        return currentPlayer;
    }

    public static void setCurrentPlayer(Activity activity, Player currentPlayer) {
        // Выбирать мы можем только из игроков из общего списка
        if (players.indexOf(currentPlayer) != -1) {
            PlayersStorage.currentPlayer = currentPlayer;
            for (ChangeNotification notification : currentPlayerChangeNotificators) {
                notification.changeOccurs();
            }
            // Сохраняем текущего игрока
        }
        SharedPreferences.Editor editor = activity.getSharedPreferences("AlchemyScoreboard.Players", Context.MODE_PRIVATE).edit();
        if (PlayersStorage.currentPlayer != null) {
            editor.putString("currentPlayer", PlayersStorage.currentPlayer.getPlayerName());
        } else {
            editor.remove("currentPlayer");
        }
        //
        editor.commit();
    }

    public static void removePlayer(Activity activity, String playerName) {
        if (playerName == null) return;
        for (Player player : players) {
            if (playerName.equals(player.getPlayerName())) {
                // Нашли. Поехали удалять, и прочее
                if (player == currentPlayer) {
                    if (players.size() > 1) {
                        // А в этом случае сбросим селектор на первый или второй (второй - если первый удалем)
                        if (players.get(0) == player) {
                            setCurrentPlayer(activity, players.get(1));
                        } else {
                            setCurrentPlayer(activity, players.get(0));
                        }
                    } else {
                        // Тут он всего один - он щас смело грохнется и все - список будет пустым
                        setCurrentPlayer(activity, null);
                    }
                }
                // Удаляем
                players.remove(player);
                // fire in the hole!
                playersChanged();
                // Сохраняем
                savePlayers(activity);
                // Удаляем ходы с этим игроком
                removeTurnsByPlayer(activity, player);
                // Пошли отсель
                break;
            }
        }
    }

    public static void savePlayers(Activity activity) {
        // Сохраняем ВСЕХ игроков
        for (int i = 0; i < players.size(); i++) {
            savePlayer(activity, players.get(i), i);
        }
    }

    public static void savePlayer(Activity activity, Player player) {
        savePlayer(activity, player, players.indexOf(player));
    }

    /**
     * Сохранение игрока со счетом
     *
     * @param activity  Activity для получения preferences
     * @param player    Игрок для сохранения
     * @param playerIdx Индекс игрока в players
     */
    public static void savePlayer(Activity activity, Player player, int playerIdx) {
        //
        SharedPreferences.Editor editor = activity.getSharedPreferences("AlchemyScoreboard.Players", Context.MODE_PRIVATE).edit();
        // Поехали по игрокам
        editor.putInt("playersCount", players.size());
        editor.putString("player" + playerIdx + "Name", player.getPlayerName());
        editor.putInt("player" + playerIdx + "Score", player.getScore());
        editor.putInt("player" + playerIdx + "Color", player.getColor());
        //
        editor.commit();
    }

    /**
     * Загрузка списка игроков со счетом и текущим игроком
     *
     * @param activity Activity для получения preferences
     */
    public static void loadPlayers(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("AlchemyScoreboard.Players", Context.MODE_PRIVATE);
        // Чистим все...
        players.clear();
        currentPlayer = null;
        //
        int totalPlayers = prefs.getInt("playersCount", 0);
        for (int i = 0; i < totalPlayers; i++) {
            String playerName = prefs.getString("player" + i + "Name", null);
            if ((playerName != null) && (playerName.trim().length() != 0)) {
                Player player = new Player();
                player.setPlayerName(playerName);
                player.setScore(prefs.getInt("player" + i + "Score", 0));
                player.setColor(prefs.getInt("player" + i + "Color", Color.parseColor("#FFFFFF")));
                players.add(player);
            }
        }
        playersChanged();
        // Загружаем текущего
        String currPlayerName = prefs.getString("currentPlayer", null);
        if (currPlayerName == null) {
            if (PlayersStorage.getPlayers().size() > 0) {
                PlayersStorage.setCurrentPlayer(activity, PlayersStorage.getPlayers().get(0));
            }
        } else {
            // А тут его надо поискать
            for (Player player : PlayersStorage.getPlayers()) {
                if (currPlayerName.equals(player.getPlayerName())) {
                    // Нашли. Устанавливаем
                    PlayersStorage.setCurrentPlayer(activity, player);
                    break;
                }
            }
            if ((PlayersStorage.getCurrentPlayer() == null) && (PlayersStorage.getPlayers().size() > 0)) {
                // Если вдруг текущего игрока не нашлось - то выставляем ПЕРВОГО
                PlayersStorage.setCurrentPlayer(activity, PlayersStorage.getPlayers().get(0));
            }
        }
    }

    public static List<Turn> getTurns() {
        return turns;
    }

    public static void saveTurn(Activity activity, Turn turn, int turnIdx) {
        SharedPreferences.Editor editor = activity.getSharedPreferences("AlchemyScoreboard.Turns", Context.MODE_PRIVATE).edit();
        editor.putInt("turnsCount", turns.size());
        editor.putString("turn" + turnIdx + "Player", turn.getPlayer().getPlayerName());
        editor.putInt("turn" + turnIdx + "ScoreDiff", turn.getScoreDiff());
        editor.commit();
    }

    public static void saveTurns(Activity activity) {
        for (int i = 0; i < turns.size(); i++) {
            saveTurn(activity, turns.get(i), i);
        }
    }

    public static void loadTurns(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("AlchemyScoreboard.Turns", Context.MODE_PRIVATE);
        turns.clear();
        int turnsTotal = prefs.getInt("turnsCount", 0);
        for (int i = 0; i < turnsTotal; i++) {
            String playerName = prefs.getString("turn" + i + "Player", null);
            if (playerName != null) {
                // Поехали искать этого игрока в списке игроков...
                for (Player player : players) {
                    if (playerName.equals(player.getPlayerName())) {
                        // Все нашли...
                        Turn turn = new Turn();
                        turn.setPlayer(player);
                        turn.setScoreDiff(prefs.getInt("turn" + i + "ScoreDiff", 0));
                        turns.add(turn);
                    }
                }
            }
        }
        turnsChanged();
    }

    public static List<TurnsChangeNotification> getTurnsChnageNotificators() {
        return turnsChnageNotificators;
    }

    private static void removeTurnsByPlayer (Activity activity, Player player) {
        int i = 0;
        while (i < turns.size()) {
            if (player.getPlayerName().equals(turns.get(i).getPlayer().getPlayerName())) {
                // Нашли - это ход удаляемого игрока
                turns.remove(i);
            } else {
                i++;
            }
        }
        // Теперь - струльнем...
        turnsChanged();
        saveTurns(activity);
    }

    public static void removeTurn(Activity activity, Turn turn) {
        if (players.indexOf(turn.getPlayer()) != -1) {
            // Еще обработаем score у игрока
            turn.getPlayer().setScore(turn.getPlayer().getScore() - turn.getScoreDiff());
            playersChanged();
            savePlayer(activity, turn.getPlayer());
            //addPlayerScore(activity, turn.getPlayer(), 0 - turn.getScoreDiff());
        }
        turns.remove(turn);
        turnsChanged();
        saveTurns(activity);
    }

    /**
     * Вызыватель нотификаторов об изменении списка ходов
     */
    private static void turnsChanged() {
        for (TurnsChangeNotification notificator : turnsChnageNotificators) {
            notificator.turnsChangeOccurs();
        }
    }

    
}
