package uz.pdp.reminder;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ReminderState {
    public static Map<Long, Reminder> reminders = new HashMap<>();

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
            System.out.println(Thread.currentThread().getId());
            StateForReminderState state = UtilLists.reminderStateMap.get(UpdateProcessor.chatId(update));
            switch (state) {
                case WRITE_DAILY_REMINDER -> reminder.setDateTime(reminder.getDateTime().plusDays(1));
                case WRITE_WEEKLY_REMINDER -> reminder.setDateTime(reminder.getDateTime().plusWeeks(1));
                case WRITE_MONTHLY_REMINDER -> reminder.setDateTime(reminder.getDateTime().plusMonths(1));
            }
            List<Reminder> reminders = UsersRepo.usersReminderMap.get(UpdateProcessor.chatId(update));
            UsersRepo.usersReminderMap.put(UpdateProcessor.chatId(update), reminders);
            reminders.stream().filter(r -> reminder.getId().equals(r.getId())).findFirst()
                    .ifPresentOrElse(r -> reminders.set(reminders.indexOf(r), reminder), () -> reminders.add(reminder));
            UsersRepo.usersReminderMap.put(UpdateProcessor.chatId(update), reminders);
        };
    }

    public static void descriptionProcess(Update update, TelegramLongPollingBot bot) {
        if (update.hasMessage()) {
            Reminder reminder = new Reminder();
            reminder.setUserId(UpdateProcessor.chatId(update));
            reminder.setNumber(ReminderRepo.getId(update));
            reminder.setType(getType(UtilLists.reminderStateMap.get(UpdateProcessor.chatId(update))));
            reminder.setDescription(update.getMessage().getText());
            reminders.put(UpdateProcessor.chatId(update), reminder);
            sendTimeMessage(update, bot);
        }
    }

    public static void timeProcess(Update update, TelegramLongPollingBot bot) {
        if (update.hasMessage()) {
            if (update.getMessage().getText().matches("^(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$")) {
                Reminder reminder = reminders.get(UpdateProcessor.chatId(update));
                LocalDate currentDate = LocalDate.now();
                LocalTime desiredTime = LocalTime.parse(update.getMessage().getText() + ":00");
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
                    UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_REMINDER_TIME);
                } else {
                    reminder.setDateTime(resultDateTime);
                    long initialDelay = LocalDateTime.now().until(reminder.getDateTime(), ChronoUnit.MILLIS);
                    long period = calculate(UtilLists.reminderStateMap.get(UpdateProcessor.chatId(update)));
                    System.out.println("id " + Thread.currentThread().getId());
                    List<Schedule> list = UtilLists.scheduledExecutorServiceMap.get(UpdateProcessor.chatId(update));
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(new Schedule(reminder.getNumber(), Executors.newScheduledThreadPool(1)));
                    UtilLists.scheduledExecutorServiceMap.put(UpdateProcessor.chatId(update), list);
                    UtilLists.scheduledExecutorServiceMap.get(UpdateProcessor.chatId(update)).forEach(e -> {
                        if (e.getNumber().equals(reminder.getNumber())) {
                            e.getSchedule().scheduleAtFixedRate(task(update, bot, reminder), initialDelay, period, TimeUnit.MILLISECONDS);
                        }
                    });
                    UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.DEFAULT);
                    UsersRepo.usersReminderMap.get(UpdateProcessor.chatId(update)).add(reminder);
                    reminders.remove(UpdateProcessor.chatId(update));
                    NewReminderState.process(update, bot);
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
                UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_REMINDER_TIME);
            }
        }
    }

    public static void sendDescriptionMessage(Update update, TelegramLongPollingBot bot) {
        UsersRepo.usersReminderMap.computeIfAbsent(UpdateProcessor.chatId(update), k -> new ArrayList<>());
        UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_REMINDER_DESCRIPTION);
        try {
            bot.execute(EditMessageText.builder()
                    .text("Enter description")
                    .replyMarkup(Markup.BACK_MARKUP)
                    .messageId(UpdateProcessor.messageId(update))
                    .chatId(UpdateProcessor.chatId(update))
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendTimeMessage(Update update, TelegramLongPollingBot bot) {
        UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_REMINDER_TIME);
        try {
            bot.execute(SendMessage.builder()
                    .text("Enter time examples (14:00), (00:14), (04:02)")
                    .replyMarkup(Markup.BACK_MARKUP)
                    .chatId(UpdateProcessor.chatId(update))
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static Long calculate(StateForReminderState state) {
        return switch (state) {
            case WRITE_DAILY_REMINDER -> 60000L;
            case WRITE_WEEKLY_REMINDER -> 604800000L;
            case WRITE_MONTHLY_REMINDER -> 2629056000L;
            default -> null;
        };
    }

    public static String getType(StateForReminderState state) {
        return switch (state) {
            case WRITE_DAILY_REMINDER -> "Daily";
            case WRITE_ONCE_REMINDER -> "Once";
            case WRITE_WEEKLY_REMINDER -> "Weekly";
            case WRITE_MONTHLY_REMINDER -> "Monthly";
            case WRITE_CERTAIN_REMINDER -> "Certain";
        };
    }
}
