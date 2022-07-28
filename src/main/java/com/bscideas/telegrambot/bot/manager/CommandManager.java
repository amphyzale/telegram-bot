package com.bscideas.telegrambot.bot.manager;

import com.bscideas.telegrambot.bot.command.Command;

public interface CommandManager {
    boolean supports(Command command);
    ResponseData handleCommand(RequestData requestData);
}
