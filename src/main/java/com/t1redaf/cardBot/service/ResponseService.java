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
import org.telegram.telegrambots.meta.api.objects.User;
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
                    throw new NotFoundException("Ваше фото было не найдено\nЧто-то пошло не так D:");
                });
        try {
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
                            У вас нет ни одного фото.
                            Вы можете добавить его с помощью команды - %s"""
                    .formatted(ADD.getCommandName()),context.chatId());
            return;
        }

        SendMessage msg = new SendMessage(context.chatId().toString(), "Выберите фото");
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
                ✨Доступные команды✨

                %s - начать работать со мной
                %s - добавить фото
                %s - удалить фото
                %s - карты для общего использования
                %s - найти все ваши фото
                %s - помощь со мной""".formatted(
                    START.getCommandName(), ADD.getCommandName(),
                    DELETE.getCommandName(),PUBLIC.getCommandName(),
                    GET_CARDS.getCommandName(), HELP.getCommandName()),
                context.chatId());
    }
/*
***Start command handling***
 */
    public void startCommand(MessageContext context) {
        telegramUserService.findByChatId(context.chatId()).ifPresentOrElse(telegramUser ->
                context.bot().silent().send("Привет, дорогой %s\n\nЯ очень рад, что ты захотел поиграть со мной".formatted(context.user().getUserName()), context.chatId()),
                () -> {
            TelegramUser newUser = new TelegramUser();
            newUser.setChatId(context.chatId());
            telegramUserService.save(newUser);
                    SendMessage message = new SendMessage(context.chatId().toString(),"""
                            Привет, новенький
                            Я существую ради хранения фотографий
                            Выбери команду %s чтобы понять какие команды я понимаю
                            """.formatted(HELP.getCommandName()));
                    message.setReplyMarkup(KeyboardFactory.getStartKeyboard());
                    try {
                        context.bot().execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteCommand(MessageContext context) {
        List<Card> cards = cardService.getCardsByChatId(context.chatId());
        if (cards.isEmpty())
            context.bot().silent().send("""
                            У вас нет ни одного фото.
                            Вы можете добавить его с помощью команды - %s"""
                    .formatted(ADD.getCommandName()),context.chatId());
        SendMessage msg = new SendMessage(context.chatId().toString(), "Выберите фото для удаления");
        msg.setReplyMarkup(
                KeyboardFactory.getCardsMessageInlineKeyboard(cards));
        try {
            context.bot().execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void replyToDeleteButtons(BaseAbilityBot bot, Update upd) {
        String nameCardToDelete = upd.getCallbackQuery().getData();
        Card cardToDelete = cardService.getCardsByChatId(getChatId(upd)).stream()
                .filter(card -> card.getName().equals(nameCardToDelete)).findAny().get();
        cardService.deleteCardByFileId(cardToDelete.getFileId());
        bot.silent().send("'%s' карта была успешна добавлена!".formatted(cardToDelete.getName()),getChatId(upd));
    }

    public void publicCommand(MessageContext context) {
        List<Card> adminCards = cardService.getCardsByChatId(1246010301L);
        SendMessage msg = new SendMessage(context.chatId().toString(), "Выберите фото");
        msg.setReplyMarkup(
                KeyboardFactory.getCardsMessageInlineKeyboard(adminCards));
        try {
            context.bot().execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
