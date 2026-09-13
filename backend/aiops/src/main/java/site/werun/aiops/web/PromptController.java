package site.werun.aiops.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/13 10:40
 * @description
 **/
@Slf4j
@RestController
@RequestMapping("/prompt")
public class PromptController {

    private final ChatClient chatClient;

    public PromptController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public Flux<String> chat(@RequestParam("prompt") String prompt, @RequestParam("conversationId") String conversationId) {
        log.info("收到用户提问:{}", prompt);
        return chatClient
                .prompt()
                .user(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content();
    }
}
