package org.marensovich.Commands;

import org.marensovich.*;
import org.marensovich.Commands.GroupCommands.GroupInfoCommand;
import org.marensovich.Commands.PrivateCommands.*;
import org.marensovich.Commands.GroupCommands.*;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> privateCommands = new HashMap<>();
    private final Map<String, Command> groupCommands = new HashMap<>();

    private final Map<Long, Command> userSessions = new HashMap<>(); // для хранения состояния пользователей

    private final Bot bot;
    private final EljurDatabase eljurDatabase;

    public CommandManager(Bot bot, EljurDatabase eljurDatabase) {
        this.bot = bot;
        this.eljurDatabase = eljurDatabase;
        initializeCommands();
    }


    private void initializeCommands() {
        privateCommands.put("/settings", new SettingsCommand(bot, eljurDatabase));
        privateCommands.put("/score", new ScoreCommand(bot, eljurDatabase));
        privateCommands.put("/homework", new HomeworkCommand(bot, eljurDatabase));
        privateCommands.put("/schedule", new SheduleCommand(bot, eljurDatabase));
        privateCommands.put("/auth", new AuthCommand(bot, eljurDatabase));
        privateCommands.put("/logout", new LogoutCommand(bot, eljurDatabase));
        privateCommands.put("/support", new SupportCommand(bot, eljurDatabase));
        privateCommands.put("/books", new BooksCommand(bot, eljurDatabase));
        privateCommands.put("/help", new HelpCommand());
        privateCommands.put("/rating", new RatingCommand(bot, eljurDatabase));
        privateCommands.put("/start", new StartCommand());
        privateCommands.put("/profile", new ProfileCommand(bot, eljurDatabase));

        groupCommands.put("/group_info", new GroupInfoCommand(bot, eljurDatabase));
    }

    public void handleCommand(Long chatId, String text, String chatType) {
        String command = text.split("@")[0];
        System.out.println("Обработка команды: " + text + " в чате типа: " + chatType);

        if ("PRIVATE".equals(chatType)) {
            // Обработка команд для личных сообщений
            Command commandToExecute = privateCommands.get(command);
            if (commandToExecute != null) {
                commandToExecute.execute(chatId.toString(), text, null);
            }
        } else if ("GROUP".equals(chatType)) {
            Command commandToExecute = groupCommands.get(command);
            if (commandToExecute != null) {
                commandToExecute.execute(chatId.toString(), text, null);
            }
        }
    }
    public void handleCallback(Long chatId, String callbackData, Integer messageId) {
        if (callbackData.startsWith("settings_")) {
            SettingsCommand settingsCommand = (SettingsCommand) privateCommands.get("/settings");
            settingsCommand.handleCallback(chatId.toString(), callbackData, messageId);
        }
        if (callbackData.startsWith("support_")) {
            SupportCommand supportCommand = (SupportCommand) privateCommands.get("/support");
            supportCommand.handleCallback(chatId.toString(), callbackData, messageId);
        }
    }


    public Command getUserSession(long chatId) {
        return userSessions.get(chatId);
    }

    public void closeSession(long chatId) {
        userSessions.remove(chatId);
    }
}
