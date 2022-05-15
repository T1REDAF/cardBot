package com.t1redaf.cardBot.repository;

import com.t1redaf.cardBot.repository.entity.Card;
import com.t1redaf.cardBot.repository.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser,Long> {
}
