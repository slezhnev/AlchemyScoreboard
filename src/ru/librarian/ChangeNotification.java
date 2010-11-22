/*
 * Copyright (c) 2010. Сергей Лежнев
 */

package ru.librarian;

import java.util.EventListener;

/**
 * Простейший notificator listener
 */
public interface ChangeNotification extends EventListener {

    public void changeOccurs();

}
