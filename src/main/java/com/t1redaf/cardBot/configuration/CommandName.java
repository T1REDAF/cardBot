package com.t1redaf.cardBot.configuration;

public enum CommandName {

    START("/start"),ADD("/add"),HELP("/help"),GET_CARDS("/cards"),;

    private final String commandName;
    CommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
