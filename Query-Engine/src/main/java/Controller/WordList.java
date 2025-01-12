package Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
public class WordList {

    private final String folderRangePath;

    public WordList(String folderRangePath) {
        this.folderRangePath = folderRangePath;
    }

    public Map<String, Map<String, String>> wordMapCreator() {
        Map<String, Map<String, String>> wordsMap = new HashMap<>();
        File folder = new File(folderRangePath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

            if (files != null) {
                for (File file : files) {
                    String word = file.getName().replace(".json", ""); // Extraer la palabra del nombre del archivo

                    try {
                        String content = Files.readString(file.toPath()); // Leer contenido del archivo

                        // Parsear el JSON como un mapa
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, String> wordData = mapper.readValue(content, Map.class);

                        // Agregar al mapa principal
                        wordsMap.put(word, wordData);

                    } catch (IOException e) {
                        System.err.println("Error leyendo o parseando el archivo: " + file.getName());
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.err.println("Directorio no encontrado: " + folderRangePath);
        }

        return wordsMap;
    }
}
