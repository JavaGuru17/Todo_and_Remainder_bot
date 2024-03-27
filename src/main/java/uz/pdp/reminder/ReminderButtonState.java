package uz.pdp.reminder;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.common.enums.BackStatesEnum;
import uz.pdp.common.util.Markup;
import uz.pdp.common.util.UpdateProcessor;
import uz.pdp.common.util.UtilLists;

public class ReminderButtonState {
    public static void process(Update update, TelegramLongPollingBot bot){
        UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.REMINDER);
        try {
            bot.execute(EditMessageText.builder()
                    .text("*Look at your list or create new one or back*")
                    .parseMode(ParseMode.MARKDOWN)
                    .chatId(UpdateProcessor.chatId(update))
                    .messageId(UpdateProcessor.messageId(update))
                    .replyMarkup(Markup.REMINDER_MENU_MARKUP)
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
