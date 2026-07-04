package com.example.learningassistantworkflow.workflow;

import jakarta.annotation.PostConstruct;
import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.GraphInput;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.RunnableConfig;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.checkpoint.MemorySaver;
import org.bsc.langgraph4j.state.StateSnapshot;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Service
public class LearningAssistantWorkflowService {

    private static final String GRAPH_ID = "learning-assistant-workflow";
    private static final int PASS_SCORE = 85;
    private static final int MAX_REVISIONS = 2;

    private final OutlineNode outlineNode;
    private final DraftNode draftNode;
    private final ReviewNode reviewNode;
    private final ReviseNode reviseNode;
    private final PublishNode publishNode;
    private org.bsc.langgraph4j.CompiledGraph<LearningAssistantState> compiledGraph;

    public LearningAssistantWorkflowService(OutlineNode outlineNode,
                                           DraftNode draftNode,
                                           ReviewNode reviewNode,
                                           ReviseNode reviseNode,
                                           PublishNode publishNode) {
        this.outlineNode = outlineNode;
        this.draftNode = draftNode;
        this.reviewNode = reviewNode;
        this.reviseNode = reviseNode;
        this.publishNode = publishNode;
    }

    @PostConstruct
    void init() {
        try {
            var stateGraph = new StateGraph<>(LearningAssistantState.SCHEMA, LearningAssistantState::new)
                    .addNode("outline", node_async(outlineNode))
                    .addNode("draft", node_async(draftNode))
                    .addNode("review", node_async(reviewNode))
                    .addNode("revise", node_async(reviseNode))
                    .addNode("publish", node_async(publishNode))
                    .addEdge(START, "outline")
                    .addEdge("outline", "draft")
                    .addEdge("draft", "review")
                    .addConditionalEdges(
                            "review",
                            edge_async(state -> {
                                if (state.score() >= PASS_SCORE) {
                                    return "publish";
                                }
                                if (state.revisionCount() < MAX_REVISIONS) {
                                    return "revise";
                                }
                                return "publish";
                            }),
                            Map.of(
                                    "publish", "publish",
                                    "revise", "revise"
                            )
                    )
                    .addEdge("revise", "review")
                    .addEdge("publish", END);

            this.compiledGraph = stateGraph.compile(
                    CompileConfig.builder()
                            .graphId(GRAPH_ID)
                            .checkpointSaver(new MemorySaver())
                            .interruptBefore("publish")
                            .releaseThread(false)
                            .build()
            );
        } catch (GraphStateException e) {
            throw new IllegalStateException("Failed to initialize learning assistant workflow graph", e);
        }
    }

    public void runDemo() {
        System.out.println("=== Learning assistant workflow demo start ===");

        RunnableConfig runnableConfig = RunnableConfig.builder()
                .graphId(GRAPH_ID)
                .threadId("learning-assistant-thread-1")
                .build();

        for (var item : compiledGraph.stream(
                Map.of(LearningAssistantState.TOPIC_KEY, "LangGraph4j 学习助教：从最小图到完整案例"),
                runnableConfig)) {
            System.out.println(item);
        }

        StateSnapshot<LearningAssistantState> pausedSnapshot = compiledGraph.getState(runnableConfig);
        System.out.println("Paused snapshot next node: " + pausedSnapshot.next());
        System.out.println("Paused snapshot state: " + pausedSnapshot.state());

        System.out.println("Checkpoint history:");
        for (var historyItem : compiledGraph.getStateHistory(runnableConfig)) {
            System.out.println(historyItem);
        }

        System.out.println("=== Resume workflow ===");
        for (var item : compiledGraph.stream(GraphInput.resume(), pausedSnapshot.config())) {
            System.out.println(item);
        }

        StateSnapshot<LearningAssistantState> finalSnapshot = compiledGraph.getState(runnableConfig);
        System.out.println("Final snapshot next node: " + finalSnapshot.next());
        System.out.println("Final snapshot state: " + finalSnapshot.state());
        System.out.println("=== Learning assistant workflow demo end ===");
    }
}
