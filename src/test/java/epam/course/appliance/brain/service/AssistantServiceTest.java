package epam.course.appliance.brain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.AdvisorSpec;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;

@ExtendWith(MockitoExtension.class)
class AssistantServiceTest {

    @Mock
    private ChatClient applianceChatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    @Mock
    private ChatClient.AdvisorSpec advisorSpec;

    @Mock
    private ChatResponse expectedChatResponse;

    @InjectMocks
    private AssistantService assistantService;

    @Captor
    private ArgumentCaptor<Consumer<AdvisorSpec>> advisorSpecCaptor;

    @BeforeEach
    void setUp() {
        doReturn(requestSpec).when(applianceChatClient).prompt();
        doReturn(requestSpec).when(requestSpec).messages(any(UserMessage.class));
        doReturn(requestSpec).when(requestSpec).advisors(any(Consumer.class));
        doReturn(responseSpec).when(requestSpec).call();
        doReturn(expectedChatResponse).when(responseSpec).chatResponse();
    }

    @Test
    void shouldSuccessfullyPassMessageAndConversationIdToChatClient() {
        String userRequest = "Hello, turn on the oven.";
        String conversationId = "test-session-123";

        ChatResponse actualResponse = assistantService.chat(userRequest, conversationId);

        assertNotNull(actualResponse);
        assertEquals(expectedChatResponse, actualResponse);
        ArgumentCaptor<UserMessage> messageCaptor = ArgumentCaptor.forClass(UserMessage.class);
        verify(requestSpec).messages(messageCaptor.capture());
        assertEquals(userRequest, messageCaptor.getValue().getText());
        verify(requestSpec).advisors(advisorSpecCaptor.capture());
        Consumer<AdvisorSpec> capturedConsumer = advisorSpecCaptor.getValue();
        capturedConsumer.accept(advisorSpec);
        verify(advisorSpec).param(CONVERSATION_ID, conversationId);
    }
}
