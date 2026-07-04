package com.example.learningassistantworkflow.workflow;

import com.example.learningassistantworkflow.assistant.DocumentWorkflowAssistant;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReviseNode implements NodeAction<LearningAssistantState> {

    private final DocumentWorkflowAssistant assistant;

    public ReviseNode(DocumentWorkflowAssistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public Map<String, Object> apply(LearningAssistantState state) {
        int nextRevision = state.revisionCount() + 1;
        System.out.println("ReviseNode executing. Next revision: " + nextRevision);
        String revisedDraft = assistant.reviseDraft(
                "topic=" + state.topic()
                        + ";revisionCount=" + nextRevision
                        + ";draft=" + state.draft()
                        + ";feedback=" + state.feedback()
        );
        System.out.println("Revised draft:\n" + revisedDraft);
        return Map.of(
                LearningAssistantState.DRAFT_KEY, revisedDraft,
                LearningAssistantState.REVISION_COUNT_KEY, nextRevision,
                LearningAssistantState.MESSAGES_KEY, "draft revised " + nextRevision
        );
    }
}
