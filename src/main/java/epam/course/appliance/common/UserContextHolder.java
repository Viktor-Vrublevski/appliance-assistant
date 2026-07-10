package epam.course.appliance.common;

import epam.course.appliance.dto.UserChatData;
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds user chat data for each session.
 */
public class UserContextHolder {

    private static final Map<String, UserChatData> USER_CHAT_DATA_MAP = new HashMap<>();

    private UserContextHolder() {
    }

    public static Map<String, UserChatData> getUserChatDataMap() {
        return USER_CHAT_DATA_MAP;
    }

    public static UserChatData getUserChatData(String sessionId) {
        return USER_CHAT_DATA_MAP.get(sessionId);
    }

    public static void setUserChatData(String sessionId, UserChatData userChatData) {
        USER_CHAT_DATA_MAP.put(sessionId, userChatData);
    }

    public static void removeUserChatData(String sessionId) {
        USER_CHAT_DATA_MAP.remove(sessionId);
    }
}
