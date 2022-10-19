package com.t1redaf.cardBot.configuration;

import com.t1redaf.cardBot.repository.entity.Card;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KeyboardFactory {

    public static ReplyKeyboard getStartKeyboard(){
        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        CommandName[] commandEnumArray = CommandName.values();
        if (commandEnumArray.length % 2 == 0){
            for (int i = 0; i < commandEnumArray.length; i+=2){
                keyboardRows.add(new KeyboardRow(List.of(
                        new KeyboardButton(commandEnumArray[i].getCommandName()),
                        new KeyboardButton(commandEnumArray[i+1].getCommandName())))
                );
            }
        }else{
            Arrays.stream(commandEnumArray).forEach((
                    command -> keyboardRows.add(new KeyboardRow(Collections.singleton(new KeyboardButton(command.getCommandName()))))));
        }

        replyKeyboard.setKeyboard(keyboardRows);
        replyKeyboard.setSelective(true);
        replyKeyboard.setResizeKeyboard(true);
        replyKeyboard.setOneTimeKeyboard(false);
        return replyKeyboard;
    }

    public static ReplyKeyboard getCardsMessageInlineKeyboard(List<Card> cards) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            cards.forEach(card -> {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(card.getName());
                button.setCallbackData(card.getName());
                rows.add(new ArrayList<>(Collections.singleton(button)));
            });
        inlineKeyboard.setKeyboard(rows);
        return inlineKeyboard;
    }

}
