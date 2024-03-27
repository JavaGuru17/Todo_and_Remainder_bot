package uz.pdp.common.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.pdp.common.enums.CallbackData;

public class Markup {
    public static InlineKeyboardMarkup TODO_MENU_MARKUP = InlineButtonUtil.newKeyboard(InlineButtonUtil.newCollection(
            InlineButtonUtil.newRow(InlineButtonUtil.newButton("ToDo List", CallbackData.TODO_LIST.getValue()),
                    InlineButtonUtil.newButton("Create New", CallbackData.NEW_TODO_LIST.getValue())),
            InlineButtonUtil.newRow(InlineButtonUtil.newButton("Back", CallbackData.BACK.getValue()))));
    public static InlineKeyboardMarkup REMINDER_MENU_MARKUP = InlineButtonUtil.newKeyboard(
            InlineButtonUtil.newCollection(InlineButtonUtil.newRow(
                            InlineButtonUtil.newButton("Reminder List", CallbackData.REMINDER_LIST.getValue()),
                            InlineButtonUtil.newButton("New Reminder List", CallbackData.NEW_REMINDER.getValue()))
                    ,InlineButtonUtil.newRow(InlineButtonUtil.newButton("Back",CallbackData.BACK.getValue()))));

    public static InlineKeyboardMarkup BACK_MARKUP = InlineButtonUtil.newKeyboard(
            InlineButtonUtil.newCollection(
                    InlineButtonUtil.newRow(
                            InlineButtonUtil.newButton("Back",CallbackData.BACK.getValue()))));
}
