package uz.pdp.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pdp.common.enums.TypeTextState;
import uz.pdp.common.repo.UsersRepo;
import uz.pdp.common.state.CallbackProcess;
import uz.pdp.common.state.GeneralController;
import uz.pdp.common.state.WriteState;
import uz.pdp.common.util.UpdateProcessor;
import uz.pdp.common.util.UtilLists;

public class TodoList_bot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {

        System.out.println(UsersRepo.usersReminderMap);
        System.out.println(UtilLists.scheduledExecutorServiceMap);
        if (update.hasCallbackQuery()) {
            CallbackProcess.process(update, this);
        } else if (update.hasMessage()) {
            if (update.getMessage().isCommand()) {
                GeneralController.handle(update, this);
            }
        }
        if (!UtilLists.enterStateMap.get(UpdateProcessor.chatId(update)).equals(TypeTextState.DEFAULT)) {
            WriteState.writeState(update, this);
        }
    }

    public TodoList_bot() {
        super("6672313454:AAE8anKxVccrT_cgz5Ohhw4D8Br5CPlaN48");
    }

    @Override
    public String getBotUsername() {
        return "TodoList17_bot";
    }
}
