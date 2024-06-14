package com.ai.langchainllm.config;

import com.ai.langchainllm.WorkaholicsAgent;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Scanner;

@Configuration
@EnableConfigurationProperties(LocalAiProperties.class)
public class AiConfig {

    @Bean
    @ConditionalOnProperty("langchain4j.open-ai.chat-model.base-url")
    ChatLanguageModel openAiChatModel(LocalAiProperties properties) {
        final ChatModelProperties chatModelProperties = properties.getChatModel();
        return LocalAiChatModel.builder()
                .baseUrl(chatModelProperties.getBaseUrl())
                .modelName(chatModelProperties.getModelName())
                .temperature(chatModelProperties.getTemperature())
                .logRequests(true) // TODO: configure also using properties
                .logResponses(true)
                .build();
    }

    @Bean
    ApplicationRunner interactiveChatRunner(WorkaholicsAgent agent) {
        return args -> {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("User: ");
                String userMessage = scanner.nextLine();

                if ("exit".equalsIgnoreCase(userMessage)) {
                    break;
                }

                String agentMessage = agent.chat(userMessage);
                System.out.println("Agent: " + agentMessage);
            }

            scanner.close();
        };
    }

    @Bean
    public WorkaholicsAgent workaholicsAgent(ChatLanguageModel chatLanguageModel,
                                             ContentRetriever contentRetriever) {
        return AiServices.builder(WorkaholicsAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .contentRetriever(contentRetriever)
                .build();
    }

    @Bean
    AllMiniLmL6V2EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    EmbeddingStore embeddingStore(AllMiniLmL6V2EmbeddingModel embeddingModel, ResourceLoader resourceLoader) throws IOException {
        return new InMemoryEmbeddingStore();
    }

    @Bean
    ContentRetriever contentRetriever(EmbeddingStore embeddingStore, AllMiniLmL6V2EmbeddingModel embeddingModel) {

        // You will need to adjust these parameters to find the optimal setting, which will depend on two main factors:
        // - The nature of your data
        // - The embedding model you are using
        int maxResults = 1;
        double minScore = 0.6;

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
    }
}