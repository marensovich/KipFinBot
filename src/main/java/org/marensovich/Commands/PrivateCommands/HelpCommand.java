package org.marensovich.Commands.PrivateCommands;

import org.marensovich.Bot;
import org.marensovich.Command;
import org.marensovich.Utility.Config.ConfigLoader;

public class HelpCommand implements Command {

    @Override
    public void execute(String chatId, String text, Integer messageId) {
        String reply = ConfigLoader.getConfig().messages.help_reply
                .replace("%BotName%", Bot.getInstance().getBotUsername());
        Bot.getInstance().sendFormattedMessage(chatId, reply);
    }
}
