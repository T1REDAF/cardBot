package com.t1redaf.cardBot.repository.entity;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "card")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "telegramUser")
public class Card {
    @Id
    @Column(name = "file_id")
    private String fileId;

    @Column(name = "name")
    private String name;

    @Column(name = "open")
    private Boolean open;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_chat_id")
    private  TelegramUser telegramUser;

}