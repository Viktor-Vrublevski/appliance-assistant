package epam.course.appliance.brain.service;

import static epam.course.appliance.ApplianceConstant.KEY_CONVERSATION_ID;
import static epam.course.appliance.ApplianceConstant.KEY_USERNAME;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class AssistantService {

    private final ChatClient chatClient;

    public AssistantService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public ChatResponse chat(String request, String conversationId, String username) {
        UserMessage userMessage = UserMessage.builder()
                .text(request)
                .build();
        return chatClient.prompt()
                .messages(userMessage)
                .toolContext(Map.of(
                        KEY_CONVERSATION_ID, conversationId,
                        KEY_USERNAME, username))
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, conversationId))
                .call()
                .chatResponse();
    }
}
