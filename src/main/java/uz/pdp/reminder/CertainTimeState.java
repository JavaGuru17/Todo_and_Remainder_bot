package uz.pdp.reminder;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
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

public class CertainTimeState {
    public static void descriptionProcess(Update update, TelegramLongPollingBot bot) {
        if (update.hasMessage()) {
            Reminder reminder = new Reminder();
            reminder.setDescription(update.getMessage().getText());
            reminder.setNumber(ReminderRepo.getId(update));
            reminder.setType(ReminderState.getType(UtilLists.reminderStateMap.get(UpdateProcessor.chatId(update))));
            reminder.setUserId(UpdateProcessor.chatId(update));
            ReminderState.reminders.put(UpdateProcessor.chatId(update), reminder);
            sendDateMessage(update, bot);
        }
    }

    public static void dateProcess(Update update, TelegramLongPollingBot bot) {
        if (update.hasMessage()) {
            if (update.getMessage().getText().matches("^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[01]) (0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]) \\d{2}$")) {
                String[] s = update.getMessage().getText().split(" ", 3);
                LocalDateTime resultDateTime = LocalDate.parse(s[0]).atTime(LocalTime.parse(s[1] + ":00"));
                if (resultDateTime.isBefore(LocalDateTime.now())) {
                    try {
                        bot.execute(SendMessage.builder()
                                .text("that date was long ago\nPlease reenter")
                                .chatId(UpdateProcessor.chatId(update))
                                .replyMarkup(Markup.BACK_MARKUP)
                                .build()
                        );
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_CERTAIN_DATE);
                } else {
                    Reminder reminder = ReminderState.reminders.remove(UpdateProcessor.chatId(update));
                    reminder.setDateTime(resultDateTime);
                    UsersRepo.usersReminderMap.get(UpdateProcessor.chatId(update)).add(reminder);
                    int delay = Integer.parseInt(s[2]) * 86400000;
                    List<Schedule> list = UtilLists.scheduledExecutorServiceMap.get(UpdateProcessor.chatId(update));
                    if (list == null) list = new ArrayList<>();
                    list.add(new Schedule(reminder.getNumber(), Executors.newScheduledThreadPool(1)));
                    UtilLists.scheduledExecutorServiceMap.put(UpdateProcessor.chatId(update), list);
                    UtilLists.scheduledExecutorServiceMap.get(UpdateProcessor.chatId(update)).forEach(e -> {
                        if (e.getNumber().equals(reminder.getNumber())) {
                            e.getSchedule().scheduleAtFixedRate(task(update, bot, reminder, delay), LocalDateTime.now().until(reminder.getDateTime(), ChronoUnit.MILLIS), delay, TimeUnit.MILLISECONDS);
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
                UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_CERTAIN_DATE);
            }
        }
    }

    public static void sendDescriptionMessage(Update update, TelegramLongPollingBot bot) {
        UtilLists.reminderStateMap.put(UpdateProcessor.chatId(update), StateForReminderState.WRITE_CERTAIN_REMINDER);
        UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.WRITE_CERTAIN_REMINDER);
        try {
            bot.execute(EditMessageText.builder()
                    .text("*Enter description:*")
                    .parseMode(ParseMode.MARKDOWN)
                    .replyMarkup(Markup.BACK_MARKUP)
                    .chatId(UpdateProcessor.chatId(update))
                    .messageId(UpdateProcessor.messageId(update))
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_CERTAIN_DESCRIPTION);
    }

    public static void sendDateMessage(Update update, TelegramLongPollingBot bot) {
        try {
            bot.execute(SendMessage.builder()
                    .text("""
                            Enter the date and in how many days it will be sent
                            Example (YYYY-MM-DD<Space>HH:MMSpace>20) (2023-04-12 12:23 14)
                            if you enter like this, it will send a reminder every 14 days from 2023-04-12 12:23""")
                    .replyMarkup(Markup.BACK_MARKUP)
                    .chatId(UpdateProcessor.chatId(update))
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_CERTAIN_DATE);
    }

    private static Runnable task(Update update, TelegramLongPollingBot bot, Reminder reminder, int day) {
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
            reminder.setDateTime(reminder.getDateTime().plusDays(day));
            UsersRepo.usersReminderMap.get(UpdateProcessor.chatId(update)).add(reminder);
        };
    }
}
