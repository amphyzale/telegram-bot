package com.bscideas.telegrambot.bot.command;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Command {
    INFO("/info"),
    START("/start"),
    REGISTER("/register"),
    CREATE_HOMEWORK("/create_homework"),
    GET_HOMEWORK("/get_homework"),
    CHECK_HOMEWORK("/check_homework"),
    CORRECT_HOMEWORK("/correct_homework"),
    UNEXPECTED_COMMAND("Unexpected command"),
    ;

    private final String command;

    public String getCommand() {
        return command;
    }

    public static Command resolve(String name) {
        for (Command value : values()) {
            if (value.getCommand().equals(name)) {
                return value;
            }
        }
        return UNEXPECTED_COMMAND;
    }
}
