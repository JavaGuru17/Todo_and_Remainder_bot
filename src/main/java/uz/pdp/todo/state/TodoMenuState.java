package uz.pdp.todo.state;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.common.enums.BackStatesEnum;
import uz.pdp.common.util.Markup;
import uz.pdp.common.util.UtilLists;
import uz.pdp.common.util.UpdateProcessor;

public class TodoMenuState {
    public static void process(Update update, TelegramLongPollingBot bot) {
        UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.TODO);
        try {
            bot.execute(EditMessageText.builder()
                    .chatId(UpdateProcessor.chatId(update))
                    .messageId(UpdateProcessor.messageId(update))
                    .text("*Look at list or make new one*")
                    .parseMode(ParseMode.MARKDOWN)
                    .replyMarkup(Markup.TODO_MENU_MARKUP)
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
