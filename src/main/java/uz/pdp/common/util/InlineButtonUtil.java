package uz.pdp.common.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class InlineButtonUtil {
    public static InlineKeyboardButton newButton(String text, String callbackData) {
        InlineKeyboardButton.InlineKeyboardButtonBuilder button = InlineKeyboardButton.builder().text(text).callbackData(callbackData);
        return button.build();
    }
    public static List<InlineKeyboardButton> newRow(InlineKeyboardButton... rows) {
        return new LinkedList<>(List.of(rows));
    }

    @SafeVarargs
    public static List<List<InlineKeyboardButton>> newCollection(List<InlineKeyboardButton>... collection) {
        return new LinkedList<>(Arrays.asList(collection));
    }
    public static InlineKeyboardMarkup newKeyboard(List<List<InlineKeyboardButton>> collection) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(collection);
        return keyboardMarkup;
    }
}
