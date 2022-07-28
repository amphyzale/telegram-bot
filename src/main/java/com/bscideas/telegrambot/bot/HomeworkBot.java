package com.bscideas.telegrambot.bot;

import com.bscideas.telegrambot.bot.cache.CacheRepository;
import com.bscideas.telegrambot.bot.command.Command;
import com.bscideas.telegrambot.bot.manager.CommandProcessor;
import com.bscideas.telegrambot.bot.manager.RequestData;
import com.bscideas.telegrambot.bot.manager.ResponseData;
import com.bscideas.telegrambot.configuration.TelegramBotConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class HomeworkBot extends TelegramLongPollingBot {

    private final CacheRepository cacheRepository;
    private final CommandProcessor commandProcessor;
    private final TelegramBotConfiguration telegramBotConfiguration;

    @Override
    public String getBotUsername() {
        return telegramBotConfiguration.getUsername();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfiguration.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                handleCallback(update.getCallbackQuery());
            }
        } catch (Exception e) {
            cacheRepository.clearUserCaches(
                    update.hasMessage() ?
                            update.getMessage().getFrom() :
                            update.getCallbackQuery().getFrom()
            );
        }
    }

    private void handleMessage(Message message) {
        final long chatId = message.getChatId();
        try {
            final RequestData requestData = RequestData.builder()
                    .command(resolve(message))
                    .user(message.getFrom())
                    .message(message)
                    .build();
            final ResponseData responseData = commandProcessor.process(requestData);
            final SendMessage sendMessage = responseData.getResponse();
            sendMessage.enableHtml(true);
            sendMessage.setParseMode(ParseMode.HTML);
            sendMessage.setChatId(String.valueOf(chatId));
            execute(sendMessage);
            if (responseData.hasNotification()) {
                notifyByChatId(responseData.getChatId(), responseData.getMessage());
            }
        } catch (TelegramApiException e) {
            log.error("Error while message processing", e);
            cacheRepository.clearUserCaches(message.getFrom());
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        try {
            final RequestData requestData = RequestData.builder()
                    .command(Command.resolve(callbackQuery.getData()))
                    .user(callbackQuery.getFrom())
                    .message(callbackQuery.getMessage())
                    .build();
            final ResponseData responseData = commandProcessor.process(requestData);
            final SendMessage sendMessage = responseData.getResponse();
            sendMessage.enableHtml(true);
            sendMessage.setParseMode(ParseMode.HTML);
            sendMessage.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
            execute(sendMessage);
            if (responseData.hasNotification()) {
                notifyByChatId(responseData.getChatId(), responseData.getMessage());
            }
        } catch (TelegramApiException e) {
            log.error("Error while callback processing", e);
            cacheRepository.clearUserCaches(callbackQuery.getFrom());
        }
    }

    /**
     * Метод-костыль, посколько сообщения в телеграмме не отслеживают цепочку вызовов -
     * последние действия пользователя восттанавливаются через кеш
     */
    private Command resolve(Message message) {
        if (cacheRepository.getCheckHomeworkMessages(message.getFrom().getId()) != null) {
            return Command.CHECK_HOMEWORK;
        }
        if (cacheRepository.getCreateHomeworkMessages(message.getFrom().getId()) != null) {
            return Command.CREATE_HOMEWORK;
        }
        if (cacheRepository.getCorrectHomeworkMessages(message.getFrom().getId()) != null) {
            return Command.CORRECT_HOMEWORK;
        }
        if (cacheRepository.getRegistrationMessages(message.getFrom().getId()) != null) {
            return Command.REGISTER;
        }
        return Command.resolve(message.getText());
    }

    //TODO ID BSC Academy channel
    private void notifyByChatId(String chatId, String message) {
        final SendMessage notifyMessage = new SendMessage();
        notifyMessage.setText(message);
        notifyMessage.setChatId(chatId);
        try {
            execute(notifyMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
