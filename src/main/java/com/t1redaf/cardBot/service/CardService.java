package com.t1redaf.cardBot.service;

import com.t1redaf.cardBot.repository.CardRepository;
import com.t1redaf.cardBot.repository.entity.Card;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
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

    public Optional<Card> getCardById(String id){
        return cardRepository.findById(id);
    }

    public void deleteCardById(String id){
         cardRepository.deleteById(id);
    }

    public List<Card> getAllCards(){
        return cardRepository.findAll();
    }
}
