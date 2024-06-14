package com.ai.langchainllm;

import dev.langchain4j.service.SystemMessage;

public interface WorkaholicsAgent {

    @SystemMessage({
            "You are an expert agent in the tutorials published by the best technology site in Spanish called 'adictos al trabajo',",
            "accessible through the url https://adictosaltrabajo.com/",
            "You will provide accurate information on their content and examples based solely on the documentation provided on the site itself."
    })
    String chat(String userMessage);

}