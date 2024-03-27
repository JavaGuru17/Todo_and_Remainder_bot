package uz.pdp.todo.state;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.common.enums.TypeTextState;
import uz.pdp.common.module.ToDo;
import uz.pdp.common.repo.UsersRepo;
import uz.pdp.common.repo.ToDoRepo;
import uz.pdp.common.util.Markup;
import uz.pdp.common.util.UtilLists;
import uz.pdp.common.util.UpdateProcessor;

import java.time.LocalDate;
import java.util.List;

public class DescriptionState {
    public static void process(Update update, TelegramLongPollingBot bot) {
        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            List<ToDo> list = UsersRepo.usersTodoMap.get(UpdateProcessor.chatId(update));
            list.add(new ToDo(text,UpdateProcessor.chatId(update),LocalDate.now(),ToDoRepo.getId(update)));
            UsersRepo.usersTodoMap.put(UpdateProcessor.chatId(update), list);

            try {
                bot.execute(SendMessage.builder()
                        .chatId(UpdateProcessor.chatId(update))
                        .text("*You are successfully add one todo*")
                        .parseMode(ParseMode.MARKDOWN)
                        .replyMarkup(Markup.TODO_MENU_MARKUP)
                        .build()
                );
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.DEFAULT);
        }
    }
}
