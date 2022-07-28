package com.bscideas.telegrambot.bot.manager;

public interface CommandProcessor {
    ResponseData process(RequestData requestData);
}
