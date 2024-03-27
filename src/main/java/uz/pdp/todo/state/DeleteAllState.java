package uz.pdp.todo.state;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.common.repo.ToDoRepo;
import uz.pdp.common.repo.UsersRepo;
import uz.pdp.common.util.Markup;
import uz.pdp.common.util.UpdateProcessor;

import java.util.LinkedList;

public class DeleteAllState {
    public static void process(Update update, TelegramLongPollingBot bot) {
        UsersRepo.usersTodoMap.put(UpdateProcessor.chatId(update), new LinkedList<>());
        ToDoRepo.deleteAll(update);
        try {
            bot.execute(EditMessageText.builder()
                    .text("*You successfully delete all list*")
                    .parseMode(ParseMode.MARKDOWN)
                    .chatId(UpdateProcessor.chatId(update))
                    .messageId(UpdateProcessor.messageId(update))
                    .replyMarkup(Markup.TODO_MENU_MARKUP)
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
