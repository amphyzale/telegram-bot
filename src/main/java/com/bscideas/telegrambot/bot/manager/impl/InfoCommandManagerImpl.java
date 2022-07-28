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
public class InfoCommandManagerImpl implements CommandManager {

    private final ButtonManager buttonManager;

    @Override
    public boolean supports(Command command) {
        return command == Command.INFO;
    }

    @Override
    public ResponseData handleCommand(RequestData requestData) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Это канал о самых вкусных пирожочках");
        sendMessage.setReplyMarkup(buttonManager.getKeyboard(requestData.getMessage().getFrom().getId().toString()));
        return ResponseData.builder()
                .response(sendMessage)
                .build();
    }
}
