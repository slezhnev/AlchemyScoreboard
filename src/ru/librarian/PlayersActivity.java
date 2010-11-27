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
import android.view.*;
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
        //getListView().setOnItemLongClickListener(this);        
        //
        // Создаем диалог
        actionDialog = new Dialog(this);
        actionDialog.setContentView(R.layout.player_action);
        actionDialog.setTitle("");
        actionDialog.findViewById(R.id.playerActionDelete).getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        actionDialog.findViewById(R.id.playerActionMoveUp).setOnClickListener(this);
        actionDialog.findViewById(R.id.playerActionMoveDown).setOnClickListener(this);
        actionDialog.findViewById(R.id.playerActionEdit).setOnClickListener(this);
        actionDialog.findViewById(R.id.playerActionRecalc).setOnClickListener(this);
        actionDialog.findViewById(R.id.playerActionDelete).setOnClickListener(this);
        actionDialog.setOwnerActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Добавлем адаптер для нотификации об изменении списка
        PlayersStorage.getPlayersAdapters().add(playerAdapter);
        // И надо не забыть дернуть нотификацию прям тут! А то мало ли - вдруг список менялся?
        doDataNotification();
    }

    private void doDataNotification() {
        playerAdapter.notifyDataSetChanged();
        registerForContextMenu(getListView());
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Убираем адаптер из списка нотификаторов
        PlayersStorage.getPlayersAdapters().remove(playerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.players_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.playerActionAddMI) {
            // Добавляем...
            Intent intent = new Intent().setClass(this, PlayerParamActivity.class);
            intent.putExtra("ru.librarian.playerName", "");
            intent.putExtra("ru.librarian.playerColor", Color.parseColor("#FFFFFF"));
            startActivity(intent);
        } else if (item.getItemId() == R.id.playerActionStartNewGameMI) {
            PlayersStorage.startNewGame(this, getListView().getContext());
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo mInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.players_context_menu, menu);
        operationalPlayer = PlayersStorage.getPlayers().get(mInfo.position);
        if (mInfo.position == 0) {
            // Hardcoded, конечно, по пока разбираться не буду
            menu.getItem(0).setEnabled(false);
        } else {
            menu.getItem(0).setEnabled(true);
        }
        if (mInfo.position == (PlayersStorage.getPlayers().size() - 1)) {
            menu.getItem(1).setEnabled(false);
        } else {
            menu.getItem(2).setEnabled(true);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.playerActionEditMI) {
            Intent intent = new Intent().setClass(this, PlayerParamActivity.class);
            intent.putExtra("ru.librarian.playerName", operationalPlayer.getPlayerName());
            intent.putExtra("ru.librarian.playerColor", operationalPlayer.getColor());
            startActivity(intent);
            actionDialog.dismiss();
        } else if (item.getItemId() == R.id.playerActionDeleteMI) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Вы действительно хотите удалить текущего игрока?")
                    .setCancelable(false)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PlayersStorage.removePlayer(PlayersActivity.this, PlayersActivity.this.operationalPlayer.getPlayerName());
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        } else if (item.getItemId() == R.id.playerActionMoveDownMI) {
            PlayersStorage.movePlayerDown(this, operationalPlayer);
        } else if (item.getItemId() == R.id.playerActionMoveUpMI) {
            PlayersStorage.movePlayerUp(this, operationalPlayer);
        } else if (item.getItemId() == R.id.playerActionRecalcMI) {
            // Пойдем пересчитаем его счет - мало ли, вдруг чего взглючило
            PlayersStorage.recalcPlayerScore(this, operationalPlayer);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Вот сюда должны приехать данные. Пока - пофигу что и как
        //super.onActivityResult(requestCode, resultCode, data);
        // Теперь еще проверим чего там в extras вернулось
        if (data.hasExtra("ru.librarian.player_mustRemove")) {
            PlayersStorage.removePlayer(this, data.getStringExtra("ru.librarian.playerName"));
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
            if (position == (PlayersStorage.getPlayers().size() - 1)) {
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
                            PlayersStorage.removePlayer(PlayersActivity.this, PlayersActivity.this.operationalPlayer.getPlayerName());
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
            PlayersStorage.movePlayerDown(this, operationalPlayer);
            actionDialog.dismiss();
        } else if (v.getId() == R.id.playerActionMoveUp) {
            PlayersStorage.movePlayerUp(this, operationalPlayer);
            actionDialog.dismiss();
            /*} else if (v.getId() == R.id.playersAddPlayer) {
            // Добавляем...
            Intent intent = new Intent().setClass(this, PlayerParamActivity.class);
            intent.putExtra("ru.librarian.playerName", "");
            intent.putExtra("ru.librarian.playerColor", Color.parseColor("#FFFFFF"));
            startActivity(intent);*/
        } else if (v.getId() == R.id.playerActionRecalc) {
            // Пойдем пересчитаем его счет - мало ли, вдруг чего взглючило
            int score = 0;
            for (Turn turn : PlayersStorage.getTurns()) {
                if (operationalPlayer == turn.getPlayer()) {
                    score = score + turn.getScoreDiff();
                }
            }
            PlayersStorage.addPlayerScore(this, operationalPlayer, score - operationalPlayer.getScore());
        }
    }
}
