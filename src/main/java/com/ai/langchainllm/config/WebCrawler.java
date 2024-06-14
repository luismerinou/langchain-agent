package com.ai.langchainllm.config;


import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.document.transformer.HtmlTextExtractor;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

@Component
@Slf4j
public class WebCrawler {

    @Autowired
    private EmbeddingStore embeddingStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    @PostConstruct
    void init() {
        ingestUrl("https://adictosaltrabajo.com/2023/08/21/devops-eso-es-cosa-del-pasado-conoce-mlops/");
        ingestUrl("https://adictosaltrabajo.com/2023/07/27/nltk-python/");
        ingestUrl("https://adictosaltrabajo.com/2023/05/10/como-ia-puede-mejorar-eficiencia-programador/");
        ingestUrl("https://adictosaltrabajo.com/2023/05/06/diagramas-de-arquitectura-con-c4-model/");
        ingestUrl("https://adictosaltrabajo.com/2023/05/12/structurizr-para-generar-diagramas-de-arquitectura-con-c4-model/");
    }

    private void ingestUrl(String url) {
        log.info("ingesting {} ", url);
        final Document document = UrlDocumentLoader.load(url, new TextDocumentParser());
        final HtmlTextExtractor transformer = new HtmlTextExtractor(".td-container", Map.of("title", "h1.entry-title", "author", ".td-post-author-name", "date", ".td-post-date", "visits", ".td-post-views"), true);
        final var transformedDocument = transformer.transform(document);

        DocumentSplitter splitter = DocumentSplitters.recursive(100, 5, new OpenAiTokenizer(GPT_3_5_TURBO));
        final EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(transformedDocument);
    }
}