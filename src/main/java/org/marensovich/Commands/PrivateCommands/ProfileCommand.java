package org.marensovich.Commands.PrivateCommands;

import org.marensovich.Bot;
import org.marensovich.Command;
import org.marensovich.EljurDatabase;
import org.json.JSONObject;
import org.marensovich.Utility.Config.ConfigLoader;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ProfileCommand implements Command {

    private final Bot bot;
    private final EljurDatabase eljurDatabase;
    private static final String PROFILE_API_URL = "http://199.83.103.127:25038/api/telegram/profile/getProfileInfo?userID=";

    public ProfileCommand(Bot bot, EljurDatabase eljurDatabase) {
        this.bot = bot;
        this.eljurDatabase = eljurDatabase;
    }

    @Override
    public void execute(String chatId, String text, Integer messageId) {
        if (!eljurDatabase.isUserAuthorized(Long.parseLong(chatId))) {
            String reply = ConfigLoader.getConfig().messages.error_access_to_command_auth
                    .replace("%BotName%", Bot.getInstance().getBotUsername())
                    .replace("%command%", "/profile");
            bot.sendMessage(chatId, reply);
            return;
        }
        Integer userID = eljurDatabase.getUserID(Long.parseLong(chatId));
        if (userID == null) {
            bot.sendMessage(chatId, "Вы не зарегистрированы. Пройдите авторизацию с помощью /auth");
            return;
        }

        String profileData = getProfileInfo(userID);
        if (profileData == null) {
            bot.sendMessage(chatId, ConfigLoader.getConfig().messages.error);
            return;
        }
        JSONObject json = new JSONObject(profileData);
        String message = formatProfileMessage(json);
        bot.sendFormattedMessage(chatId, message);
    }

    private String getProfileInfo(Integer userID) {
        try {
            URL url = new URL(PROFILE_API_URL + userID);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                return null;
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String formatProfileMessage(JSONObject json) {
        return String.format(
                        "<b>👤 Профиль</b>\n\n" +
                        "<b>👤 ФИО:</b> %s\n" +
                        "<b>🏢 Должность:</b> %s\n" +
                        "<b>📧 Почта:</b> %s\n" +
                        "<b>📞 Телефон:</b> %s\n" +
                        "<b>🆔 Telegram ID:</b> %s\n" +
                        "<b>🎓 Группа:</b> %s\n" +
                        "<b>📊 Средний балл:</b> %s\n\n" +
                        "<b>🔔 Уведомления:</b>\n" +
                        "<b>📩 Уведомления о сообщениях: </b>\n%s\n\n" +
                        "<b>🏠 Уведомления о домашнем задании: </b>\n%s\n\n" +
                        "<b>📈 Уведомления о оценках: </b>\n%s\n\n" +
                        "<b>📰 Уведомления о новостях: </b>\n%s",
                json.optString("fullname", "Не указано"),
                json.optString("post", "Не указано"),
                json.optString("mail", "Не указано"),
                json.optString("phone", "Не указано"),
                json.optString("TelegramID", "Не указано"),
                json.optString("group", "Не указано"),
                json.optString("avg_score", "Не указано"),
                json.optBoolean("notificationMessages") ? "✅ Включены" : "❌ Выключены",
                json.optBoolean("notificationHomework") ? "✅ Включены" : "❌ Выключены",
                json.optBoolean("notificationScore") ? "✅ Включены" : "❌ Выключены",
                json.optBoolean("notificationNews") ? "✅ Включены" : "❌ Выключены"
        );
    }

}