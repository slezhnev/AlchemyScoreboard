/*
 * Copyright (c) 2010. Сергей Лежнев
 */

package ru.librarian;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

public class PlayersActivity extends ListActivity implements AdapterView.OnItemLongClickListener {

    private PlayerAdapter playerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.players);
        //
        playerAdapter = new PlayerAdapter(this, R.layout.player_row, R.id.playerLVLeftText, R.id.playerLVRightText);
        setListAdapter(playerAdapter);
        //
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Добавлем адаптер для нотификации об изменении списка
        PlayersStorage.getPlayersAdapters().add(playerAdapter);
        // И надо не забыть дернуть нотификацию прям тут! А то мало ли - вдруг список менялся?
        playerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Убираем адаптер из списка нотификаторов
        PlayersStorage.getPlayersAdapters().remove(playerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Вот сюда должны приехать данные. Пока - пофигу что и как
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        if ((position > 0) && (position < PlayersStorage.getPlayers().size())) {
            Player player = PlayersStorage.getPlayers().get(position);
            Intent intent = new Intent().setClass(this, PlayerParamActivity.class);
            intent.putExtra("ru.librarian.playerName", player.getPlayerName());
            intent.putExtra("ru.librarian.playerColor", player.getColor());
            startActivityForResult(intent, 1);
        }
        return true;
    }
}
