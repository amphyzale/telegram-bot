package com.bscideas.telegrambot.bot.cache;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public interface CacheRepository {
    List<Message> getRegistrationMessages(Long id);

    void putRegistrationMessage(Long id, Message message);

    List<Message> getCreateHomeworkMessages(Long id);

    void putCreateHomeworkMessage(Long id, Message message);

    List<Message> getCheckHomeworkMessages(Long id);

    void putCheckHomeworkMessage(Long id, Message message);

    List<Message> getCorrectHomeworkMessages(Long id);

    void putCorrectHomeworkMessage(Long id, Message message);

    void clearUserCaches(User user);
}
