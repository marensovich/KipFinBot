package org.marensovich.Commands.PrivateCommands;

import org.marensovich.Bot;
import org.marensovich.Command;
import org.marensovich.Commands.CommandManager;
import org.marensovich.EljurDatabase;
import org.marensovich.Utility.Config.ConfigLoader;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.ObjectInputFilter;
import java.util.*;

public class AuthCommand implements Command {

    private final Bot bot;
    private final Map<Long, UserData> userDataMap = new HashMap<>();
    private final CommandManager commandManager;
    private final EljurDatabase eljurDatabase;

    private static class UserData {
        String login;
        String password;
        String phone;
        Long chatId;
    }

    public AuthCommand(Bot bot, EljurDatabase eljurDatabase) {
        this.bot = bot;
        this.commandManager = bot.getCommandManager();
        this.eljurDatabase = eljurDatabase;
    }

    @Override
    public void execute(String chatId, String text, Integer messageId) {
        long chatIdLong = Long.parseLong(chatId);
        UserData userData = userDataMap.getOrDefault(chatIdLong, new UserData());

        if (text.equals("/auth")) {
            // Проверяем, авторизован ли пользователь
            if (eljurDatabase.isUserAuthorized(chatIdLong)) {
                bot.sendMessage(chatId, ConfigLoader.getConfig().messages.auth_logged);
                commandManager.closeSession(Long.parseLong(chatId));
                userDataMap.remove(Long.parseLong(chatId));
                return;
            }
            startAuth(chatIdLong);
        } else if (text.equalsIgnoreCase("подтвердить")) {
            if (validateUserData(userData)) {
                confirmAuth(chatIdLong, userData);
            } else {
                bot.sendMessage(chatId, ConfigLoader.getConfig().messages.auth_errorInput);
                cancelAuth(chatIdLong);
            }
        } else if (text.equalsIgnoreCase("отменить")) {
            cancelAuth(chatIdLong);
        } else {
            handleUserInput(chatIdLong, text, userData);
        }
    }

    // Метод для обработки контакта
    public void handleContact(String chatId, String phoneNumber, Integer messageId) {
        long chatIdLong = Long.parseLong(chatId);
        UserData userData = userDataMap.get(chatIdLong);

        if (userData != null && userData.phone == null) {
            userData.phone = phoneNumber;
            userDataMap.put(chatIdLong, userData);
            confirmData(Long.parseLong(chatId), userData);
        } else {
            bot.sendMessage(chatId, ConfigLoader.getConfig().messages.auth_errorContact);
        }
    }

    private void startAuth(long chatId) {
        UserData newUserData = new UserData();
        userDataMap.put(chatId, newUserData);
        bot.sendMessage(String.valueOf(chatId), ConfigLoader.getConfig().messages.auth_writeLogin);
    }

    private void handleUserInput(long chatId, String text, UserData userData) {
        if (userData.login == null) {
            userData.login = text;
            userData.chatId = chatId;
            userDataMap.put(chatId, userData);
            bot.sendMessage(String.valueOf(chatId), ConfigLoader.getConfig().messages.auth_writePassoword);
        } else if (userData.password == null) {
            userData.password = text;
            userDataMap.put(chatId, userData);
            askForPhone(chatId); // Переходим к запросу номера телефона
        } else if (userData.phone == null) {
            userData.phone = text;
            userDataMap.put(chatId, userData);
            confirmData(chatId, userData); // Переходим к подтверждению данных
        }
    }

    private void askForPhone(long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        // Кнопка для отправки контакта
        KeyboardButton contactButton = new KeyboardButton("Отправить мой контакт");
        contactButton.setRequestContact(true);
        row.add(contactButton);
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true); // Автоматически подгонять размер кнопок

        bot.sendMessageWithKeyboard(chatId, ConfigLoader.getConfig().messages.auth_sendPhone, keyboardMarkup);
    }

    private void confirmData(long chatId, UserData userData) {

        String messageText = ConfigLoader.getConfig().messages.auth_chechData
                .replace("%login%", userData.login)
                .replace("%password%", "*".repeat(userData.password.length()))
                .replace("%phone%", userData.phone);

        // Создаем клавиатуру
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Подтвердить");
        row.add("Отменить");
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true); // Автоматически подгонять размер кнопок

        bot.sendMessageWithKeyboard(chatId, messageText, keyboardMarkup);
    }

    private boolean validateUserData(UserData userData) {
        if (userData.login == null || userData.login.isEmpty() ||
                userData.password == null || userData.password.isEmpty() ||
                userData.phone == null || userData.phone.isEmpty()) {
            return false;
        }

        Integer userID = eljurDatabase.checkUserCredentials(userData.login, userData.password, userData.phone);
        if (userID == null) {
            return false;
        }

        String fullName = eljurDatabase.getUserFullName(userID);
        if (fullName == null) {
            return false;
        }

        eljurDatabase.addAuthUser(userID, userData.chatId, userData.login, fullName);
        eljurDatabase.updateUserTelegramID(userID, userData.chatId);
        return true;
    }

    private void confirmAuth(long chatId, UserData userData) {
        if (validateUserData(userData)) {
            String fullName = eljurDatabase.getUserFullName(eljurDatabase.checkUserCredentials(userData.login, userData.password, userData.phone));
            String[] nameParts = fullName.split(" ");
            String nameWithoutSurname = "";
            if (nameParts.length > 1) {
                nameWithoutSurname = String.join(" ", Arrays.copyOfRange(nameParts, 1, nameParts.length));
            }
            bot.sendMessageWithKeyboardRemove(chatId, ConfigLoader.getConfig().messages.auth_success);
            bot.sendMessageWithKeyboardRemove(chatId,  ConfigLoader.getConfig().messages.auth_success1.replace("%name%", nameWithoutSurname));
        } else {
            bot.sendMessageWithKeyboardRemove(chatId, ConfigLoader.getConfig().messages.auth_errorInput);
        }
        userDataMap.remove(chatId); // Очищаем данные пользователя
        commandManager.closeSession(chatId); // Закрываем сессию
    }

    private void cancelAuth(long chatId) {
        bot.sendMessageWithKeyboardRemove(chatId, ConfigLoader.getConfig().messages.auth_cancel);
        userDataMap.remove(chatId); // Очищаем данные пользователя
        commandManager.closeSession(chatId); // Закрываем сессию
    }
}