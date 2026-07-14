package epam.course.appliance.brain.config;

import epam.course.appliance.brain.tool.FetchApplianceDataTool;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicCacheOptions;
import org.springframework.ai.anthropic.api.AnthropicCacheStrategy;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AssistanceConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 ChatOptions anthropicChatOptions,
                                 MessageChatMemoryAdvisor messageChatMemoryAdvisor,
                                 FetchApplianceDataTool applianceDataTool,
                                 @Value("classpath:system-prompt.xml") Resource resource) {
        return builder
                .defaultSystem(resource)
                .defaultAdvisors(messageChatMemoryAdvisor)
                .defaultOptions(anthropicChatOptions)
                .defaultTools(applianceDataTool)
                .build();
    }

    @Bean
    public ChatClient ragChatClient(ChatClient.Builder builder,
                                    ChatOptions anthropicChatOptions,
                                    MessageChatMemoryAdvisor messageChatMemoryAdvisor,
                                    QuestionAnswerAdvisor questionAnswerAdvisor,
                                    @Value("classpath:system-prompt.xml") Resource resource) {
        return builder
                .defaultSystem(resource)
                .defaultAdvisors(messageChatMemoryAdvisor, questionAnswerAdvisor)
                .defaultOptions(anthropicChatOptions)
                .build();
    }

    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory assistantChatMemory) {
        return MessageChatMemoryAdvisor.builder(assistantChatMemory).build();
    }

    @Bean
    public QuestionAnswerAdvisor questionAnswerAdvisor(ChromaVectorStore chromaVectorStore) {
        SearchRequest searchRequest = SearchRequest.builder()
                .topK(3)
                .similarityThreshold(0.5)
                .build();
        return QuestionAnswerAdvisor.builder(chromaVectorStore)
                .searchRequest(searchRequest)
                .build();
    }

    @Bean
    public ChatOptions anthropicChatOptions() {
        return AnthropicChatOptions.builder()
                .temperature(0.6)
                .cacheOptions(AnthropicCacheOptions.builder()
                        .strategy(AnthropicCacheStrategy.SYSTEM_AND_TOOLS)
                        .build())
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
