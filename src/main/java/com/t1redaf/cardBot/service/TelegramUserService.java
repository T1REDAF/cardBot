package com.t1redaf.cardBot.service;

import com.t1redaf.cardBot.repository.TelegramUserRepository;
import com.t1redaf.cardBot.repository.entity.TelegramUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;
    @Autowired
    public  TelegramUserService(TelegramUserRepository telegramUserRepository){
        this.telegramUserRepository = telegramUserRepository;
    }

    public void save(TelegramUser telegramUser){
        telegramUserRepository.save(telegramUser);
    }

    public Optional<TelegramUser> findByChatId(Long chatId){
        return telegramUserRepository.findById(chatId);
    }

}
