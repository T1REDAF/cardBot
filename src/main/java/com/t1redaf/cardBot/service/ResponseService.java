package com.t1redaf.cardBot.service;

import com.t1redaf.cardBot.repository.entity.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.ws.rs.NotFoundException;
import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Service
public class ResponseService {

    private final CardService cardService;
    @Autowired
    public ResponseService(CardService cardService) {
        this.cardService = cardService;
    }

    public void replyToButtons(BaseAbilityBot bot, Update upd) {

        String cardName = upd.getCallbackQuery().getData();
        Card card = cardService.getCardsByChatId(getChatId(upd))
                .stream().filter(s -> s.getName().trim().equals(cardName.trim())).findAny().orElseThrow(() -> {
                    throw new NotFoundException("Dont found your card\nSomething have gone wrong");
                });
//        GetFile getFile = new GetFile(card.getFileId());
        try {
//            File telegramFile = bot.execute(getFile);
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(getChatId(upd).toString());
            sendPhoto.setPhoto(new InputFile(card.getFileId()));
            bot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
