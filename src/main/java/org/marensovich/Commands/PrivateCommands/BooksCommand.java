package org.marensovich.Commands.PrivateCommands;

import org.marensovich.Bot;
import org.marensovich.Command;
import org.marensovich.EljurDatabase;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

public class BooksCommand implements Command {

    private final Bot bot;
    private final EljurDatabase eljurDatabase;
    public BooksCommand(Bot bot, EljurDatabase eljurDatabase) {
        this.bot = bot;
        this.eljurDatabase = eljurDatabase;
    }

    public void execute(String chatId, String text, Integer messageId) {
        if (text.startsWith("/books")) {
            if (!eljurDatabase.isUserAuthorized(Long.parseLong(chatId))) {
                String reply = "Уважаемый пользователь, вы не авторизованы в системе бота " + Bot.getInstance().getBotUsername() + "!\n\n" +
                        "Для получения доступа к команде /books и многим другим функциям бота необходимо пройти авторизацию через команду /auth";
                bot.sendMessage(chatId, reply);
                return;
            }
            sendCourseKeyboard(chatId);
        } else if (text.startsWith("course")) {
            if (!eljurDatabase.isUserAuthorized(Long.parseLong(chatId))) {
                String reply = "Уважаемый пользователь, вы не авторизованы в системе бота " + Bot.getInstance().getBotUsername() + "!\n\n" +
                        "Для получения доступа к команде /books и многим другим функциям бота необходимо пройти авторизацию через команду /auth";
                bot.sendMessage(chatId, reply);
                return;
            }
            handleCourseSelection(chatId, text, messageId);
        } else if (text.startsWith("book")) {
            if (!eljurDatabase.isUserAuthorized(Long.parseLong(chatId))) {
                String reply = "Уважаемый пользователь, вы не авторизованы в системе бота " + Bot.getInstance().getBotUsername() + "!\n\n" +
                        "Для получения доступа к команде /books и многим другим функциям бота необходимо пройти авторизацию через команду /auth";
                bot.sendMessage(chatId, reply);
                return;
            }
            handleSubjectSelection(chatId, text, messageId);
        } else if (text.startsWith("teacher")) {
            if (!eljurDatabase.isUserAuthorized(Long.parseLong(chatId))) {
                String reply = "Уважаемый пользователь, вы не авторизованы в системе бота " + Bot.getInstance().getBotUsername() + "!\n\n" +
                        "Для получения доступа к команде /books и многим другим функциям бота необходимо пройти авторизацию через команду /auth";
                bot.sendMessage(chatId, reply);
                return;
            }
            handleTeacherSelection(chatId, text, messageId);
        }
    }

    private void sendCourseKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите курс:");

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(createButtonRow("1 курс", "course1", "2 курс", "course2"));
        rows.add(createButtonRow("3 курс", "course3", "4 курс", "course4"));

        inlineKeyboard.setKeyboard(rows);
        message.setReplyMarkup(inlineKeyboard);

        sendMessage(chatId, message);
    }

    private void sendMessage(String chatId, SendMessage message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleCourseSelection(String chatId, String callbackData, Integer messageId) {
        String responseText = switch (callbackData) {
            case "course1" -> "Вы выбрали 1 курс. Выберите предмет:";
            case "course2" -> "Вы выбрали 2 курс. Выберите предмет:";
            case "course3" -> "Вы выбрали 3 курс. Выберите предмет:";
            case "course4" -> "Вы выбрали 4 курс. Выберите предмет:";
            default -> "Неизвестный курс.";
        };

        if (!responseText.equals("Неизвестный курс.")) {
            sendSubjectKeyboard(chatId, callbackData, messageId);
        } else {
            sendMessage(chatId, responseText, messageId);
        }
    }

    private void sendSubjectKeyboard(String chatId, String course, Integer messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText("Выберите предмет:");

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(createButtonRow("Математика", "book1_" + course, "Русский язык", "book2_" + course));
        rows.add(createButtonRow("Литература", "book3_" + course, "Обществознание", "book4_" + course));
        rows.add(createButtonRow("История", "book5_" + course, "Биология", "book6_" + course));
        rows.add(createButtonRow("Английский язык", "book7_" + course, "География", "book8_" + course));
        rows.add(createButtonRow("Химия", "book9_" + course, "Физика", "book10_" + course));
        rows.add(createButtonRow("Информатика", "book11_" + course, "ОБЗР", "book12_" + course));

        inlineKeyboard.setKeyboard(rows);
        message.setReplyMarkup(inlineKeyboard);

        sendMessage(chatId, message);
    }

    private void handleSubjectSelection(String chatId, String callbackData, Integer messageId) {
        String[] parts = callbackData.split("_");
        String book = parts[0];
        String course = parts[1];

        String subject = switch (book) {
            case "book1" -> "Математика";
            case "book2" -> "Русский язык";
            case "book3" -> "Литература";
            case "book4" -> "Обществознание";
            case "book5" -> "История";
            case "book6" -> "Биология";
            case "book7" -> "Английский язык";
            case "book8" -> "География";
            case "book9" -> "Химия";
            case "book10" -> "Физика";
            case "book11" -> "Информатика";
            case "book12" -> "ОБЗР";
            default -> null;
        };

        if (subject != null) {
            List<String> teachers = getTeachersBySubject(subject);

            if (teachers.isEmpty()) {
                sendMessage(chatId, "Преподаватели по предмету " + subject + " не найдены.", messageId);
            } else {
                sendTeacherKeyboard(chatId, "Выберите преподавателя по предмету " + subject + ":", teachers, course, messageId);
            }
        } else {
            sendMessage(chatId, "Неизвестный предмет.", messageId);
        }
    }

    private List<String> getTeachersBySubject(String subject) {
        List<String> teachers = new ArrayList<>();
        try {
            String encodedSubject = URLEncoder.encode(subject, StandardCharsets.UTF_8.toString());
            String urlString = "http://199.83.103.127:25038/api/teachers/getAllTeachersByObject?object=" + encodedSubject;

            System.out.println("Отправка запроса: " + urlString);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                errorReader.close();

                System.err.println("Ошибка сервера: " + errorResponse);
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                Gson gson = new Gson();
                teachers = gson.fromJson(response.toString(), List.class);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при запросе к серверу: " + e.getMessage());
            e.printStackTrace();
        }
        return teachers;
    }

    private void handleTeacherSelection(String chatId, String callbackData, Integer messageId) {
        String[] parts = callbackData.split("_");
        String teacher = parts[0];
        String course = parts[1];

        String responseText = switch (teacher) {
            case "teacher1" -> "Вы выбрали преподавателя " + teacher + " (курс: " + course + ")";
            case "teacher2" -> "Вы выбрали преподавателя " + teacher + " (курс: " + course + ")";
            case "teacher3" -> "Вы выбрали преподавателя " + teacher + " (курс: " + course + ")";
            case "teacher4" -> "Вы выбрали преподавателя " + teacher + " (курс: " + course + ")";
            default -> "Неизвестный преподаватель.";
        };

        sendMessage(chatId, responseText, messageId);
    }

    private void sendTeacherKeyboard(String chatId, String text, List<String> teachers, String course, Integer messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText(text);

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Создаем кнопки для каждого преподавателя
        for (int i = 0; i < teachers.size(); i++) {
            String teacher = teachers.get(i);
            rows.add(createSingleButtonRow(teacher, "teacher" + (i + 1) + "_" + course));
        }

        inlineKeyboard.setKeyboard(rows);
        message.setReplyMarkup(inlineKeyboard);

        sendMessage(chatId, message);
    }

    private List<InlineKeyboardButton> createSingleButtonRow(String text, String callbackData) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton(text, callbackData));
        return row;
    }

    private List<InlineKeyboardButton> createButtonRow(String text1, String callbackData1, String text2, String callbackData2) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton(text1, callbackData1));
        row.add(createButton(text2, callbackData2));
        return row;
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private void sendMessage(String chatId, EditMessageText message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String chatId, String text, Integer messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText(text);
        sendMessage(chatId, message);
    }
}