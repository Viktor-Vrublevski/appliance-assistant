package epam.course.appliance.brain.tool;

import static epam.course.appliance.ApplianceConstant.KEY_CONVERSATION_ID;
import static epam.course.appliance.ApplianceConstant.KEY_USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import epam.course.appliance.entity.Appliance;
import epam.course.appliance.service.ApplianceService;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.AdvisorSpec;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.ToolContext;

@ExtendWith(MockitoExtension.class)
class FetchApplianceDataToolTest {

    @Mock
    private ChatClient ragChatClient;

    @Mock
    private ApplianceService applianceService;

    @Mock
    private ToolContext toolContext;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    private FetchApplianceDataTool fetchApplianceDataTool;

    @BeforeEach
    void setUp() {
        fetchApplianceDataTool = new FetchApplianceDataTool(ragChatClient, applianceService);
    }

    @Test
    void testFetchApplianceData_Success() {
        String username = "testUser";
        String request = "List all my kitchen appliances";
        String conversationId = "conv-123";
        Appliance appliance = new Appliance();
        appliance.setCategory("Refrigerator");
        List<Appliance> appliances = List.of(appliance);
        ChatResponse expectedResponse = mock(ChatResponse.class);
        when(toolContext.getContext()).thenReturn(Map.of(
                KEY_CONVERSATION_ID, conversationId,
                KEY_USERNAME, username));
        when(applianceService.getApplianceByUsername(username)).thenReturn(appliances);
        when(ragChatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.messages(any(UserMessage.class))).thenReturn(requestSpec);
        when(requestSpec.advisors(any(Consumer.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.chatResponse()).thenReturn(expectedResponse);

        ChatResponse actualResponse = fetchApplianceDataTool.fetchApplianceData(request, toolContext);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        ArgumentCaptor<UserMessage> messageCaptor = ArgumentCaptor.forClass(UserMessage.class);
        verify(requestSpec).messages(messageCaptor.capture());
        UserMessage capturedMessage = messageCaptor.getValue();
        String expectedContent = String.format("%s\nUser %s has the following appliances: %s",
                request, username, appliance);
        assertEquals(expectedContent, capturedMessage.getText());
        ArgumentCaptor<Consumer<AdvisorSpec>> advisorCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(requestSpec).advisors(advisorCaptor.capture());
        AdvisorSpec advisorSpec = mock(AdvisorSpec.class);
        advisorCaptor.getValue().accept(advisorSpec);
        verify(advisorSpec).param(CONVERSATION_ID, conversationId);
    }

    @Test
    void testFetchApplianceData_ApplianceNotFound() {
        String username = "unknownUser";
        String request = "Show my devices";
        when(applianceService.getApplianceByUsername(username)).thenReturn(null);
        when(toolContext.getContext()).thenReturn(Map.of(KEY_USERNAME, username));

        ChatResponse actualResponse = fetchApplianceDataTool.fetchApplianceData(request, toolContext);

        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getResults());
        assertEquals(1, actualResponse.getResults().size());
        String actualContent = actualResponse.getResults().getFirst().getOutput().getText();
        assertEquals("Appliance with username unknownUser not found", actualContent);
        verifyNoInteractions(ragChatClient);
    }
}