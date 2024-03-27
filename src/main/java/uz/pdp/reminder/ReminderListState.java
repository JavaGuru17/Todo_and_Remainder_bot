package uz.pdp.reminder;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.common.enums.BackStatesEnum;
import uz.pdp.common.enums.CallbackData;
import uz.pdp.common.module.Reminder;
import uz.pdp.common.repo.UsersRepo;
import uz.pdp.common.util.InlineButtonUtil;
import uz.pdp.common.util.UpdateProcessor;
import uz.pdp.common.util.UtilLists;

import java.util.List;

public class ReminderListState {
    public static void process(Update update, TelegramLongPollingBot bot) {
        List<Reminder> list = UsersRepo.usersReminderMap.get(UpdateProcessor.chatId(update));
        if (list.isEmpty()) {
            try {
                bot.execute(AnswerCallbackQuery.builder()
                        .text("Reminders not created yet")
                        .callbackQueryId(update.getCallbackQuery().getId())
                        .showAlert(true)
                        .build()
                );
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else {
            UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.REMINDER_LIST);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                Reminder r = list.get(i);
                builder.append("\n").append(r.getNumber()).append(") *").append(r.getDescription()).append("*");
                builder.append("\n\nDate: ").append(r.getDateTime().getYear()).append(".\n").append(r.getDateTime().getDayOfMonth()).append(" - ").append(r.getDateTime().getMonth()).append("\n").append("Time :  ").append(r.getDateTime().getHour()).append(":").append(r.getDateTime().getMinute()).append(":").append(r.getDateTime().getSecond()).append("\n").append(r.getType());
                if (i != list.size() - 1) {
                    builder.append("\n-------------------------");
                }
            }
            try {
                bot.execute(EditMessageText.builder()
                        .chatId(UpdateProcessor.chatId(update))
                        .text(String.valueOf(builder))
                        .parseMode(ParseMode.MARKDOWN)
                        .messageId(UpdateProcessor.messageId(update))
                        .replyMarkup(InlineButtonUtil.newKeyboard(InlineButtonUtil.newCollection(
                                InlineButtonUtil.newRow(InlineButtonUtil.newButton("Delete", CallbackData.DELETE_REMINDER.getValue()),
                                        InlineButtonUtil.newButton("Delete all", CallbackData.DELETE_ALL_REMINDER.getValue())),
                                InlineButtonUtil.newRow(InlineButtonUtil.newButton("Back", CallbackData.BACK.getValue())))))
                        .build());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
