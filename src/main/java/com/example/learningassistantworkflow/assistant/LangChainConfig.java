package com.example.learningassistantworkflow.assistant;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfig {

    @Bean
    public ChatModel qwenChatModel(
            @Value("${learning-assistant.qwen.api-key}") String apiKey,
            @Value("${learning-assistant.qwen.base-url}") String baseUrl,
            @Value("${learning-assistant.qwen.model-name}") String modelName,
            @Value("${learning-assistant.qwen.temperature}") double temperature,
            @Value("${learning-assistant.qwen.max-tokens}") int maxTokens) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Please set DASHSCOPE_API_KEY before starting the application.");
        }

        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean
    public DocumentWorkflowAssistant documentWorkflowAssistant(ChatModel qwenChatModel) {
        return AiServices.create(DocumentWorkflowAssistant.class, qwenChatModel);
    }
}
