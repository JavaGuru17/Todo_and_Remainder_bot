package uz.pdp.todo.state;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.common.enums.BackStatesEnum;
import uz.pdp.common.enums.TypeTextState;
import uz.pdp.common.module.ToDo;
import uz.pdp.common.repo.ToDoRepo;
import uz.pdp.common.repo.UsersRepo;
import uz.pdp.common.util.Markup;
import uz.pdp.common.util.UpdateProcessor;
import uz.pdp.common.util.UtilLists;

import java.util.List;

public class DeleteState {
    public static void process(Update update, TelegramLongPollingBot bot) {
        UtilLists.backStateMap.put(UpdateProcessor.chatId(update), BackStatesEnum.DELETE_TODO);
        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            if (text.matches("^\\d+$")) {
                List<ToDo> list = UsersRepo.usersTodoMap.get(UpdateProcessor.chatId(update));
                int index = Integer.parseInt(text) - 1;
                if (!(index < 0 || index > list.size())) {
                    list.remove(index);
                    UsersRepo.usersTodoMap.put(UpdateProcessor.chatId(update), list);
                    ToDoRepo.delete(update);
                    if (index != list.size()) {
                        for (int i = index; i <= list.size() - 1; i++) {
                            ToDo toDo = list.get(i);
                            toDo.setNumber(i + 1L);
                            list.set(i, toDo);
                        }
                    }
                    try {
                        bot.execute(SendMessage.builder()
                                .text("*You successfully delete one todo*")
                                .parseMode(ParseMode.MARKDOWN)
                                .chatId(UpdateProcessor.chatId(update))
                                .replyMarkup(Markup.TODO_MENU_MARKUP)
                                .build()
                        );
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_ID_FOR_DELETE);
                    try {
                        bot.execute(SendMessage.builder()
                                .text("*Index not found*")
                                .replyMarkup(Markup.BACK_MARKUP)
                                .chatId(UpdateProcessor.chatId(update))
                                .parseMode(ParseMode.MARKDOWN)
                                .build()

                        );
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_ID_FOR_DELETE);
                try {
                    bot.execute(SendMessage.builder()
                            .text("*Invalid format*")
                            .replyMarkup(Markup.BACK_MARKUP)
                            .chatId(UpdateProcessor.chatId(update))
                            .parseMode(ParseMode.MARKDOWN)
                            .build()
                    );
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void sendMessage(Update update, TelegramLongPollingBot bot) {
        UtilLists.enterStateMap.put(UpdateProcessor.chatId(update), TypeTextState.WRITE_ID_FOR_DELETE);
        try {
            bot.execute(SendMessage.builder()
                    .text("*Enter number of todo:*")
                    .parseMode(ParseMode.MARKDOWN)
                    .chatId(UpdateProcessor.chatId(update))
                    .replyMarkup(Markup.BACK_MARKUP)
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}
