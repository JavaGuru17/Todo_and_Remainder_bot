package uz.pdp.reminder;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.common.enums.BackStatesEnum;
import uz.pdp.common.enums.StateForReminderState;
import uz.pdp.common.enums.TypeTextState;
import uz.pdp.common.module.Reminder;
import uz.pdp.common.module.Schedule;
import uz.pdp.common.repo.ReminderRepo;
import uz.pdp.common.repo.UsersRepo;
import uz.pdp.common.util.Markup;
import uz.pdp.common.util.UpdateProcessor;
import uz.pdp.common.util.UtilLists;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OnceState {
    private static Runnable task(Update update, TelegramLongPollingBot bot, Reminder reminder) {
        return () -> {
            try {
                bot.execute(SendMessage.builder()
                        .text("Reminder: " + reminder.getDescription())
                        .chatId(UpdateProcessor.chatId(update))
                        .build()
                );
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            UsersRepo.usersReminderMap.get(UpdateProcessor.chatId(update)).remove(reminder);
        };
    }

    public static void timeProcess(Update update, TelegramLongPollingBot bot) {
        if (update.hasMessage()) {
            if (update.getMessage().getText().matches("^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[01]) (0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$")) {
                String[] s = update.getMessage().getText().split(" ", 2);
                LocalDate currentDate = LocalDate.parse(s[0]);
                LocalTime desiredTime = LocalTime.parse(s[1] + ":00");
                LocalDateTime resultDateTime = currentDate.atTime(desiredTime);
                if (resultDateTime.isBefore(LocalDateTime.now())) {
                    try {
                        bot.execute(SendMessage.builder()
                                .text("that date was long ago\nPlease re enter")
                                .chatId(UpdateProcessor.chatId(update))
                                .replyMarkup(Markup.BACK_MARKUP)
                                .build()
                        );
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_ONCE_DATE);
                } else {
                    Reminder reminder = ReminderState.reminders.remove(UpdateProcessor.chatId(update));
                    reminder.setDateTime(resultDateTime);
                    UsersRepo.usersReminderMap.get(UpdateProcessor.chatId(update)).add(reminder);
                    List<Schedule> list = UtilLists.scheduledExecutorServiceMap.get(UpdateProcessor.chatId(update));
                    if (list == null) list = new ArrayList<>();
                    list.add(new Schedule(reminder.getNumber(), Executors.newScheduledThreadPool(1)));
                    UtilLists.scheduledExecutorServiceMap.put(UpdateProcessor.chatId(update), list);
                    UtilLists.scheduledExecutorServiceMap.get(UpdateProcessor.chatId(update)).forEach(e -> {
                        if (e.getNumber().equals(reminder.getNumber())) {
                            e.getSchedule().schedule(task(update, bot, reminder), LocalDateTime.now().until(reminder.getDateTime(), ChronoUnit.MILLIS), TimeUnit.MILLISECONDS);
                        }
                    });
                    NewReminderState.process(update, bot);
                    UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.DEFAULT);
                }
            } else {
                try {
                    bot.execute(SendMessage.builder()
                            .text("Invalid format")
                            .chatId(UpdateProcessor.chatId(update))
                            .replyMarkup(Markup.BACK_MARKUP)
                            .build()
                    );
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_ONCE_DATE);
            }
        }
    }

    public static void descriptionProcess(Update update, TelegramLongPollingBot bot) {
        if (update.hasMessage()) {
            Reminder reminder = new Reminder();
            reminder.setNumber(ReminderRepo.getId(update));
            reminder.setDescription(update.getMessage().getText());
            reminder.setType(ReminderState.getType(UtilLists.reminderStateMap.get(UpdateProcessor.chatId(update))));
            ReminderState.reminders.put(UpdateProcessor.chatId(update), reminder);
            sendTimeMessage(update, bot);
        }
    }

    public static void sendTimeMessage(Update update, TelegramLongPollingBot bot) {
        try {
            bot.execute(SendMessage.builder()
                    .chatId(UpdateProcessor.chatId(update))
                    .text("Enter date : Examples YYYY-MM-DD TT:MM (2023-09-20 10:30)")
                    .replyMarkup(Markup.BACK_MARKUP)
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_ONCE_DATE);
    }

    public static void sendDescriptionMessage(Update update, TelegramLongPollingBot bot) {
        UtilLists.reminderStateMap.put(UpdateProcessor.chatId(update), StateForReminderState.WRITE_ONCE_REMINDER);
        UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.WRITE_ONCE_REMINDER);
        try {
            bot.execute(EditMessageText.builder()
                    .messageId(UpdateProcessor.messageId(update))
                    .chatId(UpdateProcessor.chatId(update))
                    .text("Enter description")
                    .replyMarkup(Markup.BACK_MARKUP)
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_ONCE_DESCRIPTION);
    }

}
