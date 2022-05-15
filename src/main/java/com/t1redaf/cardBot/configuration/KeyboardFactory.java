package com.t1redaf.cardBot.configuration;

import com.t1redaf.cardBot.repository.entity.Card;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public static ReplyKeyboard getCardsMessageInlineKeyboard(SendMessage msg, List<Card> cards) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
            for (Card card : cards){
                InlineKeyboardButton button = new InlineKeyboardButton(card.getName());
                button.setCallbackData(card.getName());
                keyboardButtons.add(button);
            }

        rowsInline.add(keyboardButtons);
        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }
}
