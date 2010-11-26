/*
 * Copyright (c) 2010. Сергей Лежнев
 */

package ru.librarian;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

public class HistoryActivity extends ListActivity implements AdapterView.OnItemLongClickListener, View.OnClickListener {

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
    protected void onRestart() {
        super.onRestart();
        historyAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        historyAdapter.notifyDataSetChanged();
    }    

    public void onClick(View view) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
