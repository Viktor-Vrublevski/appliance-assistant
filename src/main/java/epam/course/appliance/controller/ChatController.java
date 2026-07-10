package epam.course.appliance.controller;

import epam.course.appliance.brain.service.AssistantService;
import epam.course.appliance.common.UserChatHolder;
import epam.course.appliance.common.UserContextHolder;
import epam.course.appliance.dto.ChatMessageDto;
import epam.course.appliance.dto.UserChatData;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {

    private final AssistantService assistantService;
    private final ChatMemory assistantChatMemory;

    public ChatController(AssistantService assistantService,
                          ChatMemory assistantChatMemory) {
        this.assistantService = assistantService;
        this.assistantChatMemory = assistantChatMemory;
    }

    @GetMapping("/chat-view")
    public String chatPage(
            HttpSession session,
            Model model) {
        UserChatData userChatData = UserContextHolder.getUserChatData(session.getId());
        List<ChatMessageDto> chatHistory = UserChatHolder.getMessages(userChatData.getUsername());
        model.addAttribute("chatHistory", chatHistory);
        return "chat";
    }

    @PostMapping("/chat/send")
    public String sendMessage(@RequestParam("message") String message,
                              HttpSession session,
                              Model model) {
        if (message == null || message.trim().isEmpty()) {
            return "redirect:/chat-view";
        }
        UserChatData userChatData = UserContextHolder.getUserChatData(session.getId());
        UserChatHolder.addMessage(userChatData.getUsername(),
                new ChatMessageDto("USER", message, LocalDateTime.now()));
        ChatResponse chatResponse = assistantService.chat(message, UserContextHolder
                .getUserChatData(session.getId()).getConversationId());
        String aiTextResponse = chatResponse.getResult().getOutput().getText();
        UserChatHolder.addMessage(userChatData.getUsername(),
                new ChatMessageDto("AI", aiTextResponse, LocalDateTime.now()));
        return "redirect:/chat-view";
    }

    @PostMapping("/chat/clear")
    public String clearChat(HttpSession session) {
        UserChatHolder.getMessages(UserContextHolder.getUserChatData(session.getId()).getUsername()).clear();
        assistantChatMemory.clear(UserContextHolder.getUserChatData(session.getId()).getConversationId());
        return "redirect:/chat-view";
    }
}
