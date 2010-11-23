/*
 * Copyright (c) 2010. Сергей Лежнев
 */

package ru.librarian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PlayerParamActivity extends Activity implements ColorPickerDialog.OnColorChangedListener {

    private View colorView;
    private int selectedColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_param);
        //
        colorView = findViewById(R.id.playerColorView);
        selectedColor = getIntent().getIntExtra("ru.librarian.playerColor", Color.parseColor("#FFFFFF"));
        colorView.setBackgroundColor(getIntent().getIntExtra("ru.librarian.playerColor", selectedColor));
        //
        ((TextView) findViewById(R.id.playerNameEdt)).setText(getIntent().getStringExtra("ru.librarian.playerName"));
    }

    public void colorSelectionClick(View v) {
        new ColorPickerDialog(this, this, Color.parseColor("#FFFFFF")).show();
    }

    public void buttonClick(View v) {
        if (v.getId() == R.id.playerParamOk) {
            // А вот тут прям сразу все и проапдейтим внатуре
            PlayersStorage.setPlayerParams(getIntent().getStringExtra("ru.librarian.playerName"),
                    ((TextView) findViewById(R.id.playerNameEdt)).getText().toString(),
                    selectedColor);
            finish();
        } else if (v.getId() == R.id.playerParamCancel) {
            finish();
        }
    }

    public void colorChanged(int color) {
        colorView.setBackgroundColor(color);
        selectedColor = color;
    }
}
