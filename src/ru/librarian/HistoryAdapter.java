/*
 * Copyright (c) 2010. Сергей Лежнев
 */

package ru.librarian;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends ArrayAdapter<Turn> {

    private List<Turn> items = PlayersStorage.getTurns();
    private Context context;
    private int textViewResourceId;
    private int leftTextViewId;
    private int rightTextViewId;

    public HistoryAdapter(Context _context, int _textViewResourceId, int _leftTextViewId, int _rightTextViewId) {
        super(_context, _textViewResourceId, PlayersStorage.getTurns());
        this.context = _context;
        this.textViewResourceId = _textViewResourceId;
        this.leftTextViewId = _leftTextViewId;
        this.rightTextViewId = _rightTextViewId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(textViewResourceId, null);
        }
        Turn o = items.get(position);
        if (o != null) {
            TextView tt = (TextView) v.findViewById(leftTextViewId);
            TextView bt = (TextView) v.findViewById(rightTextViewId);
            if (tt != null) {
                //tt.setFocusableInTouchMode(true);
                tt.setTextColor(o.getPlayer().getColor());
                tt.setText(o.getPlayer().getPlayerName());
            }
            if (bt != null) {
                //bt.setFocusableInTouchMode(true);
                if (o.getScoreDiff() >= 0) {
                    bt.setTextColor(Color.parseColor("#99FF99"));
                } else {
                    bt.setTextColor(Color.parseColor("#FF9999"));
                }
                bt.setText("" + o.getScoreDiff());
            }
        }
        return v;
    }
}
