/*
 * Copyright (c) 2010. Сергей Лежнев
 */

package ru.librarian;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TurnActivity extends ListActivity implements ChangeNotification {

    private PlayerAdapter playerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.turn);
        //
        playerAdapter = new PlayerAdapter(this, R.layout.turn_row, R.id.turnLVLeftText, R.id.turnLVRightText);
        setListAdapter(playerAdapter);
        //
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Добавлем адаптер для нотификации об изменении списка
        PlayersStorage.getPlayersAdapters().add(playerAdapter);
        // Добавляем нотификатор для обработок изменения текущего игрока
        PlayersStorage.getCurrentPlayerChangeNotificators().add(this);
        //
        //
        //PlayersStorage.setCurrentPlayer(PlayersStorage.getPlayers().get(0));
        //
        // И надо не забыть дернуть нотификацию прям тут! А то мало ли - вдруг список менялся?
        playerAdapter.notifyDataSetChanged();
        changeOccurs();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Убираем адаптер из списка нотификаторов
        PlayersStorage.getPlayersAdapters().remove(playerAdapter);
        // Убираем нотификатор
        PlayersStorage.getCurrentPlayerChangeNotificators().remove(this);
    }

    /**
     * Обработка кнопок с +/-
     *
     * @param v View
     */
    public void plus_minusClickListener(View v) {
        if (v instanceof Button) {
            int diff;
            try {
                // Забавно, но +1 в int не конвертируется. Будем конвертировать по другому
                diff = Integer.parseInt(((Button) v).getText().toString().replace('+', ' ').trim());
            } catch (NumberFormatException e) {
                diff = 0;
            }
            if (diff != 0) {
                if (PlayersStorage.getCurrentPlayer() != null) {
                    PlayersStorage.addPlayerScore(PlayersStorage.getCurrentPlayer(), diff);
                }
            }
        }
    }

    /**
     * Обработка кнопок вперед/назад по игрокам
     *
     * @param v View
     */
    public void prev_nextClickListener(View v) {
        if ((PlayersStorage.getCurrentPlayer() != null) && (PlayersStorage.getPlayers().size() > 0)) {
            int currPos = PlayersStorage.getPlayers().indexOf(PlayersStorage.getCurrentPlayer());
            if (v.getId() == R.id.nextTurnBtn) {
                if (currPos == (PlayersStorage.getPlayers().size() - 1)) {
                    // Значит - это последний. Пееходим в самое начало
                    PlayersStorage.setCurrentPlayer(PlayersStorage.getPlayers().get(0));
                } else {
                    // Выбираем следующего
                    PlayersStorage.setCurrentPlayer(PlayersStorage.getPlayers().get(currPos + 1));
                }
            } else if (v.getId() == R.id.prevTurnBtn) {
                if (currPos == 0) {
                    // Значит это первый - надо переходить на последнего
                    PlayersStorage.setCurrentPlayer(PlayersStorage.getPlayers().get(PlayersStorage.getPlayers().size() - 1));
                } else {
                    // Выбираем предыдущего
                    PlayersStorage.setCurrentPlayer(PlayersStorage.getPlayers().get(currPos - 1));
                }
            }
        }
    }

    /**
     * Обработка кнопки удаления последнего хода
     *
     * @param v View
     */
    public void removeLastClickListener(View v) {
        //TODO Доделать!
    }

    public PlayerAdapter getPlayerAdapter() {
        return playerAdapter;
    }

    /**
     * Обработки изменения текущего игрока
     */
    public void changeOccurs() {
        // Тут у нас изменился текущий игрок
        // Поехаи по очереди проверять всяческие возможности
        // 1. Нет текущего игрока
        if (PlayersStorage.getCurrentPlayer() == null) {
            // Значит список совсем пустой - все дизейблим
            findViewById(R.id.prevTurnBtn).setEnabled(false);
            findViewById(R.id.nextTurnBtn).setEnabled(false);
            ((TextView) findViewById(R.id.currPlayerName)).setText("-");
        } else {
            boolean enabled = (PlayersStorage.getPlayers().size() > 1);
            findViewById(R.id.prevTurnBtn).setEnabled(enabled);
            findViewById(R.id.nextTurnBtn).setEnabled(enabled);
            TextView currPlayerTxt = (TextView) findViewById(R.id.currPlayerName);
            Player currPlayer = PlayersStorage.getCurrentPlayer();
            currPlayerTxt.setTextColor(currPlayer.getColor());
            currPlayerTxt.setText(currPlayer.getPlayerName());
        }
    }
}
