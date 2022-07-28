package com.bscideas.telegrambot.bot.manager;

import lombok.Builder;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Getter
@Builder
public final class ResponseData {
    private final String chatId;
    private final String message;
    private final SendMessage response;

    public boolean hasNotification() {
        return chatId != null && !chatId.isEmpty()
                && message != null && !message.isEmpty();
    }
}
