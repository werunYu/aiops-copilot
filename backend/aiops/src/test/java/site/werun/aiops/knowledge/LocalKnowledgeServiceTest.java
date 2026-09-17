package site.werun.aiops.knowledge;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocalKnowledgeServiceTest {

    @Test
    void hikari_timeout_query_returns_connection_pool_knowledge() {
        LocalKnowledgeService service = new LocalKnowledgeService();

        assertThat(service.search("HikariPool connection timeout", 3))
                .extracting(KnowledgeReference::title)
                .contains("数据库连接池耗尽");
    }
}
