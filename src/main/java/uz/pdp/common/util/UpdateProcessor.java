package uz.pdp.common.util;

import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateProcessor {
    public static Long chatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        throw new IllegalArgumentException("Chat ID not found.");
    }
    public static Integer messageId(Update update){
        if (update.hasMessage()) {
            return update.getMessage().getMessageId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getMessageId();
        }
        throw new IllegalArgumentException("Message ID not found.");
    }
}
