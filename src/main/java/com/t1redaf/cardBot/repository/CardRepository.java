package com.t1redaf.cardBot.repository;

import com.t1redaf.cardBot.repository.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card,String> {
    List<Card> findCardsByTelegramUser_ChatId(Long chatId);
}
