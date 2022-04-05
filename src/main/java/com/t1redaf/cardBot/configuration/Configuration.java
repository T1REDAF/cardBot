package com.t1redaf.cardBot.configuration;

import com.t1redaf.cardBot.bot.CardBot;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@org.springframework.context.annotation.Configuration
public class Configuration {
    private final CardBot cardBot;

    public Configuration(CardBot cardBot) {
        this.cardBot = cardBot;
    }

    @Bean
    public TelegramBotsApi api(){
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(cardBot);
            return telegramBotsApi;
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
