package org.example;

import static spark.Spark.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryAPI {
    private final InvertedIndexLoader indexLoader;
    private final int port;

    public QueryAPI(String basePath, int port) {
        this.indexLoader = new InvertedIndexLoader(basePath);
        this.port = port;
    }

    // Método para iniciar el servidor y configurar los endpoints
    public void startServer() {
        port(this.port);

        // Endpoint de búsqueda
        get("/search", (req, res) -> {
            String wordParam = req.queryParams("word");
            if (wordParam == null || wordParam.isEmpty()) {
                res.status(400);
                return "Parameter 'word' is required";
            }

            Map<String, Object> results = processSearch(wordParam);

            res.type("application/json");
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(results);
        });

        // Endpoint de estadísticas
        get("/statistics", (req, res) -> {
            Map<String, Object> stats = indexLoader.getStatistics();

            res.type("application/json");
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(stats);
        });
    }

    // Método que procesa la búsqueda en el índice
    private Map<String, Object> processSearch(String wordParam) throws IOException {
        List<String> words = Arrays.stream(wordParam.split("\\s+|\\+"))
                .map(String::trim)
                .collect(Collectors.toList());

        Map<String, Object> results = new HashMap<>();

        for (String word : words) {
            Map<String, Object> wordResult = indexLoader.searchWord(word);
            if (wordResult != null) {
                results.put(word, wordResult);
            } else {
                results.put(word, "Not found");
            }
        }
        return results;
    }
}
