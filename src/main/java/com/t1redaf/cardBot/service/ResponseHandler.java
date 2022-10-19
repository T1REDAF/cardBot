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
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Service
public class ResponseHandler {

    private final CardService cardService;
    private final TelegramUserService telegramUserService;

    @Autowired
    public ResponseHandler(CardService cardService, TelegramUserService telegramUserService) {
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
            context.bot().silent().send(String.format(
                    "У вас нет ни одного фото./n Вы можете добавить его с помощью команды - %s"
                    ,ADD.getCommandName()),context.chatId());
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
        context.bot().silent().send(String.format(
                "✨Доступные команды✨ /n%s - начать работать со мной /n%s - добавить фото /n%s - удалить фото /n%s - карты для общего использования /n%s - найти все ваши фото /n%s - помощь со мной",
                    START.getCommandName(), ADD.getCommandName(),
                    DELETE.getCommandName(),PUBLIC.getCommandName(),
                    GET_CARDS.getCommandName(), HELP.getCommandName()),
                context.chatId());
    }
    /*
    ***Start command handling***
     */
    public void startCommand(MessageContext context) {
        telegramUserService.findByChatId(context.chatId()).ifPresentOrElse(telegramUser ->{
//                context.bot().silent().send(
//                        String.format("Привет, %s\n\nЯ очень рад, что ты захотел поиграть со мной",context.user().getUserName()),
//                        context.chatId());
                SendMessage message = new SendMessage(context.chatId().toString(),"Привет, друг.\nЯ рад, что ты пользуешься мной :)");
                message.setReplyMarkup(KeyboardFactory.getStartKeyboard());
                message.enableMarkdown(true);
                try {
                    context.bot().execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                },
                () -> {
            TelegramUser newUser = new TelegramUser();
            newUser.setChatId(context.chatId());
            telegramUserService.save(newUser);
                    SendMessage message = new SendMessage(
                            context.chatId().toString(),
                            String.format("Привет, новенький\nЯ существую ради хранения фотографий\nВыбери команду %s чтобы понять какие команды я понимаю "
                                ,HELP.getCommandName())
                    );
                    message.setReplyMarkup(KeyboardFactory.getStartKeyboard());
                    message.enableMarkdown(true);
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
            context.bot().silent().send(String.format(
                    "У вас нет ни одного фото. /nВы можете добавить его с помощью команды - %s"
                    ,ADD.getCommandName()),context.chatId());
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
                .filter(card -> card.getName().equals(nameCardToDelete))
                .findAny()
                .orElseThrow(() -> new NotFoundException(String.format("Card '%s' was not found",nameCardToDelete)));

        cardService.deleteCardByFileId(cardToDelete.getFileId());
        bot.silent().send(String.format("'%s' карта была успешна добавлена!",cardToDelete.getName()),getChatId(upd));
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
