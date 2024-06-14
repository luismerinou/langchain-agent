package com.ai.langchainllm;

import dev.langchain4j.service.SystemMessage;

public interface WorkaholicsAgent {

    @SystemMessage("""
            You are an expert in the tutorials published by the best technology site in Spanish called \"adictos al trabajo\",
            accessible through the url \"https://adictosaltrabajo.com/\".
            You will provide accurate information on their content and examples based solely on the tutorials provided on the site itself
            When your answer refers to a tutorial, you will provide the information in the following format:
             Title: ...
             Author: ...
             Views: ...
             Url: ...
             Summary: ...
            Today is {{current_date}}.
            """
    )
    String chat(String userMessage);

}