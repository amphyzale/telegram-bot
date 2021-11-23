package com.bscideas.telegrambot.bot;

import com.bscideas.telegrambot.bot.command.Command;
import com.bscideas.telegrambot.manger.HomeworkManager;
import com.bscideas.telegrambot.manger.RegistrationManager;
import com.bscideas.telegrambot.model.Homework;
import com.bscideas.telegrambot.model.HomeworkStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class HomeworkBot extends TelegramLongPollingBot {

    private final String INFO_LABEL = "О чем канал?";
    private final String REGISTER_LABEL = "Зарегистрироваться";
    private final String CREATE_HOMEWORK_LABEL = "Отправить ДЗ на проверку";
    private final String GET_HOMEWORK_LABEL = "Получить список ДЗ в ожидании проверки";
    private final String CHECK_HOMEWORK_LABEL = "Проверить ДЗ";
    private final String DEMO_LABEL = "Какая то функция 2";

    private final Map<Long, List<Message>> registrationMap = new HashMap<>();
    private final Map<Long, List<Message>> createHomeworkMap = new HashMap<>();
    Map<Long, CallbackQuery> callbackMap = new HashMap<>();

    private final HomeworkManager homeworkManager;
    private final RegistrationManager registrationManager;

    @Value("${bot.username}")
    private String name;

    @Value("${bot.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            callbackMap.put(update.getCallbackQuery().getFrom().getId(), update.getCallbackQuery());
            handleCallback(update.getCallbackQuery());
        }
    }

    private void handleMessage(Message message) {
        String text = message.getText();
        long chat_id = message.getChatId();
        try {
            SendMessage sendMessage = getCommandResponse(text, message.getFrom(), String.valueOf(chat_id), message);
            sendMessage.enableHtml(true);
            sendMessage.setParseMode(ParseMode.HTML);
            sendMessage.setChatId(String.valueOf(chat_id));
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("", e);
            SendMessage sendMessage = handleNotFoundCommand();
            sendMessage.setChatId(String.valueOf(chat_id));
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        try {
            SendMessage message = getCommandResponse(
                    callbackQuery.getData(), callbackQuery.getFrom(), String.valueOf(callbackQuery.getMessage().getChatId()), null
            );
            message.enableHtml(true);
            message.setParseMode(ParseMode.HTML);
            message.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
            execute(message);
        } catch (TelegramApiException e) {
            log.error("", e);
        }
    }

    private SendMessage getCommandResponse(String text, User user, String chatId, Message message) throws TelegramApiException {
        if (text.equals(Command.START.getCommand())) {
            return handleStartCommand();
        }
        if (text.equals(Command.INFO.getCommand())) {
            return handleInfoCommand();
        }
        if (text.equals(Command.REGISTER.getCommand()) || registrationMap.get(user.getId()) != null) {
            return handleRegisterCommand(user, message);
        }
        if (text.equals(Command.CREATE_HOMEWORK.getCommand()) || createHomeworkMap.get(user.getId()) != null) {
            return handleCreateHomeworkCommand(user, message);
        }
        if (text.equals(Command.GET_HOMEWORK.getCommand())) {
            return handleGetHomeworkCommand();
        }
        if (text.equals(Command.DEMO.getCommand())) {
            return handleDemoCommand(user.getUserName(), String.valueOf(user.getId()), user.getFirstName(), chatId);
        }
        return handleNotFoundCommand();
    }

    private SendMessage handleInfoCommand() {
        SendMessage message = new SendMessage();
        message.setText("Это канал о самых вкусных пирожочках");
        message.setReplyMarkup(getKeyboard());
        return message;
    }

    private SendMessage handleStartCommand() {
        SendMessage message = new SendMessage();
        message.setText("Доступные команды:");
        message.setReplyMarkup(getKeyboard());
        return message;
    }

    private SendMessage handleRegisterCommand(User user, Message message) {
        final SendMessage sendMessage = new SendMessage();
        final List<Message> messageList = registrationMap.get(user.getId());
        if (messageList == null) {
            registrationMap.put(user.getId(), new LinkedList<>());
            sendMessage.setText("Введите ФИО");
            return sendMessage;
        }
        if (messageList.isEmpty()) {
            messageList.add(message);
            sendMessage.setText("Введите кодовое слово");
            return sendMessage;
        }
        registrationManager.register(messageList.get(0).getText(), message.getText(), user.getId().toString(), user.getUserName());
        registrationMap.remove(user.getId());
        sendMessage.setText("Регистрация успешна!");
        sendMessage.setReplyMarkup(getKeyboard());
        return sendMessage;
    }

    private SendMessage handleCreateHomeworkCommand(User user, Message message) {
        final SendMessage sendMessage = new SendMessage();
        final List<Message> messageList = createHomeworkMap.get(user.getId());
        if (messageList == null) {
            createHomeworkMap.put(user.getId(), new LinkedList<>());
            sendMessage.setText("Введите адрес ссылки на ДЗ");
            return sendMessage;
        }
        final Homework homework = homeworkManager.createHomework(user.getId().toString(), message.getText());
        final SendMessage notifyMessage = new SendMessage();
        notifyMessage.setText("Новая домашка на проверку c айди: " + homework.getId() + "!");
        notifyMessage.setChatId("274620288"); //TODO ID BSC Academy channel
        try {
            execute(notifyMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        sendMessage.setText("ДЗ отправлено на проверку");
        sendMessage.setReplyMarkup(getKeyboard());
        createHomeworkMap.remove(user.getId());
        return sendMessage;
    }

    private SendMessage handleGetHomeworkCommand() {
        SendMessage message = new SendMessage();
        final String homeworks = homeworkManager.getHomeworks(null, HomeworkStatus.WAITING).stream()
                .map(i -> "HomeworkId: {" + i.getId() + "}, status: {" + i.getStatus() + "}, url: {" + i.getHomeworkUrl() + "}, from: {" + i.getMentor().getUsername() + "}")
                .collect(Collectors.joining("\n"));
        message.setText(homeworks);
        message.setReplyMarkup(getKeyboard());
        return message;
    }

    private SendMessage handleNotFoundCommand() {
        SendMessage message = new SendMessage();
        message.setText("Вы что-то сделали не так. Выберите команду:");
        message.setReplyMarkup(getKeyboard());
        return message;
    }

    private SendMessage handleDemoCommand(String username, String id, String name, String chatId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setReplyMarkup(getKeyboard());
        return message;
    }

    private InlineKeyboardMarkup getKeyboard() {
        final InlineKeyboardButton inlineKeyboardButtonInfo = new InlineKeyboardButton();
        inlineKeyboardButtonInfo.setText(INFO_LABEL);
        inlineKeyboardButtonInfo.setCallbackData(Command.INFO.getCommand());
        final List<InlineKeyboardButton> keyboardButtonsRow0 = new ArrayList<>();
        keyboardButtonsRow0.add(inlineKeyboardButtonInfo);

        final InlineKeyboardButton inlineKeyboardButtonRegister = new InlineKeyboardButton();
        inlineKeyboardButtonRegister.setText(REGISTER_LABEL);
        inlineKeyboardButtonRegister.setCallbackData(Command.REGISTER.getCommand());
        final List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButtonRegister);

        final InlineKeyboardButton inlineKeyboardButtonCreateHomework = new InlineKeyboardButton();
        inlineKeyboardButtonCreateHomework.setText(CREATE_HOMEWORK_LABEL);
        inlineKeyboardButtonCreateHomework.setCallbackData(Command.CREATE_HOMEWORK.getCommand());
        final List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButtonCreateHomework);

        final InlineKeyboardButton inlineKeyboardButtonGetHomework = new InlineKeyboardButton();
        inlineKeyboardButtonGetHomework.setText(GET_HOMEWORK_LABEL);
        inlineKeyboardButtonGetHomework.setCallbackData(Command.GET_HOMEWORK.getCommand());
        final List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(inlineKeyboardButtonGetHomework);

        final InlineKeyboardButton inlineKeyboardButtonDemo = new InlineKeyboardButton();
        inlineKeyboardButtonDemo.setText(DEMO_LABEL);
        inlineKeyboardButtonDemo.setCallbackData(Command.DEMO.getCommand());
        final List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        keyboardButtonsRow4.add(inlineKeyboardButtonDemo);

        final List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
        keyboardButtons.add(keyboardButtonsRow0);
        keyboardButtons.add(keyboardButtonsRow1);
        keyboardButtons.add(keyboardButtonsRow2);
        keyboardButtons.add(keyboardButtonsRow3);
        keyboardButtons.add(keyboardButtonsRow4);

        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboardButtons);
        return inlineKeyboardMarkup;
    }
}
