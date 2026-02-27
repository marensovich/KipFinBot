package org.marensovich.Commands.GroupCommands;

import org.marensovich.Bot;
import org.marensovich.Command;
import org.marensovich.EljurDatabase;

public class GroupInfoCommand implements Command {

    private final Bot bot;
    private final EljurDatabase eljurDatabase;

    public GroupInfoCommand(Bot bot, EljurDatabase eljurDatabase) {
        this.bot = bot;
        this.eljurDatabase = eljurDatabase;
    }

    @Override
    public void execute(String chatId, String text, Integer messageId) {
        String reply = "Информация о группе: здесь размещены важные анонсы.";
        bot.sendMessage(String.valueOf(chatId), reply);
    }

}
