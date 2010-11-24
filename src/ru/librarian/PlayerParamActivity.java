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
        // Если мы добавляем - то автоматически создадим имя...
        if (getIntent().getStringExtra("ru.librarian.playerName").length() == 0) {
            ((TextView) findViewById(R.id.playerNameEdt)).setText("Игрок" + (PlayersStorage.getPlayers().size() + 1));
        } else {
            ((TextView) findViewById(R.id.playerNameEdt)).setText(getIntent().getStringExtra("ru.librarian.playerName"));
        }
    }

    public void colorSelectionClick(View v) {
        new ColorPickerDialog(this, this, Color.parseColor("#FFFFFF")).show();
    }

    public void buttonClick(View v) {
        if (v.getId() == R.id.playerParamOk) {
            String newName = ((TextView) findViewById(R.id.playerNameEdt)).getText().toString();
            // Проверяем на неправильность имени
            if (newName.trim().length() == 0) {
                // Лаемся и уходим
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Задайте имя игрока")
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
            // Проверим - а нет ли уже игрока с таким именем?
            // Проверяем ТОЛЬКО в том случае, если имя было изменено
            // Если extra playerName равна пустой строке - это признак того, что
            // игрок добавлялся
            if ((getIntent().getStringExtra("ru.librarian.playerName").length() == 0) ||
                    (!newName.equals(getIntent().getStringExtra("ru.librarian.playerName")))) {
                for (Player player : PlayersStorage.getPlayers()) {
                    if (newName.equals(player.getPlayerName())) {
                        // Нашли
                        // Ругнемся - и пошли нафиг
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Игрок с таким именем уже существует. Задайте другое имя")
                                .setCancelable(true)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                        return;
                    }
                }
            }
            // А вот тут прям сразу все и проапдейтим
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
