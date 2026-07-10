package epam.course.appliance.dto;

/**
 * DTO for user chat data
 * Stores username and conversation id
 */
public class UserChatData {

    private String username;
    private String conversationId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
