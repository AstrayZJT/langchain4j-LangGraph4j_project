# Learning Assistant Workflow

这是一个从零开始的 Spring Boot 学习项目，主题是“学习助教 / 文档生成工作流”。

## 当前阶段

- `v0.1.0` 项目初始化
- `v0.2.0` 学习助教完整工作流
- `v0.2.1` 接入真实 LLM（qwen-plus）

## 学习进度

- [学习进度清单](./docs/学习进度/README.md)

## 运行

先设置环境变量：

```bash
set DASHSCOPE_API_KEY=你的key
set DASHSCOPE_MODEL_NAME=qwen-plus
set DASHSCOPE_TIMEOUT=PT5M
```

再启动：

```bash
mvn spring-boot:run
```

启动后，控制台会打印完整工作流的执行过程、checkpoint 暂停信息和最终文档。
