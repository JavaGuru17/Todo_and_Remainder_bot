package uz.pdp.reminder;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.common.enums.BackStatesEnum;
import uz.pdp.common.enums.TypeTextState;
import uz.pdp.common.module.Reminder;
import uz.pdp.common.module.Schedule;
import uz.pdp.common.repo.ReminderRepo;
import uz.pdp.common.repo.UsersRepo;
import uz.pdp.common.util.Markup;
import uz.pdp.common.util.UpdateProcessor;
import uz.pdp.common.util.UtilLists;

import java.util.List;

public class ReminderDeleteState {
    public static void process(Update update, TelegramLongPollingBot bot) {
        UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.DELETE_REMINDER);
        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            if (text.matches("^\\d+$")) {
                List<Reminder> list = UsersRepo.usersReminderMap.get(UpdateProcessor.chatId(update));
                List<Schedule> schedules = UtilLists.scheduledExecutorServiceMap.get(UpdateProcessor.chatId(update));
                int index = Integer.parseInt(text) - 1;
                if (!(index < 0 || index+1 > list.size())) {
                    list.remove(index);
                    schedules.get(index).getSchedule().shutdown();
                    schedules.remove(index);
                    UtilLists.scheduledExecutorServiceMap.put(UpdateProcessor.chatId(update),schedules);
                    UsersRepo.usersReminderMap.put(UpdateProcessor.chatId(update), list);

                    ReminderRepo.delete(update);
                    if (index != list.size()) {
                        for (int i = index; i <= list.size() - 1; i++) {
                            list.get(i).setNumber(i+1L);

                        }
                        for (int i = index; i <= schedules.size() - 1; i++) {
                            schedules.get(i).setNumber(i+1L);
                        }
                    }
                    try {
                        bot.execute(SendMessage.builder()
                                .text("*You successfully delete one todo*")
                                .parseMode(ParseMode.MARKDOWN)
                                .chatId(UpdateProcessor.chatId(update))
                                .replyMarkup(Markup.REMINDER_MENU_MARKUP)
                                .build()
                        );
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.DEFAULT);

                } else {
                    UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_REMINDER_ID_FOR_DELETE);
                    try {
                        bot.execute(SendMessage.builder()
                                .text("*Index not found*")
                                .replyMarkup(Markup.BACK_MARKUP)
                                .chatId(UpdateProcessor.chatId(update))
                                .parseMode(ParseMode.MARKDOWN)
                                .build()

                        );
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_REMINDER_ID_FOR_DELETE);
                try {
                    bot.execute(SendMessage.builder()
                            .text("*Invalid format*")
                            .replyMarkup(Markup.BACK_MARKUP)
                            .chatId(UpdateProcessor.chatId(update))
                            .parseMode(ParseMode.MARKDOWN)
                            .build()
                    );
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void sendMessage(Update update, TelegramLongPollingBot bot) {
        UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_REMINDER_ID_FOR_DELETE);
        try {
            bot.execute(SendMessage.builder()
                    .text("*Enter number of reminder:*")
                    .parseMode(ParseMode.MARKDOWN)
                    .chatId(UpdateProcessor.chatId(update))
                    .replyMarkup(Markup.BACK_MARKUP)
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}
