package org.marensovich.Commands.PrivateCommands;

import org.marensovich.Bot;
import org.marensovich.Command;
import org.marensovich.EljurDatabase;
import org.marensovich.Utility.Config.ConfigLoader;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

public class SupportCommand implements Command {

    private final Bot bot;
    private final EljurDatabase eljurDatabase;

    public SupportCommand(Bot bot, EljurDatabase eljurDatabase) {
        this.bot = bot;
        this.eljurDatabase = eljurDatabase;
    }

    @Override
    public void execute(String chatId, String text, Integer messageId) {
        //if (!eljurDatabase.isUserAuthorized(Long.parseLong(chatId))) {
        //    String reply = ConfigLoader.getConfig().messages.error_access_to_command_auth
        //                    .replace("%BotName%", Bot.getInstance().getBotUsername())
        //                    .replace("%command%", "/support");
        //    bot.sendMessage(chatId, reply);
        //    return;
        //}

        sendMainSettingsKeyboard(chatId);

    }

    private void sendMainSettingsKeyboard(String chatId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        String message = ConfigLoader.getConfig().messages.support_reply
                .replace("%linkText%", ConfigLoader.getConfig().messages.support_linkText)
                .replace("%link%", ConfigLoader.getConfig().settings.link_info_support);
        rows.add(Collections.singletonList(InlineKeyboardButton.builder()
                .text(ConfigLoader.getConfig().messages.support_buttonText)
                .url(ConfigLoader.getConfig().settings.link_info_support)
                .build()));

        keyboardMarkup.setKeyboard(rows);
        bot.sendMessageWithKeyboard(Long.valueOf(chatId), message, keyboardMarkup);
    }

    public void handleCallback(String chatId, String callbackData, Integer messageId) {
        long chatIdLong = Long.parseLong(chatId);
        if (callbackData.equals("support_openChat")) {

        }
    }

}
