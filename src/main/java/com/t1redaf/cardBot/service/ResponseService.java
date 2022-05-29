package com.t1redaf.cardBot.service;

import com.t1redaf.cardBot.configuration.KeyboardFactory;
import com.t1redaf.cardBot.repository.entity.Card;
import com.t1redaf.cardBot.repository.entity.TelegramUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;

import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.ws.rs.NotFoundException;

import java.util.List;

import static com.t1redaf.cardBot.configuration.CommandName.*;
import static com.t1redaf.cardBot.configuration.CommandName.HELP;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Service
public class ResponseService {

    private final CardService cardService;
    private final TelegramUserService telegramUserService;
    @Autowired
    public ResponseService(CardService cardService, TelegramUserService telegramUserService) {
        this.cardService = cardService;
        this.telegramUserService = telegramUserService;
    }
/*
***Handling callback data from card buttons***
 */
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
/*
***Get list of user's cards and make buttons***
 */
    public void getListOfCards(MessageContext context) {
        List<Card> cards = cardService.getCardsByChatId(context.chatId());
        if (cards.isEmpty()) {
            context.bot().silent().send("""
                            You dont have any cards.
                            Please add photo of card via command %s"""
                    .formatted(ADD.getCommandName()),context.chatId());
            return;
        }

        SendMessage msg = new SendMessage(context.chatId().toString(), "Choose your card");
        msg.setReplyMarkup(
                KeyboardFactory.getCardsMessageInlineKeyboard(cards));
        try {
            context.bot().execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
/*
***Help command response***
 */
    public void helpCommand(MessageContext context) {
        context.bot().silent().send("""
                ✨Available commands✨

                %s - start working with me
                %s - add card
                %s - get list of your cards
                %s - get help at work with me""".formatted(
                START.getCommandName(), ADD.getCommandName(),
                GET_CARDS.getCommandName(),HELP.getCommandName()),
                context.chatId());
    }
/*
***Start command handling***
 */
    public void startCommand(MessageContext context) {
        telegramUserService.findByChatId(context.chatId()).ifPresentOrElse(telegramUser ->
                context.bot().silent().send("Hi, dear %s\n\nI am very glad to see you again".formatted(context.user().getUserName()), context.chatId()),
                () -> {
            TelegramUser newUser = new TelegramUser();
            newUser.setChatId(context.chatId());
            telegramUserService.save(newUser);
            SendMessage message = new SendMessage(context.chatId().toString(),"""
                            Hi, you are new in this bot
                            This bot can store photo of cards and give it to you in one click
                            Also users can make their cards 'public' to public usage
                            Command '/help' can give you commands
                            I hope you enjoy this bot!""");
            message.setReplyMarkup(KeyboardFactory.getStartKeyboard());
            try {
                context.bot().execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }
}
