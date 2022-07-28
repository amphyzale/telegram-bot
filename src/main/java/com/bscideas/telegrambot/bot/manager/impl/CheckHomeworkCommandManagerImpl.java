package com.bscideas.telegrambot.bot.manager.impl;

import com.bscideas.telegrambot.bot.cache.CacheRepository;
import com.bscideas.telegrambot.bot.command.Command;
import com.bscideas.telegrambot.bot.manager.ButtonManager;
import com.bscideas.telegrambot.bot.manager.CommandManager;
import com.bscideas.telegrambot.bot.manager.RequestData;
import com.bscideas.telegrambot.bot.manager.ResponseData;
import com.bscideas.telegrambot.manger.HomeworkManager;
import com.bscideas.telegrambot.model.Homework;
import com.bscideas.telegrambot.model.HomeworkStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CheckHomeworkCommandManagerImpl implements CommandManager {

    private final ButtonManager buttonManager;
    private final HomeworkManager homeworkManager;
    private final CacheRepository cacheRepository;

    @Override
    public boolean supports(Command command) {
        return command == Command.CHECK_HOMEWORK;
    }

    @Override
    public ResponseData handleCommand(RequestData requestData) {
        final User user = requestData.getUser();
        final List<Message> messageList = cacheRepository.getCheckHomeworkMessages(user.getId());
        final Message message = requestData.getMessage();
        final SendMessage sendMessage = new SendMessage();
        if (messageList == null) {
            cacheRepository.putCheckHomeworkMessage(user.getId(), message);
            sendMessage.setText("Введите ID ДЗ на проверку или ID ДЗ и статус для изменения");
            return ResponseData.builder()
                    .response(sendMessage)
                    .build();
        }
        final String[] data = message.getText().split("\\s");
        final Long homeworkId = Long.valueOf(data[0]);
        if (data.length == 1) {
            homeworkManager.toCheckHomework(user.getId().toString(), homeworkId);
            sendMessage.setText("ДЗ с id: {" + homeworkId + "} взята на проверку!");
            sendMessage.setReplyMarkup(buttonManager.getKeyboard(user.getId().toString()));
            //TODO notification for Intern?
            cacheRepository.clearUserCaches(user);
            return ResponseData.builder()
                    .response(sendMessage)
                    .build();
        }
        final HomeworkStatus status = HomeworkStatus.valueOf(data[1]);
        final Optional<Homework> homework = homeworkManager.getHomework(homeworkId);
        if (homework.isPresent()
                && homework.get().getStatus() == HomeworkStatus.ON_CHECK
                && (status == HomeworkStatus.DONE || status == HomeworkStatus.ON_CORRECTIONS)) {
            sendMessage.setText("Данная работа уже на проверке");
            sendMessage.setReplyMarkup(buttonManager.getKeyboard(user.getId().toString()));
            cacheRepository.clearUserCaches(user);
            return ResponseData.builder()
                    .response(sendMessage)
                    .build();
        }
        homeworkManager.toResolveHomework(user.getId().toString(), homeworkId, status);
        cacheRepository.clearUserCaches(user);
        sendMessage.setText("ДЗ успешно переведена в статус: {" + status.getText() + "}");
        sendMessage.setReplyMarkup(buttonManager.getKeyboard(user.getId().toString()));
        return ResponseData.builder()
                .chatId(homework.map(value -> value.getIntern().getTelegramId()).orElse(null))
                .message(homework.isPresent() ? "Ваша работа переведена в статус: {" + status.getText() + "}" : null)
                .response(sendMessage)
                .build();
    }
}
