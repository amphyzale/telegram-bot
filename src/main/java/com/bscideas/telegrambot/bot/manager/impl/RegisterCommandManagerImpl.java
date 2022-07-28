package com.bscideas.telegrambot.bot.manager.impl;

import com.bscideas.telegrambot.bot.cache.CacheRepository;
import com.bscideas.telegrambot.bot.command.Command;
import com.bscideas.telegrambot.bot.manager.ButtonManager;
import com.bscideas.telegrambot.bot.manager.CommandManager;
import com.bscideas.telegrambot.bot.manager.RequestData;
import com.bscideas.telegrambot.bot.manager.ResponseData;
import com.bscideas.telegrambot.manger.RegistrationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegisterCommandManagerImpl implements CommandManager {

    private final ButtonManager buttonManager;
    private final CacheRepository cacheRepository;
    private final RegistrationManager registrationManager;


    @Override
    public boolean supports(Command command) {
        return command == Command.REGISTER;
    }

    @Override
    public ResponseData handleCommand(RequestData requestData) {
        final User user = requestData.getUser();
        final Message message = requestData.getMessage();
        final SendMessage sendMessage = new SendMessage();
        final List<Message> messageList = cacheRepository.getRegistrationMessages(user.getId());
        if (messageList == null) {
            cacheRepository.putRegistrationMessage(user.getId(), message);
            sendMessage.setText("Введите ФИО");
            return ResponseData.builder()
                    .response(sendMessage)
                    .build();
        }
        if (messageList.isEmpty()) {
            cacheRepository.putRegistrationMessage(user.getId(), message);
            sendMessage.setText("Введите кодовое слово");
            return ResponseData.builder()
                    .response(sendMessage)
                    .build();
        }
        registrationManager.register(
                messageList.get(0).getText(),
                message.getText(),
                user.getId().toString(),
                user.getUserName()
        );
        cacheRepository.clearUserCaches(user);
        sendMessage.setText("Регистрация успешна!");
        sendMessage.setReplyMarkup(buttonManager.getKeyboard(user.getId().toString()));
        return ResponseData.builder()
                .response(sendMessage)
                .build();
    }
}
