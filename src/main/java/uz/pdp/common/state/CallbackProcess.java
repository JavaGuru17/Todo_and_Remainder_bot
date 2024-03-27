package uz.pdp.common.state;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pdp.common.enums.BackStatesEnum;
import uz.pdp.common.enums.CallbackData;
import uz.pdp.common.enums.StateForReminderState;
import uz.pdp.common.module.Reminder;
import uz.pdp.common.module.ToDo;
import uz.pdp.common.util.UpdateProcessor;
import uz.pdp.common.util.UtilLists;
import uz.pdp.reminder.*;
import uz.pdp.common.repo.UsersRepo;
import uz.pdp.todo.state.*;

import java.util.List;

public class CallbackProcess {
    public static void process(Update update, TelegramLongPollingBot bot) {
        final String data = update.getCallbackQuery().getData();
        CallbackData callback = CallbackData.of(data);
        switch (callback){
            case TODO_BUTTON -> TodoMenuState.process(update, bot);
            case TODO_LIST, DELETE_ALL_CANCEL -> ToDoListState.process(update, bot);
            case NEW_TODO_LIST -> NewToDoListState.process(update, bot);
            case BACK -> BackState.process(update, bot);
            case DELETE_TODO -> {
                List<ToDo> list = UsersRepo.usersTodoMap.get(UpdateProcessor.chatId(update));
                if (list.size() == 1) {
                    ConfirmDeleteAllState.process(update, bot);
                } else {
                    DeleteState.sendMessage(update, bot);
                }
            }
            case DELETE_ALL_TODO -> ConfirmDeleteAllState.process(update, bot);
            case DELETE_ALL_REMINDER -> ReminderConfirmDeleteAllState.process(update, bot);
            case DELETE_ALL_CANCEL_REMINDER, REMINDER_LIST -> ReminderListState.process(update, bot);
            case DELETE_REMINDER -> {
                List<Reminder> list = UsersRepo.usersReminderMap.get(UpdateProcessor.chatId(update));
                if (list.size() == 1) {
                    ReminderConfirmDeleteAllState.process(update, bot);
                } else {
                    ReminderDeleteState.sendMessage(update, bot);
                }
            }
            case DELETE_ALL_CONFIRM -> DeleteAllState.process(update, bot);
            case DELETE_ALL_CONFIRM_REMINDER -> ReminderDeleteAllState.process(update, bot);

            case REMINDER_BUTTON -> ReminderButtonState.process(update, bot);
            case NEW_REMINDER -> NewReminderState.processWithEdition(update, bot);
            case DAILY -> {
                UtilLists.reminderStateMap.put(UpdateProcessor.chatId(update), StateForReminderState.WRITE_DAILY_REMINDER);
                UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.WRITE_REMINDER);
                ReminderState.sendDescriptionMessage(update, bot);
            }
            case WEEKLY -> {
                UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.WRITE_REMINDER);
                UtilLists.reminderStateMap.put(UpdateProcessor.chatId(update), StateForReminderState.WRITE_WEEKLY_REMINDER);
                ReminderState.sendDescriptionMessage(update, bot);
            }
            case MONTHLY -> {
                UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.WRITE_REMINDER);
                UtilLists.reminderStateMap.put(UpdateProcessor.chatId(update), StateForReminderState.WRITE_MONTHLY_REMINDER);
                ReminderState.sendDescriptionMessage(update, bot);
            }
            case ONCE -> OnceState.sendDescriptionMessage(update, bot);
            case EVERY_CERTAIN_TIME -> CertainTimeState.sendDescriptionMessage(update, bot);
        }
    }

}
