package epam.course.appliance.dto;

import java.time.LocalDateTime;

public class ChatMessageDto {

    private final String sender;
    private final String content;
    private final LocalDateTime timestamp;

    public ChatMessageDto(String sender, String content, LocalDateTime timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
