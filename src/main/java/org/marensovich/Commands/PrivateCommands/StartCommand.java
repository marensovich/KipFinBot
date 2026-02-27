package org.marensovich.Commands.PrivateCommands;

import org.marensovich.Bot;
import org.marensovich.Command;
import org.marensovich.Utility.Config.ConfigLoader;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

public class StartCommand implements Command {

    @Override
    public void execute(String chatId, String text, Integer messageId) {
        String reply = ConfigLoader.getConfig().messages.start_reply;
        InputFile inputFile = new InputFile();
        File file = new File("src/main/resources/bot_logo.jpg");
        inputFile.setMedia(file);
        Bot.getInstance().sendPhoto(chatId, inputFile, reply);
    }

}
