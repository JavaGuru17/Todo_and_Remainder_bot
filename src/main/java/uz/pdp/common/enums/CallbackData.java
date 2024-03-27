package uz.pdp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor

public enum CallbackData {
    TODO_BUTTON("todo"),
    REMINDER_BUTTON("reminder"),
    TODO_LIST("todo_list"),
    NEW_TODO_LIST("new_todo_list"),
    BACK("back"),
    DELETE_TODO("delete_todo"),
    DELETE_ALL_TODO("delete_todo_all"),
    DELETE_REMINDER("delete_reminder"),
    DELETE_ALL_REMINDER("delete_reminder_all"),
    DELETE_ALL_CONFIRM("delete_all_confirm"),
    DELETE_ALL_CANCEL("delete_all_cancel"),
    DELETE_ALL_CONFIRM_REMINDER("delete_all_confirm_reminder"),
    DELETE_ALL_CANCEL_REMINDER("delete_all_cancel_reminder"),

    NEW_REMINDER("new_reminder"),
    REMINDER_LIST("reminder_list"),
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    ONCE("once"),
    EVERY_CERTAIN_TIME("every_certain_time");

    private final String value;
    private static final Map<String,CallbackData> map = Arrays.stream(CallbackData.values())
            .toList()
            .stream()
            .collect(Collectors.toMap(CallbackData::getValue,callbackData -> callbackData));
    public static CallbackData of(String data){
        CallbackData callbackData = map.get(data);
        if(callbackData != null) return callbackData;
        throw new IllegalArgumentException();
    }
}
