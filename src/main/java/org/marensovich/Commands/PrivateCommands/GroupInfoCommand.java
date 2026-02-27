package org.marensovich.Commands.PrivateCommands;

import org.marensovich.Bot;
import org.marensovich.Command;

public class GroupInfoCommand implements Command {

    @Override
    public void execute(String chatId, String text, Integer messageId) {
        String reply = "Вы отправили команду /group_info";
        Bot.getInstance().sendMessage(chatId, reply);
    }


}
