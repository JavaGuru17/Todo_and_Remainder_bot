package uz.pdp.common.repo;

import uz.pdp.common.module.Reminder;
import uz.pdp.common.module.ToDo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersRepo {
    public static Map<Long, List<ToDo>> usersTodoMap = new HashMap<>();
    public static Map<Long, List<Reminder>> usersReminderMap = new HashMap<>();
}