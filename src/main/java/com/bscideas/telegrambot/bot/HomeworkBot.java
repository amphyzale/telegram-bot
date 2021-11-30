package com.bscideas.telegrambot.bot;

import com.bscideas.telegrambot.bot.command.Command;
import com.bscideas.telegrambot.configuration.TelegramBotConfiguration;
import com.bscideas.telegrambot.manger.HomeworkManager;
import com.bscideas.telegrambot.manger.RegistrationManager;
import com.bscideas.telegrambot.model.Homework;
import com.bscideas.telegrambot.model.HomeworkStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private static final String INFO_LABEL = "О чем канал?";
    private static final String REGISTER_LABEL = "Зарегистрироваться";
    private static final String CREATE_HOMEWORK_LABEL = "Отправить ДЗ на проверку";
    private static final String GET_HOMEWORK_LABEL = "Получить список ДЗ в ожидании проверки";
    private static final String CHECK_HOMEWORK_LABEL = "Проверить ДЗ";
    private static final String CORRECT_HOMEWORK_LABEL = "Исправить замечания";

    private final Map<Long, List<Message>> registrationMap = new HashMap<>();
    private final Map<Long, List<Message>> createHomeworkMap = new HashMap<>();
    private final Map<Long, List<Message>> checkHomeworkMap = new HashMap<>();
    private final Map<Long, List<Message>> correctHomeworkMap = new HashMap<>();

    private final HomeworkManager homeworkManager;
    private final RegistrationManager registrationManager;
    private final TelegramBotConfiguration telegramBotConfiguration;

    @Override
    public String getBotUsername() {
        return telegramBotConfiguration.getName();
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
            clearCaches(update.hasMessage() ? update.getMessage().getFrom() : update.getCallbackQuery().getFrom());
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
            clearCaches(message.getFrom());
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
            clearCaches(callbackQuery.getFrom());
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
        if (text.equals(Command.CHECK_HOMEWORK.getCommand()) || checkHomeworkMap.get(user.getId()) != null) {
            return handleCheckHomeworkCommand(user, message);
        }
        if (text.equals(Command.CORRECT_HOMEWORK.getCommand()) || correctHomeworkMap.get(user.getId()) != null) {
            return correctHomeworkCommand(user, message);
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
        if (messageList.isEmpty()) {
            createHomeworkMap.get(user.getId()).add(message);
            sendMessage.setText("Введите название стажировки");
            return sendMessage;
        }
        if (messageList.size() == 1) {
            final Homework homework = homeworkManager.createHomework(user.getId().toString(), createHomeworkMap.get(user.getId()).get(0).getText(), message.getText());
            notifyByChatId("274620288", "Новая домашка на проверку c айди: " + homework.getId() + "!");
            sendMessage.setText("ДЗ отправлено на проверку");
            sendMessage.setReplyMarkup(getKeyboard());
            createHomeworkMap.remove(user.getId());
            return sendMessage;
        }
        sendMessage.setReplyMarkup(getKeyboard());
        return sendMessage;
    }

    private SendMessage handleGetHomeworkCommand() {
        final SendMessage message = new SendMessage();
        final String homeworks = homeworkManager.getHomeworksByStatus(null, HomeworkStatus.WAITING).stream()
                .map(i -> "HomeworkId: {" + i.getId() + "}, status: {" + i.getStatus() + "}, url: {" + i.getHomeworkUrl() + "}, from: {@" + i.getIntern().getUsername() + "}")
                .collect(Collectors.joining("\n"));
        message.setText(homeworks);
        message.setReplyMarkup(getKeyboard());
        return message;
    }

    private SendMessage handleCheckHomeworkCommand(User user, Message message) {
        final SendMessage sendMessage = new SendMessage();
        final List<Message> messageList = checkHomeworkMap.get(user.getId());
        if (messageList == null) {
            checkHomeworkMap.put(user.getId(), new LinkedList<>());
            sendMessage.setText("Введите ID ДЗ на проверку или ID ДЗ и статус для изменения");
            return sendMessage;
        }
        final String[] data = message.getText().split("\\s");
        final Long homeworkId = Long.valueOf(data[0]);
        if (data.length == 1) {
            homeworkManager.toCheckHomework(user.getId().toString(), homeworkId);
            sendMessage.setText("ДЗ с id: {" + homeworkId + "} взята на проверку!");
            sendMessage.setReplyMarkup(getKeyboard());
            //TODO notification for Intern?
            checkHomeworkMap.remove(user.getId());
            return sendMessage;
        }
        final HomeworkStatus status = HomeworkStatus.valueOf(data[1]);
        final Optional<Homework> homework = homeworkManager.getHomework(homeworkId);
//        if (homework.isPresent() && homework.get().getStatus() == HomeworkStatus.ON_CHECK &&
//                (status != HomeworkStatus.DONE || status != HomeworkStatus.ON_CORRECTIONS)) {
//            sendMessage.setText("Данная работа уже на проверке");
//            sendMessage.setReplyMarkup(getKeyboard());
//            checkHomeworkMap.remove(user.getId());
//            return sendMessage;
//        }
        homeworkManager.toResolveHomework(user.getId().toString(), homeworkId, status);
        homework
                .ifPresent(i -> notifyByChatId(i.getIntern().getTelegramId(), "Ваша работа переведена в статус: {" + status.getText() + "}"));
        checkHomeworkMap.remove(user.getId());
        sendMessage.setText("ДЗ успешно переведена в статус: {" + status.getText() + "}");
        sendMessage.setReplyMarkup(getKeyboard());
        return sendMessage;
    }

    private SendMessage correctHomeworkCommand(User user, Message message) {
        final SendMessage sendMessage = new SendMessage();
        final List<Message> messageList = correctHomeworkMap.get(user.getId());
        if (messageList == null) {
            correctHomeworkMap.put(user.getId(), new LinkedList<>());
            final List<Homework> homeworkList = homeworkManager.getHomeworksByStatusAndUser(HomeworkStatus.ON_CORRECTIONS, user.getId().toString());
            if (homeworkList == null) {
                sendMessage.setText("Something went wrong");
                correctHomeworkMap.remove(user.getId());
                return sendMessage;
            }
            sendMessage.setText("Введи один из идентификаторов ДЗ:\n" +
                    homeworkList.stream()
                            .map(i -> "HomeworkId: {" + i.getId() + "}, status: {" + i.getStatus() + "}, url: {" + i.getHomeworkUrl() + "}")
                            .collect(Collectors.joining("\n"))
            );
            return sendMessage;
        }
        final Homework homework = homeworkManager.correctHomework(Long.valueOf(message.getText()));
        if (homework == null) {
            sendMessage.setText("No homework with id: " + message.getText());
            message.setReplyMarkup(getKeyboard());
            correctHomeworkMap.remove(user.getId());
            return sendMessage;
        }
        notifyByChatId(homework.getInternship().getChatId(), "Новая домашка на проверку c айди: " + homework.getId() + "!");
        sendMessage.setText("ДЗ отправлено на проверку");
        message.setReplyMarkup(getKeyboard());
        correctHomeworkMap.remove(user.getId());
        return sendMessage;
    }

    private SendMessage handleNotFoundCommand() {
        SendMessage message = new SendMessage();
        message.setText("Вы что-то сделали не так. Выберите команду:");
        message.setReplyMarkup(getKeyboard());
        return message;
    }

    private void notifyByChatId(String chatId, String message) {  //TODO ID BSC Academy channel
        final SendMessage notifyMessage = new SendMessage();
        notifyMessage.setText(message);
        notifyMessage.setChatId(chatId);
        try {
            execute(notifyMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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

        final InlineKeyboardButton inlineKeyboardButtonCheckHomework = new InlineKeyboardButton();
        inlineKeyboardButtonCheckHomework.setText(CHECK_HOMEWORK_LABEL);
        inlineKeyboardButtonCheckHomework.setCallbackData(Command.CHECK_HOMEWORK.getCommand());
        final List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        keyboardButtonsRow4.add(inlineKeyboardButtonCheckHomework);

        final InlineKeyboardButton inlineKeyboardButtonCorrectHomework = new InlineKeyboardButton();
        inlineKeyboardButtonCorrectHomework.setText(CORRECT_HOMEWORK_LABEL);
        inlineKeyboardButtonCorrectHomework.setCallbackData(Command.CORRECT_HOMEWORK.getCommand());
        final List<InlineKeyboardButton> keyboardButtonsRow5 = new ArrayList<>();
        keyboardButtonsRow5.add(inlineKeyboardButtonCorrectHomework);

        final List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
        keyboardButtons.add(keyboardButtonsRow0);
        keyboardButtons.add(keyboardButtonsRow1);
        keyboardButtons.add(keyboardButtonsRow2);
        keyboardButtons.add(keyboardButtonsRow3);
        keyboardButtons.add(keyboardButtonsRow4);
        keyboardButtons.add(keyboardButtonsRow5);

        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboardButtons);
        return inlineKeyboardMarkup;
    }

    private void clearCaches(User user) {
        registrationMap.remove(user.getId());
        createHomeworkMap.remove(user.getId());
        checkHomeworkMap.remove(user.getId());
        correctHomeworkMap.remove(user.getId());
    }
}
