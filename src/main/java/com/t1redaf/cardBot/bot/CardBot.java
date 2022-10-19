package com.t1redaf.cardBot.bot;

import com.t1redaf.cardBot.repository.entity.Card;
import com.t1redaf.cardBot.repository.entity.TelegramUser;
import com.t1redaf.cardBot.service.CardService;
import com.t1redaf.cardBot.service.ResponseHandler;
import com.t1redaf.cardBot.service.TelegramUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.objects.ReplyFlow;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.ws.rs.NotFoundException;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static com.t1redaf.cardBot.configuration.CommandName.ADD;
import static org.telegram.abilitybots.api.objects.Flag.*;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Component
public class CardBot extends AbilityBot {


    private final TelegramUserService telegramUserService;
    private final CardService cardService;
    private final ResponseHandler responseHandler;

    /*
    ***Start command***
    */
    public Ability startCommand(){
        return Ability
                .builder()
                .name("start")
                .info("User registration handling")
                .input(0)
                .locality(USER)
                .privacy(PUBLIC)
                .action(responseHandler::startCommand)
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
                .action(responseHandler::helpCommand)
                .build();
    }

    /*
    ***Add card***
    */
    public ReplyFlow addCard() {
        Reply addCardReply = Reply.of((bot, update) ->{
                    String fileId = Objects.requireNonNull(update.getMessage().getPhoto().
                            stream().max(Comparator.comparing(PhotoSize::getFileSize))
                            .orElse(null)).getFileId();
                    GetFile getFile = new GetFile(fileId);
                    try {
                        File fileTg = execute(getFile);
                        Card newCard = new Card();
                        TelegramUser user = telegramUserService.findByChatId(update.getMessage().getChatId()).orElseThrow(
                                () ->{
                                    throw new NotFoundException("Вы не были найдены в базе данных");
                                });
                        newCard.setFileId(fileTg.getFileId());
                        newCard.setTelegramUser(user);
                        newCard.setName(update.getMessage().getCaption());
                        newCard.setOpen(false);
                        user.addCard(newCard);
                        cardService.save(newCard);
                        silent.send("Фото было успешно добавлено в вашу колекцию\n\nСпасибо, что пользуетесь мной:)",
                                update.getMessage().getChatId());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            , PHOTO, CAPTION);
        return ReplyFlow.builder(db)
                .action((bot,update) ->
                        silent.send("Пожалуйста прикрепите фото и его название(в заголовке)",
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
                .locality(USER)
                .privacy(PUBLIC)
                .action(responseHandler::getListOfCards)
                .build();
    }
    /*
    ***Get card***
    */
    public Reply replyToButtons() {
        BiConsumer<BaseAbilityBot,Update> action = responseHandler::replyToButtons;
        return Reply.of(action, CALLBACK_QUERY,
                (upd)->upd.getCallbackQuery().getMessage().getText().contains("Выберите фото"));
    }

    /*
    ***Get all cards to delete***
     */
    public Ability deleteCards(){
        return Ability
                .builder()
                .name("delete")
                .info("Delete card")
                .input(0)
                .locality(USER)
                .privacy(PUBLIC)
                .action(responseHandler::deleteCommand)
                .build();
    }
    /*
    ***Handling delete query***
     */
    public Reply replyToDeleteButtons(){
        BiConsumer<BaseAbilityBot,Update> action = responseHandler::replyToDeleteButtons;
        return Reply.of(action,CALLBACK_QUERY,
                (upd)->upd.getCallbackQuery().getMessage().getText().contains("Выберите фото для удаления"));
    }
    /*
    ***Public method
     */
    public Ability publicCommand(){
        return Ability
                .builder()
                .name("public")
                .info("Get public cards")
                .input(0)
                .locality(USER)
                .privacy(PUBLIC)
                .action(responseHandler::publicCommand)
                .build();
    }
    public Predicate<Update> hasText(String msg){
        return update -> update.getMessage().getText().toLowerCase().contains(msg);
    }

    protected CardBot(@Value("${bot.token}") String botToken,
                      @Value("${bot.name}") String botName,
                      TelegramUserService telegramUserService,
                      CardService cardService,
                      ResponseHandler responseHandler)
    {
        super(botToken, botName);
        this.telegramUserService = telegramUserService;
        this.cardService = cardService;
        this.responseHandler = responseHandler;
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

