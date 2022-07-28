package com.bscideas.telegrambot.bot.manager.impl;

import com.bscideas.telegrambot.bot.command.Command;
import com.bscideas.telegrambot.bot.manager.ButtonManager;
import com.bscideas.telegrambot.manger.AuthorizationManager;
import com.bscideas.telegrambot.manger.HomeworkManager;
import com.bscideas.telegrambot.model.HomeworkStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ButtonManagerImpl implements ButtonManager {

    private static final String INFO_LABEL = "О чем канал?";
    private static final String REGISTER_LABEL = "Зарегистрироваться";
    private static final String CREATE_HOMEWORK_LABEL = "Отправить ДЗ на проверку";
    private static final String GET_HOMEWORK_LABEL = "Получить список ДЗ в ожидании проверки";
    private static final String CHECK_HOMEWORK_LABEL = "Проверить ДЗ";
    private static final String CORRECT_HOMEWORK_LABEL = "Исправить замечания";

    private final HomeworkManager homeworkManager;
    private final AuthorizationManager authorizationManager;

    @Override
    public InlineKeyboardMarkup getKeyboard(String telegramId) {
        final List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();

        keyboardButtons.add(List.of(createButton(INFO_LABEL, Command.INFO.getCommand())));

        if (!authorizationManager.hasUserAuthority(telegramId)) {
            keyboardButtons.add(List.of(createButton(REGISTER_LABEL, Command.REGISTER.getCommand())));
        }

        if (authorizationManager.hasInternAuthority(telegramId)) {
            keyboardButtons.add(List.of(createButton(CREATE_HOMEWORK_LABEL, Command.CREATE_HOMEWORK.getCommand())));
        }

        if (authorizationManager.hasMentorAuthority(telegramId)) {
            keyboardButtons.add(List.of(createButton(GET_HOMEWORK_LABEL, Command.GET_HOMEWORK.getCommand())));
        }

        if (authorizationManager.hasMentorAuthority(telegramId)) {
            keyboardButtons.add(List.of(createButton(CHECK_HOMEWORK_LABEL, Command.CHECK_HOMEWORK.getCommand())));
        }

        if (authorizationManager.hasUserAuthority(telegramId) &&
                homeworkManager.getHomeworksByStatusAndUser(HomeworkStatus.ON_CORRECTIONS, telegramId) != null) {
            keyboardButtons.add(List.of(createButton(CORRECT_HOMEWORK_LABEL, Command.CORRECT_HOMEWORK.getCommand())));
        }

        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboardButtons);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        final InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

}
