/*
 * Copyright (c) 2010. Сергей Лежнев
 */

package ru.librarian;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PlayerParamActivity extends Activity implements ColorPickerDialog.OnColorChangedListener {

    private View colorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_param);
        //
        colorView = findViewById(R.id.playerColorView);
        colorView.setBackgroundColor(getIntent().getIntExtra("ru.librarian.playerColor", Color.parseColor("#FFFFFF")));
        //
        ((TextView)findViewById(R.id.playerNameEdt)).setText(getIntent().getStringExtra("ru.librarian.playerName"));
        // Пробуем сделать красную кнопку...
        findViewById(R.id.playerParamDelete).getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
    }

    public void colorSelectionClick(View v) {
        new ColorPickerDialog(this, this, Color.parseColor("#FFFFFF")).show();
    }

    public void colorChanged(int color) {
        colorView.setBackgroundColor(color);                       
    }
}
