package site.werun.aiops.knowledge;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LocalKnowledgeService {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("[a-z0-9_-]{2,}|[\\u4e00-\\u9fff]{2,}");
    private final List<KnowledgeDocument> documents;

    public LocalKnowledgeService() {
        this.documents = loadDocuments();
    }

    public List<KnowledgeReference> search(String query, int limit) {
        if (limit <= 0) {
            return List.of();
        }
        Set<String> queryTokens = tokens(query);
        return documents.stream()
                .map(document -> new ScoredDocument(document, score(document, queryTokens)))
                .filter(scored -> scored.score() > 0)
                .sorted(Comparator.comparingInt(ScoredDocument::score).reversed()
                        .thenComparing(scored -> scored.document().title()))
                .limit(limit)
                .map(scored -> scored.document().toReference())
                .toList();
    }

    private int score(KnowledgeDocument document, Set<String> queryTokens) {
        Set<String> documentTokens = tokens(document.content());
        return (int) queryTokens.stream().filter(documentTokens::contains).count();
    }

    private List<KnowledgeDocument> loadDocuments() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:knowledge/*.md");
            return Arrays.stream(resources)
                    .map(this::readDocument)
                    .sorted(Comparator.comparing(KnowledgeDocument::title))
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("无法加载本地知识库", exception);
        }
    }

    private KnowledgeDocument readDocument(Resource resource) {
        try (var inputStream = resource.getInputStream()) {
            String content = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8).trim();
            String title = content.lines()
                    .filter(line -> line.startsWith("# "))
                    .findFirst()
                    .map(line -> line.substring(2).trim())
                    .orElse(resource.getFilename());
            return new KnowledgeDocument(title, "knowledge/" + resource.getFilename(), content);
        } catch (IOException exception) {
            throw new IllegalStateException("无法读取知识文档: " + resource.getFilename(), exception);
        }
    }

    private Set<String> tokens(String text) {
        Matcher matcher = TOKEN_PATTERN.matcher(text.toLowerCase(Locale.ROOT));
        java.util.HashSet<String> result = new java.util.HashSet<>();
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    private record ScoredDocument(KnowledgeDocument document, int score) {
    }
}
