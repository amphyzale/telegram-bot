package com.bscideas.telegrambot.bot.manager.impl;

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

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetHomeworkCommandManagerImpl implements CommandManager {

    private final ButtonManager buttonManager;
    private final HomeworkManager homeworkManager;


    @Override
    public boolean supports(Command command) {
        return command == Command.GET_HOMEWORK;
    }

    @Override
    public ResponseData handleCommand(RequestData requestData) {
        final SendMessage sendMessage = new SendMessage();
        final String homeworks = homeworkManager.getHomeworksByStatus(null, HomeworkStatus.WAITING).stream()
                .map(this::createMessage)
                .collect(Collectors.joining("\n"));
        sendMessage.setText(homeworks);
        sendMessage.setReplyMarkup(buttonManager.getKeyboard(requestData.getMessage().getFrom().getId().toString()));
        return ResponseData.builder()
                .response(sendMessage)
                .build();
    }

    private String createMessage(Homework homework) {
        return "HomeworkId: {" + homework.getId() + "}, " +
                "status: {" + homework.getStatus() + "}, " +
                "url: {" + homework.getHomeworkUrl() + "}, " +
                "from: {@" + homework.getIntern().getUsername() + "}";
    }

}
