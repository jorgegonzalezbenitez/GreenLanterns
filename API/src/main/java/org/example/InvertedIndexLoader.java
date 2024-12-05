package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class InvertedIndexLoader {
    private final String basePath;
    private Set<String> authors = new HashSet<>(); // Para almacenar autores únicos
    private Set<String> books = new HashSet<>(); // Para almacenar títulos únicos

    public InvertedIndexLoader(String basePath) {
        this.basePath = basePath;
    }

    // Método para buscar una palabra específica en el índice
    public Map<String, Object> searchWord(String word) throws IOException {
        String subDir = getSubdirectory(word);
        if (subDir == null) {
            return null; // Si no hay subdirectorio, devolvemos null
        }

        String filePath = basePath + File.separator + "English" + File.separator + subDir + File.separator + word + ".json";
        File jsonFile = new File(filePath);

        if (!jsonFile.exists()) {
            System.out.println("File not found: " + filePath); // Depuración si el archivo no existe
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> wordData = mapper.readValue(jsonFile, Map.class);

        // Depuración: Verificar el contenido del JSON
        System.out.println("Processing file: " + filePath);
        System.out.println("Content: " + wordData);

        if (wordData != null && wordData.containsKey(word)) {
            List<Map<String, Object>> booksList = (List<Map<String, Object>>) wordData.get(word);

            if (booksList != null) {
                for (Map<String, Object> book : booksList) {
                    String author = (String) book.get("author");
                    String title = (String) book.get("title");

                    // Añadir autores y libros únicos
                    if (author != null && !author.isEmpty()) {
                        authors.add(author);
                    }
                    if (title != null && !title.isEmpty()) {
                        books.add(title);
                    }
                }
            } else {
                System.out.println("No books found for word: " + word);
            }
        } else {
            System.out.println("No data found for word: " + word);
        }

        return wordData;
    }

    // Método para obtener las estadísticas de autores y libros
    public Map<String, Object> getStatistics() throws IOException, ParseException {
        // Inicializamos contadores
        Set<String> uniqueAuthors = new HashSet<>();
        Set<String> uniqueBooks = new HashSet<>();

        // Iteramos por todas las palabras en el índice
        File dir = new File(basePath + File.separator + "English");
        for (File subDir : dir.listFiles()) {
            if (subDir.isDirectory()) {
                for (File wordFile : subDir.listFiles()) {
                    if (wordFile.getName().endsWith(".json")) {
                        // Cargar el JSON de la palabra
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> wordData = mapper.readValue(wordFile, Map.class);
                        String word = wordFile.getName().replace(".json", "");
                        if (wordData != null && wordData.containsKey(word)) {
                            List<Map<String, Object>> booksList = (List<Map<String, Object>>) wordData.get(word);
                            for (Map<String, Object> book : booksList) {
                                String author = (String) book.get("author");
                                String title = (String) book.get("title");

                                // Añadir a los conjuntos de autores y libros únicos
                                if (author != null && !author.isEmpty()) {
                                    uniqueAuthors.add(author);
                                }
                                if (title != null && !title.isEmpty()) {
                                    uniqueBooks.add(title);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Llamamos al método getOldestAndNewestBooks para obtener los datos de los libros más antiguos y más nuevos

        // Crear mapa de estadísticas
        Map<String, Object> stats = new HashMap<>();
        stats.put("authors", uniqueAuthors.size());
        stats.put("books", uniqueBooks.size());

        return stats;
    }






    private String getSubdirectory(String word) {
        char firstChar = Character.toUpperCase(word.charAt(0));
        if (firstChar >= 'A' && firstChar <= 'D') {
            return "A-D";
        } else if (firstChar >= 'E' && firstChar <= 'H') {
            return "E-H";
        } else if (firstChar >= 'I' && firstChar <= 'L') {
            return "I-L";
        } else if (firstChar >= 'M' && firstChar <= 'P') {
            return "M-P";
        } else if (firstChar >= 'Q' && firstChar <= 'T') {
            return "Q-T";
        } else if (firstChar >= 'U' && firstChar <= 'Z') {
            return "U-Z";
        } else {
            return null;
        }
    }
}
