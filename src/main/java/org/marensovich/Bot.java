package org.marensovich;

import org.marensovich.Commands.CommandManager;
import org.marensovich.Commands.PrivateCommands.AuthCommand;
import org.marensovich.Commands.PrivateCommands.BooksCommand;
import org.marensovich.Utility.Config.ConfigData;
import org.marensovich.Utility.Config.ConfigLoader;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Bot extends TelegramLongPollingBot {

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    ConfigData config = ConfigLoader.getConfig();

    private CommandManager commandManager;
    private static Bot instance;
    private EljurDatabase eljurDatabase;

    public Bot() {
        this.eljurDatabase = new EljurDatabase();
        startBot();
    }

    public void startBot() {
        eljurDatabase.checkDatabase();
//        this.commandManager = new CommandManager(this, eljurDatabase);
    }

    public static Bot getInstance() {
        if (instance == null) {
            instance = new Bot();
        }
        return instance;
    }

    private final BooksCommand booksCommand = new BooksCommand(this, eljurDatabase);
    private final AuthCommand authCommand = new AuthCommand(this, eljurDatabase);

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            String text = message.getText();
            String chatType = getChatType(message.getChat());
            commandManager.handleCommand(Long.parseLong(chatId), text, chatType);
        } else if (update.hasCallbackQuery()) {
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            String callbackData = update.getCallbackQuery().getData();

            if (callbackData.startsWith("settings_")) {
                commandManager.handleCallback(Long.parseLong(chatId), callbackData, messageId);
            }
            if (callbackData.startsWith("support_")) {
                commandManager.handleCallback(Long.parseLong(chatId), callbackData, messageId);
            }
        }
    }

    private String getChatType(Chat chat) {
        String chatType = chat.getType().toString();

        if ("group".equalsIgnoreCase(chatType) || "supergroup".equalsIgnoreCase(chatType)) {
            return "GROUP"; // Для групповых чатов
        } else if ("private".equalsIgnoreCase(chatType)) {
            return "PRIVATE"; // Для личных сообщений
        }
        return "UNKNOWN"; // Если тип чата не распознан
    }


    @Override
    public String getBotUsername() {
        return config.bot.username;
    }

    @Override
    public String getBotToken() {
        return config.bot.token;
    }

    public void sendFormattedMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode("HTML");

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void deleteMessage(String chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(messageId);
        deleteMessage.setChatId(chatId);
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendVideo(String chatId, InputFile video, String caption) {
        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(chatId);
        sendVideo.setVideo(video);
        sendVideo.setCaption(caption);
        try {
            execute(sendVideo);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    public void sendVideo(String chatId, InputFile video) {
        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(chatId);
        sendVideo.setVideo(video);
        try {
            execute(sendVideo);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    public void sendPhoto(String chatId, InputFile photo, String caption) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(photo);
        sendPhoto.setCaption(caption);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    public void sendPhoto(String chatId, InputFile photo) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(photo);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    public void sendMessageWithKeyboard(long chatId, String text, Object keyboardMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.disableWebPagePreview();
        message.setParseMode("HTML");

        if (keyboardMarkup instanceof InlineKeyboardMarkup) {
            message.setReplyMarkup((InlineKeyboardMarkup) keyboardMarkup);
        } else if (keyboardMarkup instanceof ReplyKeyboardMarkup) {
            message.setReplyMarkup((ReplyKeyboardMarkup) keyboardMarkup);
        } else {
            throw new IllegalArgumentException("Unsupported keyboard type");
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageWithKeyboardRemove(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(new ReplyKeyboardRemove(true)); // Убираем клавиатуру

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
