package uz.pdp.reminder;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.common.repo.ReminderRepo;
import uz.pdp.common.repo.UsersRepo;
import uz.pdp.common.util.Markup;
import uz.pdp.common.util.UpdateProcessor;
import uz.pdp.common.util.UtilLists;

import java.util.ArrayList;

public class ReminderDeleteAllState {
    public static void process(Update update, TelegramLongPollingBot bot) {
        UsersRepo.usersReminderMap.get(UpdateProcessor.chatId(update)).clear();
        UtilLists.scheduledExecutorServiceMap.get(UpdateProcessor.chatId(update)).forEach(el->el.getSchedule().shutdown());
        UtilLists.scheduledExecutorServiceMap.put(UpdateProcessor.chatId(update),new ArrayList<>());

        ReminderRepo.deleteAll(update);
        try {
            bot.execute(EditMessageText.builder()
                    .text("*You successfully delete all list*")
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
