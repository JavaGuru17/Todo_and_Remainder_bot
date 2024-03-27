package uz.pdp.common.util;

import uz.pdp.common.enums.BackStatesEnum;
import uz.pdp.common.enums.StateForReminderState;
import uz.pdp.common.enums.TypeTextState;
import uz.pdp.common.module.Schedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilLists {
    public static Map<Long, TypeTextState> enterStateMap = new HashMap<>();
    public static Map<Long, BackStatesEnum> backStateMap = new HashMap<>();
    public static Map<Long, StateForReminderState> reminderStateMap = new HashMap<>();
    public static Map<Long, List<Schedule>> scheduledExecutorServiceMap = new HashMap<>();
}
