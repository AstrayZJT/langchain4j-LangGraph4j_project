# 04 - 真实 LLM 工作流详解

这一节专门讲清楚：**整个项目到底是怎么跑起来的，每一部分分别在干什么**。

## 1. 总体流程

```text
Spring Boot 启动
-> 组装 LangChain4j 真模型
-> 组装 LangGraph4j 图
-> 按节点执行
-> 遇到条件分支 / 循环 / checkpoint 就切换路径
-> 最后输出最终文档
```

这个项目不是一个单纯聊天程序，而是一个“工作流 + 真 LLM”的系统。

## 2. 启动入口在干什么

看 [LearningAssistantWorkflowApplication.java](../../src/main/java/com/example/learningassistantworkflow/LearningAssistantWorkflowApplication.java)。

它做两件事：

1. `SpringApplication.run(...)` 启动 Spring
2. 通过 `ApplicationRunner` 在启动后自动执行工作流

所以你一运行项目，它不会等你手动点接口，而是直接开始整条流程。

## 3. 真 LLM 是怎么接入的

看 [LangChainConfig.java](../../src/main/java/com/example/learningassistantworkflow/assistant/LangChainConfig.java)。

这里做的是“模型层”：

- 从环境变量读 `DASHSCOPE_API_KEY`
- 默认模型名是 `qwen-plus`
- 用 `OpenAiChatModel` 连接 DashScope 的 OpenAI 兼容接口
- 再用 `AiServices.create(...)` 把接口变成可调用代理

也就是说，代码里调用的是 `DocumentWorkflowAssistant`，底层实际会去请求 qwen-plus。

## 4. 提示词接口在干什么

看 [DocumentWorkflowAssistant.java](../../src/main/java/com/example/learningassistantworkflow/assistant/DocumentWorkflowAssistant.java)。

它不是实现类，而是一个声明式提示词接口：

- `createOutline()` 负责生成提纲
- `writeDraft()` 负责生成初稿
- `reviewDraft()` 负责审阅打分
- `reviseDraft()` 负责修订
- `publishDocument()` 负责整理最终文档

每个方法上的 `@UserMessage`，就是 LangChain4j 帮你拼 prompt 的地方。

## 5. 状态在干什么

看 [LearningAssistantState.java](../../src/main/java/com/example/learningassistantworkflow/workflow/LearningAssistantState.java)。

它负责“节点之间传什么数据”：

- `topic`：主题
- `outline`：提纲
- `draft`：草稿
- `feedback`：审阅意见
- `score`：分数
- `revisionCount`：修订次数
- `finalDocument`：最终文档
- `messages`：过程记录

`SCHEMA` 里：

- `base()` 是普通单值字段
- `appender()` 是列表追加字段

所以每个节点不是直接改对象，而是“返回一份更新”，再由图统一合并。

## 6. 每个节点在干什么

这些类都是 `NodeAction<LearningAssistantState>`：

- [OutlineNode.java](../../src/main/java/com/example/learningassistantworkflow/workflow/OutlineNode.java)：输入主题，生成提纲
- [DraftNode.java](../../src/main/java/com/example/learningassistantworkflow/workflow/DraftNode.java)：输入提纲，生成初稿
- [ReviewNode.java](../../src/main/java/com/example/learningassistantworkflow/workflow/ReviewNode.java)：输入草稿，调用模型打分并给反馈
- [ReviseNode.java](../../src/main/java/com/example/learningassistantworkflow/workflow/ReviseNode.java)：根据反馈修订草稿
- [PublishNode.java](../../src/main/java/com/example/learningassistantworkflow/workflow/PublishNode.java)：把最终内容整理成文档

每个节点都只做三件事：

1. 从 state 取值
2. 调 LLM
3. 把新值写回 state

## 7. 工作流怎么编排

看 [LearningAssistantWorkflowService.java](../../src/main/java/com/example/learningassistantworkflow/workflow/LearningAssistantWorkflowService.java)。

这里做的是图层：

- `START -> outline -> draft -> review`
- `review` 后面分支：
  - 分数够高 -> `publish`
  - 分数不够 -> `revise -> review`
- `publish -> END`

LangGraph4j 的价值就在这里：它不管你怎么生成内容，只管**流程怎么走**。

## 8. checkpoint 在干什么

当前用的是：

- `MemorySaver()`
- `interruptBefore("publish")`

这表示：

- 每一步状态都会被保存
- 在 `publish` 前先暂停
- 你可以读当前快照
- 再用 `GraphInput.resume()` 恢复执行

它解决的是：

- 中途暂停
- 查看当前状态
- 继续跑完

## 9. 为什么会看到 HTTP 请求日志

你看到的这条：

```text
POST https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions
```

说明的是：

- LangChain4j 已经真的在请求 qwen-plus
- 模型层已经接通

但它不等于整个工作流完成。  
工作流是否生效，要看这些节点是否按顺序执行：

- `OutlineNode`
- `DraftNode`
- `ReviewNode`
- `ReviseNode`
- `PublishNode`

## 10. 怎么判断工作流真的跑通

你要看这几类日志是否连起来：

- `OutlineNode executing`
- `DraftNode executing`
- `ReviewNode executing`
- `ReviseNode executing`
- `Paused snapshot next node: publish`
- `=== Resume workflow ===`
- `PublishNode executing`
- `Final snapshot next node: __END__`

如果这些都出现了，说明：

1. 真模型生效了
2. 图跑通了
3. 条件分支生效了
4. checkpoint 生效了
5. 最终文档也写回了 state

