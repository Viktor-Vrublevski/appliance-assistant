package epam.course.appliance.brain.service;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import java.util.Map;
import java.util.UUID;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class AssistantService {

    private final ChatClient applianceChatClient;

    public AssistantService(ChatClient applianceChatClient) {
        this.applianceChatClient = applianceChatClient;
    }

    public ChatResponse chat(String request, String conversationId) {
        UserMessage userMessage = UserMessage.builder()
                .text(request)
                .build();
        return applianceChatClient.prompt()
                .messages(userMessage)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, conversationId))
                .call()
                .chatResponse();
    }
}
