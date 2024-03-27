package uz.pdp.todo.state;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.common.enums.BackStatesEnum;
import uz.pdp.common.enums.TypeTextState;
import uz.pdp.common.util.Markup;
import uz.pdp.common.util.UpdateProcessor;
import uz.pdp.common.util.UtilLists;

public class NewToDoListState {
    public static void process(Update update, TelegramLongPollingBot bot) {
        try {
            bot.execute(EditMessageText.builder()
                    .chatId(UpdateProcessor.chatId(update))
                    .messageId(UpdateProcessor.messageId(update))
                    .text("*Enter description:* ")
                    .parseMode(ParseMode.MARKDOWN)
                    .replyMarkup(Markup.BACK_MARKUP)
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_DESCRIPTION);
        UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.NEW_TODOLIST);
    }
}
