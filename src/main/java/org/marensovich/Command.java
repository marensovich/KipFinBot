package org.marensovich;

public interface Command {
    void execute(String chatId, String text, Integer messageId);
}
