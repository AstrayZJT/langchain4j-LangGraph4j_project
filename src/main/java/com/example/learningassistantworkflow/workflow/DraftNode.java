package com.example.learningassistantworkflow.workflow;

import com.example.learningassistantworkflow.assistant.DocumentWorkflowAssistant;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DraftNode implements NodeAction<LearningAssistantState> {

    private final DocumentWorkflowAssistant assistant;

    public DraftNode(DocumentWorkflowAssistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public Map<String, Object> apply(LearningAssistantState state) {
        System.out.println("DraftNode executing. Outline length: " + state.outline().length());
        String draft = assistant.writeDraft(state.outline());
        System.out.println("Generated draft:\n" + draft);
        return Map.of(
                LearningAssistantState.DRAFT_KEY, draft,
                LearningAssistantState.MESSAGES_KEY, "draft generated"
        );
    }
}
