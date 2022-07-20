package com.t1redaf.cardBot.repository.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Entity
@Table(name = "tg_user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "cards")
public class TelegramUser {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @OneToMany(
            mappedBy = "telegramUser",
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    private List<Card> cards;

    public void addCard(Card card){
        if (isNull(cards)){
            cards = new ArrayList<>();
        }
        cards.add(card);
    }
}
