package epam.course.appliance.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import epam.course.appliance.brain.service.AssistantService;
import epam.course.appliance.common.UserChatHolder;
import epam.course.appliance.common.UserContextHolder;
import epam.course.appliance.dto.ChatMessageDto;
import epam.course.appliance.dto.UserChatData;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AssistantService assistantService;

    @Mock
    private ChatMemory assistantChatMemory;

    @InjectMocks
    private ChatController chatController;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
        session = new MockHttpSession();

        UserChatData userChatData = new UserChatData();
        userChatData.setUsername("testuser");
        userChatData.setConversationId("conv-123");

        UserContextHolder.setUserChatData(session.getId(), userChatData);
        UserChatHolder.clear("testuser");
    }

    @AfterEach
    void tearDown() {
        UserContextHolder.removeUserChatData(session.getId());
        UserChatHolder.clear("testuser");
    }

    @Test
    void chatPageLoadsWithChatHistory() throws Exception {
        List<ChatMessageDto> chatHistory = new ArrayList<>();
        chatHistory.add(new ChatMessageDto("USER", "Hello", null));
        UserChatHolder.addMessage("testuser", new ChatMessageDto("USER", "Hello", null));

        mockMvc.perform(get("/chat-view")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("chat"))
                .andExpect(model().attributeExists("chatHistory"));
    }

    @Test
    void sendMessageAddsUserMessageAndAiResponse() throws Exception {
        ChatResponse mockResponse = mock(ChatResponse.class);
        Generation mockGeneration = mock(Generation.class);
        when(mockResponse.getResult()).thenReturn(mockGeneration);
        when(mockGeneration.getOutput()).thenReturn(mock(org.springframework.ai.chat.messages.AssistantMessage.class));
        when(mockGeneration.getOutput().getText()).thenReturn("AI response");
        when(assistantService.chat("Hello", "conv-123", "testuser")).thenReturn(mockResponse);

        mockMvc.perform(post("/chat/send")
                        .param("message", "Hello")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chat-view"));

        verify(assistantService).chat("Hello", "conv-123", "testuser");
        List<ChatMessageDto> messages = UserChatHolder.getMessages("testuser");
        org.junit.jupiter.api.Assertions.assertEquals(2, messages.size());
        org.junit.jupiter.api.Assertions.assertEquals("USER", messages.get(0).getSender());
        org.junit.jupiter.api.Assertions.assertEquals("Hello", messages.get(0).getContent());
        org.junit.jupiter.api.Assertions.assertEquals("AI", messages.get(1).getSender());
        org.junit.jupiter.api.Assertions.assertEquals("AI response", messages.get(1).getContent());
    }

    @Test
    void sendMessageRedirectsWhenMessageIsEmpty() throws Exception {
        mockMvc.perform(post("/chat/send")
                        .param("message", "")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chat-view"));

        verify(assistantService, never()).chat(anyString(), anyString(), anyString());
    }

    @Test
    void sendMessageReturnsBadRequestWhenMessageParameterIsMissing() throws Exception {
        mockMvc.perform(post("/chat/send")
                        .session(session))
                .andExpect(status().isBadRequest());

        verify(assistantService, never()).chat(anyString(), anyString(), anyString());
    }

    @Test
    void sendMessageRedirectsWhenMessageIsWhitespace() throws Exception {
        mockMvc.perform(post("/chat/send")
                        .param("message", "   ")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chat-view"));

        verify(assistantService, never()).chat(anyString(), anyString(), anyString());
    }

    @Test
    void clearChatClearsMessagesAndMemory() throws Exception {
        UserChatHolder.addMessage("testuser", new ChatMessageDto("USER", "Test", null));

        mockMvc.perform(post("/chat/clear")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chat-view"));

        verify(assistantChatMemory).clear("conv-123");
        org.junit.jupiter.api.Assertions.assertEquals(0, UserChatHolder.getMessages("testuser").size());
    }

    @Test
    void chatPageHandlesEmptyHistory() throws Exception {
        mockMvc.perform(get("/chat-view")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("chat"))
                .andExpect(model().attributeExists("chatHistory"));

        List<ChatMessageDto> messages = UserChatHolder.getMessages("testuser");
        org.junit.jupiter.api.Assertions.assertEquals(0, messages.size());
    }
}