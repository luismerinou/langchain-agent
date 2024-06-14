package com.ai.langchainllm.config;

import com.ai.langchainllm.WorkaholicsAgent;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public WorkaholicsAgent customerSupportAgent(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(WorkaholicsAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .build();
    }
}