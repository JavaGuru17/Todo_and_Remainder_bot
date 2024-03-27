package uz.pdp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter

public enum BackStatesEnum {
    DEFAULT,
    TODO,
    REMINDER,
    TODOLIST,
    NEW_TODOLIST,
    NEW_REMAINDER_LIST,
    REMINDER_LIST,
    DELETE_TODO,
    DELETE_REMINDER,
    WRITE_REMINDER,
    WRITE_ONCE_REMINDER,
    WRITE_CERTAIN_REMINDER
}
