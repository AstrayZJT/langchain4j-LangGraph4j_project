package com.example.learningassistantworkflow.workflow;

import com.example.learningassistantworkflow.assistant.DocumentWorkflowAssistant;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReviewNode implements NodeAction<LearningAssistantState> {

    private final DocumentWorkflowAssistant assistant;

    public ReviewNode(DocumentWorkflowAssistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public Map<String, Object> apply(LearningAssistantState state) {
        System.out.println("ReviewNode executing. revisionCount=" + state.revisionCount());
        String rawResult = assistant.reviewDraft(
                "topic=" + state.topic()
                        + ";revisionCount=" + state.revisionCount()
                        + ";draft=" + state.draft()
        );
        ReviewResult result = ReviewResult.parse(rawResult);
        System.out.println("Review score: " + result.score());
        System.out.println("Review feedback: " + result.feedback());
        return Map.of(
                LearningAssistantState.SCORE_KEY, result.score(),
                LearningAssistantState.FEEDBACK_KEY, result.feedback(),
                LearningAssistantState.MESSAGES_KEY, "reviewed with score " + result.score()
        );
    }
}
