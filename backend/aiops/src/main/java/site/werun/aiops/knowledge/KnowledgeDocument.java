package site.werun.aiops.knowledge;

record KnowledgeDocument(String title, String source, String content) {

    KnowledgeReference toReference() {
        String excerpt = content.length() <= 500 ? content : content.substring(0, 500);
        return new KnowledgeReference(title, source, excerpt);
    }
}
