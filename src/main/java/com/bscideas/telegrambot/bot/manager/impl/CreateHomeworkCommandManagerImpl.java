package com.bscideas.telegrambot.bot.manager.impl;

import com.bscideas.telegrambot.bot.cache.CacheRepository;
import com.bscideas.telegrambot.bot.command.Command;
import com.bscideas.telegrambot.bot.manager.ButtonManager;
import com.bscideas.telegrambot.bot.manager.CommandManager;
import com.bscideas.telegrambot.bot.manager.RequestData;
import com.bscideas.telegrambot.bot.manager.ResponseData;
import com.bscideas.telegrambot.manger.HomeworkManager;
import com.bscideas.telegrambot.model.Homework;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateHomeworkCommandManagerImpl implements CommandManager {

    private final ButtonManager buttonManager;
    private final HomeworkManager homeworkManager;
    private final CacheRepository cacheRepository;

    @Override
    public boolean supports(Command command) {
        return command == Command.CREATE_HOMEWORK;
    }

    @Override
    public ResponseData handleCommand(RequestData requestData) {
        final User user = requestData.getUser();
        final Message message = requestData.getMessage();
        final SendMessage sendMessage = new SendMessage();
        final List<Message> messageList = cacheRepository.getCreateHomeworkMessages(user.getId());
        if (messageList == null) {
            cacheRepository.putCreateHomeworkMessage(user.getId(), message);
            sendMessage.setText("Введите адрес ссылки на ДЗ");
            return ResponseData.builder()
                    .response(sendMessage)
                    .build();
        }
        if (messageList.isEmpty()) {
            cacheRepository.putCreateHomeworkMessage(user.getId(), message);
            sendMessage.setText("Введите название стажировки");
            return ResponseData.builder()
                    .response(sendMessage)
                    .build();
        }
        if (messageList.size() == 1) {
            final String homeworkInfo = messageList.get(0).getText();
            final Homework homework = homeworkManager.createHomework(user.getId().toString(), homeworkInfo, message.getText());
            sendMessage.setText("ДЗ отправлено на проверку");
            sendMessage.setReplyMarkup(buttonManager.getKeyboard(user.getId().toString()));
            cacheRepository.clearUserCaches(user);
            return ResponseData.builder()
                    .chatId("274620288")
                    .message("Новая домашка на проверку c айди: " + homework.getId() + "!")
                    .response(sendMessage)
                    .build();
        }
        sendMessage.setReplyMarkup(buttonManager.getKeyboard(user.getId().toString()));
        return ResponseData.builder()
                .response(sendMessage)
                .build();
    }

}
