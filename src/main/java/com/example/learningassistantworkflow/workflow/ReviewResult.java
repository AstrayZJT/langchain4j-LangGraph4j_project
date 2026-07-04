package com.example.learningassistantworkflow.workflow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ReviewResult(int score, String feedback) {

    private static final Pattern SCORE_PATTERN = Pattern.compile("score=(\\d+);feedback=(.*)");
    private static final Pattern FIRST_NUMBER_PATTERN = Pattern.compile("(\\d{1,3})");

    public static ReviewResult parse(String text) {
        Matcher matcher = SCORE_PATTERN.matcher(text.trim());
        if (!matcher.matches()) {
            Matcher numberMatcher = FIRST_NUMBER_PATTERN.matcher(text);
            int score = numberMatcher.find() ? Integer.parseInt(numberMatcher.group(1)) : 0;
            return new ReviewResult(score, text.trim());
        }
        return new ReviewResult(Integer.parseInt(matcher.group(1)), matcher.group(2).trim());
    }
}
