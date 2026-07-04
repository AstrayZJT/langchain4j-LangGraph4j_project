package com.example.learningassistantworkflow;

import com.example.learningassistantworkflow.workflow.LearningAssistantWorkflowService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LearningAssistantWorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningAssistantWorkflowApplication.class, args);
    }

    @Bean
    ApplicationRunner startupRunner(LearningAssistantWorkflowService workflowService) {
        return args -> workflowService.runDemo();
    }
}
