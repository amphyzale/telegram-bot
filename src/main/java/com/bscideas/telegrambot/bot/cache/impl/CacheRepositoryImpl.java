package com.bscideas.telegrambot.bot.cache.impl;

import com.bscideas.telegrambot.bot.cache.CacheRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.*;

@Component
public class CacheRepositoryImpl implements CacheRepository {

    private final Map<Long, List<Message>> registrationMap = new HashMap<>();
    private final Map<Long, List<Message>> createHomeworkMap = new HashMap<>();
    private final Map<Long, List<Message>> checkHomeworkMap = new HashMap<>();
    private final Map<Long, List<Message>> correctHomeworkMap = new HashMap<>();

    @Override
    public List<Message> getRegistrationMessages(Long id) {
        return registrationMap.get(id);
    }

    @Override
    public void putRegistrationMessage(Long id, Message message) {
        final List<Message> messages = registrationMap.get(id);
        if (messages == null) {
            registrationMap.put(id, new LinkedList<>());
            return;
        }
        messages.add(message);
    }

    @Override
    public List<Message> getCreateHomeworkMessages(Long id) {
        return createHomeworkMap.get(id);
    }

    @Override
    public void putCreateHomeworkMessage(Long id, Message message) {
        final List<Message> messages = createHomeworkMap.get(id);
        if (messages == null) {
            createHomeworkMap.put(id, new LinkedList<>());
            return;
        }
        messages.add(message);
    }

    @Override
    public List<Message> getCheckHomeworkMessages(Long id) {
        return checkHomeworkMap.get(id);
    }

    @Override
    public void putCheckHomeworkMessage(Long id, Message message) {
        final List<Message> messages = checkHomeworkMap.get(id);
        if (messages == null) {
            checkHomeworkMap.put(id, new LinkedList<>());
            return;
        }
        messages.add(message);
    }

    @Override
    public List<Message> getCorrectHomeworkMessages(Long id) {
        return correctHomeworkMap.get(id);
    }

    @Override
    public void putCorrectHomeworkMessage(Long id, Message message) {
        final List<Message> messages = correctHomeworkMap.get(id);
        if (messages == null) {
            correctHomeworkMap.put(id, new LinkedList<>());
            return;
        }
        messages.add(message);
    }

    @Override
    public void clearUserCaches(User user) {
        registrationMap.remove(user.getId());
        createHomeworkMap.remove(user.getId());
        checkHomeworkMap.remove(user.getId());
        correctHomeworkMap.remove(user.getId());
    }

    @Scheduled(cron = "0 */15 * ? * *")
    private void clearCaches() {
        registrationMap.clear();
        createHomeworkMap.clear();
        checkHomeworkMap.clear();
        correctHomeworkMap.clear();
    }
}
