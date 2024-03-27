package uz.pdp.common.state;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pdp.common.enums.BackStatesEnum;
import uz.pdp.common.enums.TypeTextState;
import uz.pdp.common.util.UtilLists;
import uz.pdp.common.util.UpdateProcessor;
import uz.pdp.reminder.NewReminderState;
import uz.pdp.reminder.ReminderButtonState;
import uz.pdp.reminder.ReminderState;
import uz.pdp.todo.state.ToDoListState;
import uz.pdp.todo.state.TodoMenuState;

public class BackState {
    public static void process(Update update, TelegramLongPollingBot bot) {
        BackStatesEnum state = UtilLists.backStateMap.get(UpdateProcessor.chatId(update));
        switch (state){
            case TODO, REMINDER -> MainState.processWithEdition(update,bot);
            case TODOLIST, NEW_TODOLIST -> TodoMenuState.process(update,bot);
            case NEW_REMAINDER_LIST, REMINDER_LIST, DELETE_REMINDER -> ReminderButtonState.process(update, bot);
            case DELETE_TODO -> {
                UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.DEFAULT);
                ToDoListState.process(update, bot);
            }
            case WRITE_REMINDER, WRITE_ONCE_REMINDER,WRITE_CERTAIN_REMINDER -> {
                ReminderState.reminders.remove(UpdateProcessor.chatId(update));
                NewReminderState.processWithEdition(update, bot);
            }

        }
    }
}
