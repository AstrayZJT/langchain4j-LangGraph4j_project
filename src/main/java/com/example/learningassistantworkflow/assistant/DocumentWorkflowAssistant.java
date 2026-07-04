package com.example.learningassistantworkflow.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@SystemMessage("You are a concise Chinese learning assistant that writes clear learning documents.")
public interface DocumentWorkflowAssistant {

    @UserMessage("请根据主题生成提纲，只输出 Markdown 条目，不要解释：{{it}}")
    String createOutline(String topic);

    @UserMessage("请根据提纲生成初稿，要求结构清晰、内容具体、使用 Markdown：{{it}}")
    String writeDraft(String outline);

    @UserMessage("请审阅这份草稿，并严格输出格式 score=分数;feedback=审阅意见。分数范围 0-100：{{it}}")
    String reviewDraft(String reviewContext);

    @UserMessage("请根据反馈修订草稿，输出修订后的 Markdown 内容：{{it}}")
    String reviseDraft(String revisionContext);

    @UserMessage("请把这份内容整理成最终文档，补齐标题、要点和验证方式：{{it}}")
    String publishDocument(String publishContext);
}
