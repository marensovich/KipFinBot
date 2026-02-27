package org.marensovich.Commands.PrivateCommands;

import org.marensovich.Bot;
import org.marensovich.Command;
import org.marensovich.EljurDatabase;
import org.marensovich.Utility.Config.ConfigLoader;

public class LogoutCommand implements Command {

    private final Bot bot;
    private final EljurDatabase eljurDatabase;

    public LogoutCommand(Bot bot, EljurDatabase eljurDatabase) {
        this.bot = bot;
        this.eljurDatabase = eljurDatabase;
    }

    @Override
    public void execute(String chatId, String text, Integer messageId) {
        if (!eljurDatabase.isUserAuthorized(Long.parseLong(chatId))) {
            String reply = ConfigLoader.getConfig().messages.logout_reply
                    .replace("%BotName%", Bot.getInstance().getBotUsername());
            bot.sendMessage(chatId, reply);
            return;
        }
        long chatIdLong = Long.parseLong(chatId);
        eljurDatabase.clearUserTelegramID(eljurDatabase.getUserID(chatIdLong));
        if (eljurDatabase.logoutUser(chatIdLong)) {
            bot.sendMessage(chatId, ConfigLoader.getConfig().messages.logout_quit_success);
        } else {
            bot.sendMessage(chatId, ConfigLoader.getConfig().messages.logout_noAuth);
        }
    }
}