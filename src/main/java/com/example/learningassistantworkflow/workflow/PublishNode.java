package com.example.learningassistantworkflow.workflow;

import com.example.learningassistantworkflow.assistant.DocumentWorkflowAssistant;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PublishNode implements NodeAction<LearningAssistantState> {

    private final DocumentWorkflowAssistant assistant;

    public PublishNode(DocumentWorkflowAssistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public Map<String, Object> apply(LearningAssistantState state) {
        System.out.println("PublishNode executing. score=" + state.score());
        String finalDocument = assistant.publishDocument(
                "topic=" + state.topic()
                        + ";outline=" + state.outline()
                        + ";draft=" + state.draft()
                        + ";feedback=" + state.feedback()
                        + ";score=" + state.score()
        );
        System.out.println("Final document:\n" + finalDocument);
        return Map.of(
                LearningAssistantState.FINAL_DOCUMENT_KEY, finalDocument,
                LearningAssistantState.MESSAGES_KEY, "document published"
        );
    }
}
