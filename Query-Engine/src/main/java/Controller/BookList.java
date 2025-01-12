package Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class BookList {


    public static Map<String, String> bookMapCreator(String datalakePath) {
        Map<String, String> bookMap = new HashMap<>();
        File folder = new File(datalakePath);

        // Verificar si la ruta es válida
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().startsWith("book_")) {
                        String fileName = file.getName();
                        String uuid = fileName.substring(5); // Elimina el prefijo "book_"

                        try {
                            // Leer el contenido del archivo
                            String content = Files.readString(file.toPath());

                            // Agregar el ID y el contenido al mapa
                            bookMap.put(uuid, content);

                        } catch (IOException e) {
                            System.err.println("Error leyendo el archivo: " + file.getName());
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            System.err.println("La ruta proporcionada no es un directorio válido: " + datalakePath);
        }
        System.out.println("la clabve es: " + bookMap.keySet());
        return bookMap;
    }
}
