package com.t1redaf.cardBot.service;

import com.t1redaf.cardBot.repository.CardRepository;
import com.t1redaf.cardBot.repository.entity.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CardService {

    private final CardRepository cardRepository;
    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void save(Card card){
        cardRepository.save(card);
    }

    public void deleteCardByFileId(String id){
         cardRepository.deleteById(id);
    }

    public List<Card> getCardsByChatId(Long chatId){
        return cardRepository.findCardsByTelegramUser_ChatId(chatId);
    }
}
