package com.bscideas.telegrambot.bot.manager;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface ButtonManager {
    InlineKeyboardMarkup getKeyboard(String telegramId);
}
