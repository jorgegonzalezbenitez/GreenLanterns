package Controller;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class WordList {

    private final String folderRangePath;

    public WordList(String folderRangePath) {
        this.folderRangePath = folderRangePath;
    }

    /**
     * Procesa los archivos JSON de una carpeta y devuelve una lista de mapas.
     * Cada mapa tendrá la palabra (archivo sin extensión) como clave y el contenido JSON como valor.
     */
    public List<Map<String, String>> wordMapCreator() {
        List<Map<String, String>> wordsList = new ArrayList<>();
        File folder = new File(folderRangePath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

            if (files != null) {
                for (File file : files) {
                    String word = file.getName().replace(".json", ""); // Extraer la palabra del nombre del archivo

                    try {
                        String content = Files.readString(file.toPath()); // Leer contenido del archivo

                        // Crear un mapa con la palabra y su contenido
                        Map<String, String> wordMap = new HashMap<>();
                        wordMap.put(word, content);
                        wordsList.add(wordMap); // Añadir a la lista

                    } catch (IOException e) {
                        System.err.println("Error leyendo el archivo: " + file.getName());
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.err.println("Directorio no encontrado: " + folderRangePath);
        }

        return wordsList;
    }
}
