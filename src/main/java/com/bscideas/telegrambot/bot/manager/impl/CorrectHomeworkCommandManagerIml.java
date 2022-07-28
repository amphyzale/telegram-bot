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
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CorrectHomeworkCommandManagerIml implements CommandManager {

    private final ButtonManager buttonManager;
    private final HomeworkManager homeworkManager;
    private final CacheRepository cacheRepository;

    @Override
    public boolean supports(Command command) {
        return command == Command.CORRECT_HOMEWORK;
    }

    @Override
    public ResponseData handleCommand(RequestData requestData) {
        final User user = requestData.getUser();
        final SendMessage sendMessage = new SendMessage();
        final List<Message> messageList = cacheRepository.getCorrectHomeworkMessages(user.getId());
        final Message message = requestData.getMessage();
        if (messageList == null) {
            cacheRepository.putCorrectHomeworkMessage(user.getId(), message);
            final List<Homework> homeworkList = homeworkManager.getHomeworksByStatusAndUser(HomeworkStatus.ON_CORRECTIONS, user.getId().toString());
            if (homeworkList == null) {
                sendMessage.setText("Something went wrong");
                cacheRepository.clearUserCaches(user);
                return ResponseData.builder()
                        .response(sendMessage)
                        .build();
            }
            sendMessage.setText("Введи один из идентификаторов ДЗ:\n" +
                    homeworkList.stream()
                            .map(i -> "HomeworkId: {" + i.getId() + "}, status: {" + i.getStatus() + "}, url: {" + i.getHomeworkUrl() + "}")
                            .collect(Collectors.joining("\n"))
            );
            return ResponseData.builder()
                    .response(sendMessage)
                    .build();
        }
        final Homework homework = homeworkManager.correctHomework(Long.valueOf(message.getText()));
        if (homework == null) {
            sendMessage.setText("No homework with id: " + message.getText());
            message.setReplyMarkup(buttonManager.getKeyboard(user.getId().toString()));
            cacheRepository.clearUserCaches(user);
            return ResponseData.builder()
                    .response(sendMessage)
                    .build();
        }
        sendMessage.setText("ДЗ отправлено на проверку");
        message.setReplyMarkup(buttonManager.getKeyboard(user.getId().toString()));
        cacheRepository.clearUserCaches(user);
        return ResponseData.builder()
                .chatId(homework.getInternship().getChatId())
                .message("Новая домашка на проверку c айди: " + homework.getId() + "!")
                .response(sendMessage)
                .build();
    }
}
