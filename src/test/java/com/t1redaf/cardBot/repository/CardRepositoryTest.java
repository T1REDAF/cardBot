package com.t1redaf.cardBot.repository;

import com.t1redaf.cardBot.repository.entity.Card;
import com.t1redaf.cardBot.repository.entity.TelegramUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CardRepositoryTest {

    @Autowired
    private CardRepository underTest;

    @Autowired
    private TelegramUserRepository telegramUserRepository;

    private TelegramUser tgUser;
    private final Long tgUserChatId = 123L;
    private Card card;
    private final String cardFileId = "12345";
    @BeforeEach
    void setUp() {
        tgUser = TelegramUser
                .builder()
                .chatId(tgUserChatId)
                .build();
        card = Card
                .builder()
                .telegramUser(tgUser)
                .fileId(cardFileId)
                .name("card")
                .build();
         tgUser.setCards(Collections.singletonList(card));
        telegramUserRepository.save(tgUser);
    }

    @Test
    public void shouldNotFindCard(){

        //given

        //when
        List<Card> assert1 = underTest.findCardsByTelegramUser_ChatId(123L);
        //then
        assertThat(assert1).isEmpty();
    }

    @Test
    public void shouldFindSameCard(){

        //given

        //when
        List<Card> sameCards =  underTest.findCardsByTelegramUser_ChatId(tgUserChatId);
        //then
        assertThat(sameCards)
                .isNotEmpty()
                .hasSize(1)
                .contains(card);
    }

    }