package epam.course.appliance.brain.tool;

import static epam.course.appliance.ApplianceConstant.KEY_CONVERSATION_ID;
import static epam.course.appliance.ApplianceConstant.KEY_USERNAME;
import static epam.course.appliance.brain.tool.prompt.FetchAppliancePrompt.FETCH_APPLIANCE_PROMPT;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import epam.course.appliance.entity.Appliance;
import epam.course.appliance.service.ApplianceService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * Tool for fetching appliance data.
 * Fetches appliance data from the PostgreSQL database to pass to the LLM.
 * Uses RAG to retrieve relevant appliance data.
 */
@Component
public class FetchApplianceDataTool {
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchApplianceDataTool.class);

    private final ChatClient ragChatClient;
    private final ApplianceService applianceService;

    public FetchApplianceDataTool(ChatClient ragChatClient, ApplianceService applianceService) {
        this.ragChatClient = ragChatClient;
        this.applianceService = applianceService;
    }

    @Tool(name = "fetchApplianceDataTool", description = FETCH_APPLIANCE_PROMPT)
    public ChatResponse fetchApplianceData(String category, String request, ToolContext toolContext) {
        String username = (String) toolContext.getContext().get(KEY_USERNAME);
        List<Appliance> appliances = applianceService.getApplianceByUsername(username);
        if (appliances == null) {
            LOGGER.info("Appliance with username {} not found", username);
            return ChatResponse.builder().generations(List.of(new Generation(AssistantMessage.builder()
                            .content("Appliance with username " + username + " not found")
                            .build())))
                    .build();
        }
        LOGGER.info("Appliance with username {} found", username);
        appliances = appliances.stream().filter(appliance -> appliance.getCategory().equals(category))
                .toList();
        if (appliances.isEmpty()) {
            return ChatResponse.builder().generations(List.of(new Generation(AssistantMessage.builder()
                            .content("User " + username + " has no appliances in category " + category)
                            .build())))
                    .build();
        }
        UserMessage userMessage = UserMessage.builder()
                .text(String.format("%s\nUser %s has the following appliances: %s",
                        request, username, formatAppliancesAsText(appliances)))
                .build();
        return ragChatClient.prompt()
                .messages(userMessage)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, toolContext.getContext()
                        .get(KEY_CONVERSATION_ID)))
                .call()
                .chatResponse();
    }

    private String formatAppliancesAsText(List<Appliance> appliances) {
        return String.join(",", appliances.stream().map(Appliance::toString).toList());
    }
}
