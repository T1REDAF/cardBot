DROP TABLE IF EXISTS tg_user;
DROP TABLE IF EXISTS card;

CREATE TABLE tg_user(
    chat_id BIGINT,
    PRIMARY KEY (chat_id)
);

CREATE TABLE card(
    file_id VARCHAR(50),
    `name` VARCHAR(30),
    user_chat_id BIGINT,
    foreign key (user_chat_id) references tg_user(chat_id),
    PRIMARY KEY (file_id)
);