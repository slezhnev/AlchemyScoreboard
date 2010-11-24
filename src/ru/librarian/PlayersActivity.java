/*
 * Copyright (c) 2010. Сергей Лежнев
 */

package ru.librarian;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

public class PlayersActivity extends ListActivity implements AdapterView.OnItemLongClickListener, View.OnClickListener {

    private PlayerAdapter playerAdapter;
    private Player operationalPlayer;
    private Dialog actionDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.players);
        //
        playerAdapter = new PlayerAdapter(this, R.layout.player_row, R.id.playerLVLeftText, R.id.playerLVRightText);
        setListAdapter(playerAdapter);
        //
        getListView().setOnItemLongClickListener(this);
        //
        // Создаем диалог
        actionDialog = new Dialog(this);
        actionDialog.setContentView(R.layout.player_action);
        actionDialog.setTitle("");
        actionDialog.findViewById(R.id.playerActionDelete).getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        actionDialog.findViewById(R.id.playerActionMoveUp).setOnClickListener(this);
        actionDialog.findViewById(R.id.playerActionMoveDown).setOnClickListener(this);
        actionDialog.findViewById(R.id.playerActionEdit).setOnClickListener(this);
        actionDialog.findViewById(R.id.playerActionDelete).setOnClickListener(this);        
        actionDialog.setOwnerActivity(this);
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
        //super.onActivityResult(requestCode, resultCode, data);
        // Теперь еще проверим чего там в extras вернулось
        if (data.hasExtra("ru.librarian.player_mustRemove")) {
            PlayersStorage.removePlayer(data.getStringExtra("ru.librarian.playerName"));
        } else if ((data.hasExtra("ru.librarian.playerName_return")) && (data.getStringExtra("ru.librarian.playerName_return") != null)) {
            // А вот и оно. Меняем у него имя и цвет
            operationalPlayer.setPlayerName(data.getStringExtra("ru.librarian.playerName_return"));
            operationalPlayer.setColor(data.getIntExtra("ru.librarian.playerColor_return", Color.parseColor("#FFFFFF")));
            // Fire in the hole!
            PlayersStorage.playersChanged();
        }
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        if ((position >= 0) && (position < PlayersStorage.getPlayers().size())) {
            operationalPlayer = PlayersStorage.getPlayers().get(position);
            actionDialog.setTitle(operationalPlayer.getPlayerName());
            if (position == 0) {
                actionDialog.findViewById(R.id.playerActionMoveUp).setEnabled(false);
            } else {
                actionDialog.findViewById(R.id.playerActionMoveUp).setEnabled(true);
            }
            if (position == (PlayersStorage.getPlayers().size()-1)) {
                actionDialog.findViewById(R.id.playerActionMoveDown).setEnabled(false);
            } else {
                actionDialog.findViewById(R.id.playerActionMoveDown).setEnabled(true);
            }
            actionDialog.show();
            //
        } else {
            operationalPlayer = null;
        }
        return true;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.playerActionEdit) {
            Intent intent = new Intent().setClass(this, PlayerParamActivity.class);
            intent.putExtra("ru.librarian.playerName", operationalPlayer.getPlayerName());
            intent.putExtra("ru.librarian.playerColor", operationalPlayer.getColor());
            startActivity(intent);
            actionDialog.dismiss();
        } else if (v.getId() == R.id.playerActionDelete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Вы действительно хотите удалить текущего игрока?")
                    .setCancelable(false)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PlayersStorage.removePlayer(PlayersActivity.this.operationalPlayer.getPlayerName());
                            actionDialog.dismiss();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            actionDialog.dismiss();
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        } else if (v.getId() == R.id.playerActionMoveDown) {
            PlayersStorage.movePlayerDown(operationalPlayer);
            actionDialog.dismiss();
        } else if (v.getId() == R.id.playerActionMoveUp) {
            PlayersStorage.movePlayerUp(operationalPlayer);
            actionDialog.dismiss();
        } else if (v.getId() == R.id.playersAddPlayer) {
            // Добавляем...
            Intent intent = new Intent().setClass(this, PlayerParamActivity.class);
            intent.putExtra("ru.librarian.playerName", "");
            intent.putExtra("ru.librarian.playerColor", Color.parseColor("#FFFFFF"));
            startActivity(intent);
        }
    }
}
