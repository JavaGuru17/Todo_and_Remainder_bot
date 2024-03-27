package uz.pdp.common.repo;

import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pdp.common.util.UpdateProcessor;

import java.util.HashMap;
import java.util.Map;

public class ReminderRepo {
    public static Map<Long, Long> idMap = new HashMap<>();
    public static long getId(Update update) {
        Long chatId = UpdateProcessor.chatId(update);
        if (!idMap.containsKey(chatId)) {
            idMap.put(chatId, 1L);
            return 1;
        }
        Long id = idMap.get(chatId) + 1;
        idMap.put(chatId, id);
        return idMap.get(chatId);
    }

    public static void delete(Update update) {
        Long chatId = UpdateProcessor.chatId(update);
        Long l = idMap.get(chatId) - 1;
        idMap.put(chatId,l);
    }
    public static void deleteAll(Update update) {
        Long chatId = UpdateProcessor.chatId(update);
        idMap.put(chatId, 0L);
    }
}
