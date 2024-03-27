package uz.pdp.todo.state;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import uz.pdp.common.enums.BackStatesEnum;
import uz.pdp.common.enums.CallbackData;
import uz.pdp.common.module.ToDo;
import uz.pdp.common.repo.UsersRepo;
import uz.pdp.common.util.InlineButtonUtil;
import uz.pdp.common.util.UtilLists;
import uz.pdp.common.util.UpdateProcessor;

import java.util.List;

public class ToDoListState {
    public static void process(Update update, TelegramLongPollingBot bot) {
        List<ToDo> list = UsersRepo.usersTodoMap.get(UpdateProcessor.chatId(update));
        if (list.isEmpty()) {
            try {
                bot.execute(AnswerCallbackQuery.builder()
                        .text("List not created yet")
                        .callbackQueryId(update.getCallbackQuery().getId())
                        .showAlert(true)
                        .build()
                );
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else {
            UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.TODOLIST);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                ToDo t = list.get(i);
                builder.append("\n").append(t.getNumber()).append(") *").append(t.getDescription()).append("*");
                builder.append("\n\nDate: ").append(t.getCreatedDate());
                if (i != list.size() - 1) {
                    builder.append("\n-------------------------");
                }
            }
            try {
                bot.execute(EditMessageText.builder()
                        .chatId(update.getCallbackQuery().getMessage().getChatId())
                        .text(String.valueOf(builder))
                        .parseMode(ParseMode.MARKDOWN)
                        .messageId(UpdateProcessor.messageId(update))
                        .replyMarkup(InlineButtonUtil.newKeyboard(InlineButtonUtil.newCollection(
                                InlineButtonUtil.newRow(InlineButtonUtil.newButton("Delete", CallbackData.DELETE_TODO.getValue()),
                                        InlineButtonUtil.newButton("Delete all", CallbackData.DELETE_ALL_TODO.getValue())),
                                InlineButtonUtil.newRow(InlineButtonUtil.newButton("Back", CallbackData.BACK.getValue())))))
                        .build());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
