/*
 * Copyright (c) 2010. Сергей Лежнев
 */

package ru.librarian;

import java.util.EventListener;

/**
 * Простейший notificator listener.
 * Этот - для обработки изменения списка ходов
 */
public interface TurnsChangeNotification extends EventListener {

    public void turnsChangeOccurs();

}
