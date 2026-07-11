package epam.course.appliance.brain.service;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

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

    public ChatResponse chat(String request, String conversationId) {
        UserMessage userMessage = UserMessage.builder()
                .text(request)
                .build();
        return chatClient.prompt()
                .messages(userMessage)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, conversationId))
                .call()
                .chatResponse();
    }
}
