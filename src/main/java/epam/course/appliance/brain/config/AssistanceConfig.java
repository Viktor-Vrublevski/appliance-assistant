package epam.course.appliance.brain.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicCacheOptions;
import org.springframework.ai.anthropic.api.AnthropicCacheStrategy;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AssistanceConfig {

    @Bean
    public ChatClient applianceChatClient(ChatClient.Builder builder,
                                          ChatMemory assistantChatMemory,
                                          @Value("classpath:system-prompt.xml") Resource resource) throws IOException {
        String systemPrompt = resource.getContentAsString(StandardCharsets.UTF_8);
        AnthropicChatOptions chatOptions = AnthropicChatOptions.builder()
                .cacheOptions(AnthropicCacheOptions.builder()
                        .strategy(AnthropicCacheStrategy.SYSTEM_AND_TOOLS)
                        .build())
                .build();
        return builder
                .defaultSystem(systemPrompt)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(assistantChatMemory).build())
                .defaultOptions(chatOptions)
                .build();
    }

    @Bean
    public ChatMemory assistantChatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(15)
                .build();
    }
}
