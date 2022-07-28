package com.bscideas.telegrambot.bot.manager.impl;

import com.bscideas.telegrambot.bot.command.Command;
import com.bscideas.telegrambot.bot.manager.ButtonManager;
import com.bscideas.telegrambot.bot.manager.CommandManager;
import com.bscideas.telegrambot.bot.manager.RequestData;
import com.bscideas.telegrambot.bot.manager.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
public class NotFoundCommandManagerImpl implements CommandManager {

    private final ButtonManager buttonManager;

    @Override
    public boolean supports(Command command) {
        return command == Command.UNEXPECTED_COMMAND;
    }

    @Override
    public ResponseData handleCommand(RequestData requestData) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Вы что-то сделали не так. Выберите команду:");
        sendMessage.setReplyMarkup(buttonManager.getKeyboard(null));
        return ResponseData.builder()
                .response(sendMessage)
                .build();
    }
}
