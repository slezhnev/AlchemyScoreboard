/*
 * Copyright (c) 2010. Сергей Лежнев
 */

package ru.librarian;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

public class HistoryActivity extends ListActivity implements AdapterView.OnItemLongClickListener, View.OnClickListener, TurnsChangeNotification {

    private HistoryAdapter historyAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        //
        historyAdapter = new HistoryAdapter(this, R.layout.turn_row, R.id.turnsLVLeftText, R.id.turnsLVRightText);
        setListAdapter(historyAdapter);
        //
        getListView().setOnItemLongClickListener(this);
        //
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Добавляем нотификатор
        PlayersStorage.getTurnsChnageNotificators().add(this);
        // Не забудем еще обновить данные
        historyAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Убираем нотификатор
        PlayersStorage.getTurnsChnageNotificators().remove(this);
    }

    public void onClick(View view) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Удалить выбранный ход?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PlayersStorage.removeTurn(HistoryActivity.this, PlayersStorage.getTurns().get(position));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
        return true;        
    }

    public void turnsChangeOccurs() {
        historyAdapter.notifyDataSetChanged();
    }
}
