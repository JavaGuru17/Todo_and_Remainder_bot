package uz.pdp.common.state;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pdp.common.enums.TypeTextState;
import uz.pdp.common.util.UpdateProcessor;
import uz.pdp.common.util.UtilLists;
import uz.pdp.reminder.CertainTimeState;
import uz.pdp.reminder.OnceState;
import uz.pdp.reminder.ReminderDeleteState;
import uz.pdp.reminder.ReminderState;
import uz.pdp.todo.state.DeleteState;
import uz.pdp.todo.state.DescriptionState;

public class WriteState {
    public static void writeState(Update update, TelegramLongPollingBot bot) {
        TypeTextState state = UtilLists.enterStateMap.get(UpdateProcessor.chatId(update));
        switch (state){
            case WRITE_DESCRIPTION -> DescriptionState.process(update, bot);
            case WRITE_ID_FOR_DELETE -> DeleteState.process(update,bot);
            case WRITE_REMINDER_DESCRIPTION -> ReminderState.descriptionProcess(update,bot);
            case WRITE_REMINDER_TIME -> ReminderState.timeProcess(update, bot);
            case WRITE_ONCE_DATE -> OnceState.timeProcess(update, bot);
            case WRITE_ONCE_DESCRIPTION -> OnceState.descriptionProcess(update,bot);
            case WRITE_REMINDER_ID_FOR_DELETE -> ReminderDeleteState.process(update, bot);
            case WRITE_CERTAIN_DESCRIPTION -> CertainTimeState.descriptionProcess(update, bot);
            case WRITE_CERTAIN_DATE -> CertainTimeState.dateProcess(update, bot);
        }
    }
}
