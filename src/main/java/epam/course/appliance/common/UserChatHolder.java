package epam.course.appliance.common;

import epam.course.appliance.dto.ChatMessageDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class holds chat messages for each user.
 */
public class UserChatHolder {

    private static final Map<String, List<ChatMessageDto>> USER_CHAT_MEMORY = new HashMap<>();

    private UserChatHolder() {
    }

    public static Map<String, List<ChatMessageDto>> getUserChatMemory() {
        return USER_CHAT_MEMORY;
    }

    public static void addMessage(String username, ChatMessageDto message) {
        USER_CHAT_MEMORY.computeIfAbsent(username, k -> new ArrayList<>()).add(message);
    }

    public static List<ChatMessageDto> getMessages(String username) {
        return USER_CHAT_MEMORY.computeIfAbsent(username, k -> new ArrayList<>());
    }

    public static void clear(String username) {
        USER_CHAT_MEMORY.remove(username);
    }
}

