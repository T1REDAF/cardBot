package com.t1redaf.cardBot.configuration;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyboardFactory {

    public static ReplyKeyboard getStartKeyboard(){
        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.addAll(new ArrayList<>(Arrays.asList("/start","/help","/add","/get")));
        keyboardRows.add(row1);
        replyKeyboard.setKeyboard(keyboardRows);
        return replyKeyboard;
    }
}
