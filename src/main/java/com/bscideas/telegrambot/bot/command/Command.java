package com.bscideas.telegrambot.bot.command;

public enum Command {
    INFO("/info"),
    START("/start"),
    REGISTER("/register"),
    CREATE_HOMEWORK("/create_homework"),
    GET_HOMEWORK("/get_homework"),
    CHECK_HOMEWORK("/check_homework"),
    DEMO("/demo");


    private final String command;

    Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
