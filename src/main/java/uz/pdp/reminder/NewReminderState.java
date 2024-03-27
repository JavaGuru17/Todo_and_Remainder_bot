package uz.pdp.reminder;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.common.enums.BackStatesEnum;
import uz.pdp.common.enums.CallbackData;
import uz.pdp.common.enums.TypeTextState;
import uz.pdp.common.util.InlineButtonUtil;
import uz.pdp.common.util.UpdateProcessor;
import uz.pdp.common.util.UtilLists;

public class NewReminderState {
    public static void processWithEdition(Update update, TelegramLongPollingBot bot) {
        UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.DEFAULT);
        UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.NEW_REMAINDER_LIST);
        try {
            bot.execute(EditMessageText.builder()
                    .text("*Select reminder frequency*")
                    .parseMode(ParseMode.MARKDOWN)
                    .chatId(UpdateProcessor.chatId(update))
                    .messageId(UpdateProcessor.messageId(update))
                    .replyMarkup(
                            InlineButtonUtil.newKeyboard(
                                    InlineButtonUtil.newCollection(
                                            InlineButtonUtil.newRow(
                                                    InlineButtonUtil.newButton("Daily", CallbackData.DAILY.getValue()),
                                                    InlineButtonUtil.newButton("Weekly", CallbackData.WEEKLY.getValue()),
                                                    InlineButtonUtil.newButton("Monthly", CallbackData.MONTHLY.getValue())),
                                            InlineButtonUtil.newRow(
                                                    InlineButtonUtil.newButton("Once", CallbackData.ONCE.getValue()),
                                                    InlineButtonUtil.newButton("Every certain time", CallbackData.EVERY_CERTAIN_TIME.getValue())),
                                            InlineButtonUtil.newRow(InlineButtonUtil.newButton("Back", CallbackData.BACK.getValue())))))
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public static void process(Update update, TelegramLongPollingBot bot) {
        UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.NEW_REMAINDER_LIST);
        try {
            bot.execute(SendMessage.builder()
                    .text("*Select reminder frequency*")
                    .parseMode(ParseMode.MARKDOWN)
                    .chatId(UpdateProcessor.chatId(update))
                    .replyMarkup(
                            InlineButtonUtil.newKeyboard(
                                    InlineButtonUtil.newCollection(
                                            InlineButtonUtil.newRow(
                                                    InlineButtonUtil.newButton("Daily", CallbackData.DAILY.getValue()),
                                                    InlineButtonUtil.newButton("Weekly", CallbackData.WEEKLY.getValue()),
                                                    InlineButtonUtil.newButton("Monthly", CallbackData.MONTHLY.getValue())),
                                            InlineButtonUtil.newRow(
                                                    InlineButtonUtil.newButton("Once", CallbackData.ONCE.getValue()),
                                                    InlineButtonUtil.newButton("Every certain time", CallbackData.EVERY_CERTAIN_TIME.getValue())),
                                            InlineButtonUtil.newRow(InlineButtonUtil.newButton("Back", CallbackData.BACK.getValue())))))
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
