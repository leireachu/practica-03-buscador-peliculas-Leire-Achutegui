package com.springai.init.service;

import com.springai.init.model.Pelicula;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PeliculasServiceImpl implements PeliculasService, InitializingBean {

    VectorStore vectorStore;
    ChatClient chatClient;

    @Value("${text.system}")
    String textSystem;

    public PeliculasServiceImpl(VectorStore vectorStore, ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }

    @Override
    public List<Pelicula> consultarPeliculas(String query) {
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(query).topK(10).build());
        String contexto = documents.stream().map(document ->
                document.getText()).collect(Collectors.joining("\n"));

        Prompt prompt = buildPrompt(query, contexto);

        return List.of(chatClient.prompt(prompt).call().entity(Pelicula[].class));
    }


    private Prompt buildPrompt(String mensajeUser, String contexto) {
        PromptTemplate templateSys = new PromptTemplate(textSystem);
        Message systemMessage = templateSys.createMessage(Map.of("contexto", contexto));
        Message userMessage = new UserMessage("Mensaje usuario: " + mensajeUser);
        return new Prompt(List.of(systemMessage,userMessage));

    }


    @Override
    public void afterPropertiesSet() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream("static/peliculas.json");

        List<String> peliculasList = mapper.readValue(
                inputStream,
                new TypeReference<List<String>>() {}
        );

        vectorStore.delete("doc_content != ''");
        vectorStore.add(peliculasList.stream()
                .map(Document::new)
                .toList());

    }
}
