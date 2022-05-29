package com.t1redaf.cardBot.bot;

import com.t1redaf.cardBot.configuration.KeyboardFactory;
import com.t1redaf.cardBot.repository.entity.Card;
import com.t1redaf.cardBot.repository.entity.TelegramUser;
import com.t1redaf.cardBot.service.CardService;
import com.t1redaf.cardBot.service.ResponseService;
import com.t1redaf.cardBot.service.TelegramUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.t1redaf.cardBot.configuration.CommandName.*;
import static org.telegram.abilitybots.api.objects.Flag.*;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class CardBot extends AbilityBot {


    private final TelegramUserService telegramUserService;
    private final CardService cardService;
    private final ResponseService responseService;

    /*
    ***Start command***
    */
    public Ability startCommand(){
        return Ability
                .builder()
                .name("start")
                .info("User registration handling")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(responseService::startCommand)
                .build();
    }

    /*
    ***Help command***
    */
    public Ability helpCommand() {
        return Ability
                .builder()
                .name("help")
                .info("information about bot commands")
                .input(0)
                .locality(USER)
                .privacy(PUBLIC)
                .action(responseService::helpCommand)
                .build();
    }

    /*
    ***Add card***
    */
    public ReplyFlow addCard() {
        Reply addCardReply = Reply.of((bot, update) ->{
                    PhotoSize photo = update.getMessage().getPhoto().get(3);
                    GetFile getFile = new GetFile(photo.getFileId());
                    try {
                        File fileTg = execute(getFile);
//                        String filePath = fileTg.getFilePath();
//                        java.io.File file = downloadFile(filePath,
//                                new java.io.File("src/main/resources/user cards/"+photo.getFileId()+".jpg"));
                        Card newCard = new Card();
                        TelegramUser user = telegramUserService.findByChatId(update.getMessage().getChatId()).orElseThrow(
                                () ->{
                                    throw new NotFoundException("User not found in db");
                                });
                        newCard.setFileId(fileTg.getFileId());
                        newCard.setTelegramUser(user);
                        newCard.setName(update.getMessage().getCaption());
                        user.addCard(newCard);
                        cardService.save(newCard);
                        silent.send("Card successfully have added to your collection\n\nThank you for using me:)",
                                update.getMessage().getChatId());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            , PHOTO, CAPTION);
        return ReplyFlow.builder(db)
                .action((bot,update) ->
                        silent.send("Please attach a photo of card and his name in one message(necessarily)",
                                update.getMessage().getChatId()))
                .onlyIf(hasText(ADD.getCommandName()))
                .next(addCardReply)
                .build();

    }

    /*
    ***Get list of cards
    */
    public Ability getListOfCards(){
        return  Ability
                .builder()
                .name("cards")
                .info("Get all of cards")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(responseService::getListOfCards)
                .build();
    }
    /*
    ***Get card***
    */
    public Reply replyToButtons() {
        BiConsumer<BaseAbilityBot,Update> action = responseService::replyToButtons;
        return Reply.of(action, CALLBACK_QUERY);
    }

    /*
    ***Delete card
     */
    //TODO MAKE DELETE CARD METHOD
    public Predicate<Update> hasText(String msg){
        return update -> update.getMessage().getText().split(" ")[0].equalsIgnoreCase(msg);
    }

    protected CardBot(@Value("${bot.token}") String botToken, @Value("${bot.name}") String botName, TelegramUserService telegramUserService, CardService cardService, ResponseService responseService) {
        //TODO ADD BOT TOKEN, BOT NAME TO ENVIRONMENT VARIABLES
        super(botToken, botName);
        this.telegramUserService = telegramUserService;
        this.cardService = cardService;
        this.responseService = responseService;
    }

    @Override
    public long creatorId() {
        return 1246010301;
    }

    @Override
    public String getBotToken() {
        return super.getBotToken();
    }


}

