package org.marensovich.Commands.PrivateCommands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.marensovich.Bot;
import org.marensovich.Command;
import org.marensovich.EljurDatabase;
import org.json.JSONObject;
import org.marensovich.Utility.Config.ConfigLoader;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class SettingsCommand implements Command {
    private final Bot bot;
    private final EljurDatabase eljurDatabase;
    private final Map<Long, Map<String, Boolean>> userNotificationSettings = new HashMap<>();
    private static final String SETTINGS_API_URL = "http://199.83.103.127:25038/api/telegram/settings/setNotificationSettings?userID=";
    private static final String PROFILE_API_URL = "http://199.83.103.127:25038/api/telegram/profile/getProfileInfo";

    public SettingsCommand(Bot bot, EljurDatabase eljurDatabase) {
        this.bot = bot;
        this.eljurDatabase = eljurDatabase;
    }

    @Override
    public void execute(String chatId, String text, Integer messageId) {
        if (!eljurDatabase.isUserAuthorized(Long.parseLong(chatId))) {
            String reply = ConfigLoader.getConfig().messages.error_access_to_command_auth
                    .replace("%BotName%", Bot.getInstance().getBotUsername())
                    .replace("%command%", "/settings");
            bot.sendMessage(chatId, reply);
            return;
        }
        long chatIdLong = Long.parseLong(chatId);
        if (!userNotificationSettings.containsKey(chatIdLong)) {
            Map<String, Boolean> serverSettings = getNotificationSettings(chatIdLong);
            if (serverSettings != null) {
                userNotificationSettings.put(chatIdLong, serverSettings);
            } else {
                userNotificationSettings.put(chatIdLong, new HashMap<>());
            }
        }

        sendMainSettingsKeyboard(chatId);
    }

    public void handleCallback(String chatId, String callbackData, Integer messageId) {
        long chatIdLong = Long.parseLong(chatId);
        userNotificationSettings.putIfAbsent(chatIdLong, new HashMap<>());
        Map<String, Boolean> settings = userNotificationSettings.get(chatIdLong);

        if (callbackData.equals("settings_open_notifications")) {
            sendNotificationSettingsKeyboard(chatId, messageId);
        } else if (callbackData.startsWith("settings_toggle_")) {
            String type = callbackData.replace("settings_toggle_", "");
            boolean newState = !settings.getOrDefault(type, false);
            settings.put(type, newState);
            sendNotificationSettingsKeyboard(chatId, messageId);
        } else if (callbackData.equals("settings_save")) {
            saveNotificationSettings(chatIdLong);
        }
    }

    private void sendMainSettingsKeyboard(String chatId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> settingsRow = new ArrayList<>();
        settingsRow.add(InlineKeyboardButton.builder()
                .text("⚙️ Уведомления")
                .callbackData("settings_open_notifications")
                .build());
        rows.add(settingsRow);

        keyboardMarkup.setKeyboard(rows);
        bot.sendMessageWithKeyboard(Long.valueOf(chatId), "Настройки:", keyboardMarkup);
    }

    private void sendNotificationSettingsKeyboard(String chatId, int messageId) {
        long chatIdLong = Long.parseLong(chatId);
        Map<String, Boolean> settings = userNotificationSettings.getOrDefault(chatIdLong, new HashMap<>());

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(createNotificationButton("📩 Сообщения", "messages", settings.getOrDefault("messages", false)));
        rows.add(createNotificationButton("🏠 Домашнее задание", "homework", settings.getOrDefault("homework", false)));
        rows.add(createNotificationButton("📈 Оценки", "scores", settings.getOrDefault("scores", false)));
        rows.add(createNotificationButton("📰 Новости", "news", settings.getOrDefault("news", false)));

        List<InlineKeyboardButton> saveRow = new ArrayList<>();
        saveRow.add(InlineKeyboardButton.builder()
                .text("💾 Сохранить")
                .callbackData("settings_save")
                .build());
        rows.add(saveRow);

        keyboardMarkup.setKeyboard(rows);

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText("Настройки уведомлений:");
        editMessage.setReplyMarkup(keyboardMarkup);

        try {
            bot.execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private List<InlineKeyboardButton> createNotificationButton(String text, String type, boolean isEnabled) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text(text + " " + (isEnabled ? "✅" : "❌"))
                .callbackData("settings_toggle_" + type)
                .build());
        return row;
    }

    private void saveNotificationSettings(long chatId) {
        Map<String, Boolean> settings = userNotificationSettings.get(chatId);
        if (settings == null) return;
        JSONObject settingsJson = new JSONObject();
        settingsJson.put("userID", eljurDatabase.getUserID(chatId));
        settingsJson.put("notificationMessages", settings.getOrDefault("messages", false));
        settingsJson.put("notificationHomework", settings.getOrDefault("homework", false));
        settingsJson.put("notificationScore", settings.getOrDefault("scores", false));
        settingsJson.put("notificationNews", settings.getOrDefault("news", false));

        String response = sendSettingsToServer(chatId, settingsJson);
        bot.sendMessage(String.valueOf(chatId), response != null ? "Настройки сохранены" : "Ошибка сохранения!");
    }

    private String sendSettingsToServer(long chatId, JSONObject settingsJson) {
        try {
            int userID = eljurDatabase.getUserID(chatId);

            URL url = new URL(SETTINGS_API_URL + userID);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = settingsJson.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() != 200) {
                bot.sendMessage(String.valueOf(chatId), "Произошла ошибка при обработке запроса, попробуйте позже или обратитесь к поддержке");
                return null;
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Boolean> getNotificationSettings(long userID) {
        try {
            if (userID <= 0) {
                System.out.println("Ошибка: некорректный userID");
                return null;
            }

            String apiUrl = String.format("%s?userID=%d", PROFILE_API_URL, eljurDatabase.getUserID(userID));
            URL url = new URL(apiUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

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


            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.toString());

            Map<String, Boolean> notificationSettings = new HashMap<>();
            notificationSettings.put("notificationMessages", jsonNode.path("notificationMessages").asBoolean(false));
            notificationSettings.put("notificationHomework", jsonNode.path("notificationHomework").asBoolean(false));
            notificationSettings.put("notificationScore", jsonNode.path("notificationScore").asBoolean(false));
            notificationSettings.put("notificationNews", jsonNode.path("notificationNews").asBoolean(false));

            return notificationSettings;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}