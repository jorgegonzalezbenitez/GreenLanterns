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
                        // Extraer el UUID del nombre del archivo
                        String fileName = file.getName();
                        String uuid = fileName.substring(5); // Elimina el prefijo "book_"

                        // Agregar el ID y el contenido al mapa
                        bookMap.put(uuid, fileName);

                    }
                }
            }
        } else {
            System.err.println("La ruta proporcionada no es un directorio válido: " + datalakePath);
        }
        return bookMap;
    }
}
