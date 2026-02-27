package org.marensovich.Commands.PrivateCommands;

import org.marensovich.Bot;
import org.marensovich.Command;
import org.marensovich.EljurDatabase;
import org.marensovich.Utility.Config.ConfigLoader;

public class HomeworkCommand implements Command {

    private final Bot bot;
    private final EljurDatabase eljurDatabase;

    public HomeworkCommand(Bot bot, EljurDatabase eljurDatabase) {
        this.bot = bot;
        this.eljurDatabase = eljurDatabase;
    }

    @Override
    public void execute(String chatId, String text, Integer messageId) {
        if (!eljurDatabase.isUserAuthorized(Long.parseLong(chatId))) {
            String reply = ConfigLoader.getConfig().messages.error_access_to_command_auth
                    .replace("%BotName%", Bot.getInstance().getBotUsername())
                    .replace("%command%", "/homework");
            bot.sendMessage(chatId, reply);
            return;
        }
    }


}
