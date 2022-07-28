package com.bscideas.telegrambot.bot.manager;

import com.bscideas.telegrambot.bot.command.Command;
import lombok.Builder;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Getter
@Builder
public final class RequestData {
    private final Command command;
    private final User user;
    private final Message message;
}
