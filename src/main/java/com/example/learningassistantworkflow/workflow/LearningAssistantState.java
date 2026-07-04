package com.example.learningassistantworkflow.workflow;

import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LearningAssistantState extends AgentState {

    public static final String TOPIC_KEY = "topic";
    public static final String OUTLINE_KEY = "outline";
    public static final String DRAFT_KEY = "draft";
    public static final String FEEDBACK_KEY = "feedback";
    public static final String SCORE_KEY = "score";
    public static final String REVISION_COUNT_KEY = "revisionCount";
    public static final String FINAL_DOCUMENT_KEY = "finalDocument";
    public static final String MESSAGES_KEY = "messages";

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
            TOPIC_KEY, Channels.base(() -> ""),
            OUTLINE_KEY, Channels.base(() -> ""),
            DRAFT_KEY, Channels.base(() -> ""),
            FEEDBACK_KEY, Channels.base(() -> ""),
            SCORE_KEY, Channels.base(() -> 0),
            REVISION_COUNT_KEY, Channels.base(() -> 0),
            FINAL_DOCUMENT_KEY, Channels.base(() -> ""),
            MESSAGES_KEY, Channels.appender(ArrayList::new)
    );

    public LearningAssistantState(Map<String, Object> initData) {
        super(initData);
    }

    public String topic() {
        return this.<String>value(TOPIC_KEY).orElse("");
    }

    public String outline() {
        return this.<String>value(OUTLINE_KEY).orElse("");
    }

    public String draft() {
        return this.<String>value(DRAFT_KEY).orElse("");
    }

    public String feedback() {
        return this.<String>value(FEEDBACK_KEY).orElse("");
    }

    public int score() {
        return this.<Integer>value(SCORE_KEY).orElse(0);
    }

    public int revisionCount() {
        return this.<Integer>value(REVISION_COUNT_KEY).orElse(0);
    }

    public String finalDocument() {
        return this.<String>value(FINAL_DOCUMENT_KEY).orElse("");
    }

    public List<String> messages() {
        return this.<List<String>>value(MESSAGES_KEY).orElse(List.of());
    }
}
