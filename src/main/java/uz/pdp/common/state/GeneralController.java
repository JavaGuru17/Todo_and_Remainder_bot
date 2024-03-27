package uz.pdp.common.state;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.common.enums.BackStatesEnum;
import uz.pdp.common.enums.TypeTextState;
import uz.pdp.common.module.Reminder;
import uz.pdp.common.repo.UsersRepo;
import uz.pdp.common.util.UpdateProcessor;
import uz.pdp.common.util.UtilLists;

import java.util.LinkedList;
import java.util.List;

public class GeneralController {
    private static void setDefault(Update update){
        UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.DEFAULT);
        UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.DEFAULT);
    }
    public static void handle(Update update, TelegramLongPollingBot bot) {
        String text = update.getMessage().getText();
        if ("/start".equals(text)) {
            setDefault(update);
            if (!UsersRepo.usersTodoMap.containsKey(UpdateProcessor.chatId(update)) ) {
                UsersRepo.usersTodoMap.put(UpdateProcessor.chatId(update), new LinkedList<>());
            }
            if(!UsersRepo.usersReminderMap.containsKey(UpdateProcessor.chatId(update))){
                List<Reminder> reminders = new LinkedList<>();
                UsersRepo.usersReminderMap.put(UpdateProcessor.chatId(update), reminders);
            }
            MainState.process(update, bot);
        } else if ("/settings".equals(text)) {
            try {
                bot.execute(SendMessage.builder()
                        .chatId(UpdateProcessor.chatId(update))
                        .text("_Write ideas for what do you wanna change in settings in ToDo at this_ @IamNnnnnnn _admin_")
                        .parseMode(ParseMode.MARKDOWN)
                        .build());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if ("/help".equals(text)) {
            try {
                bot.execute(SendMessage.builder()
                        .chatId(UpdateProcessor.chatId(update))
                        .text("_If you encounter a problem or bug while using my bot, then write to me at this_ @IamNnnnnnn _admin_")
                        .parseMode(ParseMode.MARKDOWN)
                        .build());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
