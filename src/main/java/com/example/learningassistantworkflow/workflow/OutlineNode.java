package com.example.learningassistantworkflow.workflow;

import com.example.learningassistantworkflow.assistant.DocumentWorkflowAssistant;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OutlineNode implements NodeAction<LearningAssistantState> {

    private final DocumentWorkflowAssistant assistant;

    public OutlineNode(DocumentWorkflowAssistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public Map<String, Object> apply(LearningAssistantState state) {
        System.out.println("OutlineNode executing. Topic: " + state.topic());
        String outline = assistant.createOutline(state.topic());
        System.out.println("Generated outline:\n" + outline);
        return Map.of(
                LearningAssistantState.OUTLINE_KEY, outline,
                LearningAssistantState.MESSAGES_KEY, "outline generated"
        );
    }
}
