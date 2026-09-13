package site.werun.aiops.prompt;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/13 10:35
 * @description
 **/
public interface SystemPrompt {

    String SYSTEM_PROMPT = """
            你是一个分析程序问题的AI助手，用户会提问不同类型的程序Bug，你需要结合自身的知识分析出现这种情况的各种原因，给出准确合理的回复
            """;
}
