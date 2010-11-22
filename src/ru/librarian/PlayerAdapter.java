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

public class PlayerAdapter extends ArrayAdapter<Player> {

    private ArrayList<Player> items = PlayersStorage.getPlayers();
    private Context context;
    private int textViewResourceId;
    private int leftTextViewId;
    private int rightTextViewId;

    public PlayerAdapter(Context _context, int _textViewResourceId, int _leftTextViewId, int _rightTextViewId) {
        super(_context, _textViewResourceId, PlayersStorage.getPlayers());
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
        Player o = items.get(position);
        if (o != null) {
            TextView tt = (TextView) v.findViewById(leftTextViewId);
            TextView bt = (TextView) v.findViewById(rightTextViewId);
            if (tt != null) {
                //tt.setFocusableInTouchMode(true);
                tt.setTextColor(o.getColor());
                tt.setText(o.getPlayerName());
            }
            if (bt != null) {
                //bt.setFocusableInTouchMode(true);
                if (o.getScore() >= 0) {
                    bt.setTextColor(Color.parseColor("#99FF99"));
                } else {
                    bt.setTextColor(Color.parseColor("#FF9999"));
                }
                bt.setText("" + o.getScore());
            }
        }
        return v;
    }
}
