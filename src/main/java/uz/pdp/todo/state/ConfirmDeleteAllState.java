package uz.pdp.todo.state;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.common.enums.CallbackData;
import uz.pdp.common.util.InlineButtonUtil;
import uz.pdp.common.util.UpdateProcessor;

public class ConfirmDeleteAllState {
    public static void process(Update update, TelegramLongPollingBot bot) {
        try {
            bot.execute(EditMessageText.builder()
                    .text("*Are you about to delete your list?*")
                    .parseMode(ParseMode.MARKDOWN)
                    .chatId(UpdateProcessor.chatId(update))
                    .messageId(UpdateProcessor.messageId(update))
                    .replyMarkup(InlineButtonUtil.newKeyboard(
                            InlineButtonUtil.newCollection(InlineButtonUtil.newRow(
                                    InlineButtonUtil.newButton("Yes", CallbackData.DELETE_ALL_CONFIRM.getValue()),
                                    InlineButtonUtil.newButton("No", CallbackData.DELETE_ALL_CANCEL.getValue()))
                            ))
                    ).build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
