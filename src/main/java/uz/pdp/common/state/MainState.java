package uz.pdp.common.state;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.common.enums.CallbackData;
import uz.pdp.common.util.InlineButtonUtil;
import uz.pdp.common.util.UpdateProcessor;

public class MainState {
    public static void process(Update update, TelegramLongPollingBot bot) {
        try {
            bot.execute(SendMessage.builder()
                    .text("*Choose Todo or Reminder*")
                    .parseMode(ParseMode.MARKDOWN)
                    .chatId(UpdateProcessor.chatId(update))
                    .replyMarkup(InlineButtonUtil.newKeyboard(InlineButtonUtil.newCollection(
                            InlineButtonUtil.newRow(InlineButtonUtil.newButton("ToDo", CallbackData.TODO_BUTTON.getValue()),
                                    InlineButtonUtil.newButton("Reminder", CallbackData.REMINDER_BUTTON.getValue()))
                    )))
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void processWithEdition(Update update, TelegramLongPollingBot bot) {
        try {
            bot.execute(EditMessageText.builder()
                    .text("*Choose Todo or Reminder*")
                    .parseMode(ParseMode.MARKDOWN)
                    .messageId(UpdateProcessor.messageId(update))
                    .chatId(UpdateProcessor.chatId(update))
                    .replyMarkup(InlineButtonUtil.newKeyboard(InlineButtonUtil.newCollection(
                            InlineButtonUtil.newRow(InlineButtonUtil.newButton("ToDo", CallbackData.TODO_BUTTON.getValue()),
                                    InlineButtonUtil.newButton("Reminder", CallbackData.REMINDER_BUTTON.getValue()))
                    )))
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
