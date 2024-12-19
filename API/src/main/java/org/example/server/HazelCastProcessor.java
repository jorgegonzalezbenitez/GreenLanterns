package org.example.server;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.example.controller.BookList;
import org.example.controller.WordList;

import java.io.File;
import java.util.*;

public class HazelCastProcessor {
    private final String dataLakePath;
    private final String dataMartPath;
    private HazelcastInstance hazelcastInstance;

    public HazelCastProcessor(String dataLakePath, String dataMartPath) {
        this.dataLakePath = dataLakePath;
        this.dataMartPath = dataMartPath;

        // Inicializar Hazelcast una vez para reutilizar en todas las funciones
        Config config = new Config();
        JoinConfig joinConfig = config.getNetworkConfig().getJoin();

        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(true)
                .addMember("192.168.1.146")
                .addMember("192.168.1.144");

        this.hazelcastInstance = Hazelcast.newHazelcastInstance(config);
    }

    public void processData() {
        // Obtener el mapa de Hazelcast
        Map<String, Object> hazelcastMap = hazelcastInstance.getMap("datalake-map");

        // Crear datos para procesar
        Map<String, String> bookMap = BookList.bookMapCreator(dataLakePath);

        // Procesar y almacenar los datos
        List<Map<String, String>> bookList = new ArrayList<>();
        for (Map.Entry<String, String> entry : bookMap.entrySet()) {
            Map<String, String> singleBookMap = new HashMap<>();
            singleBookMap.put(entry.getKey(), entry.getValue());
            bookList.add(singleBookMap);
        }

        hazelcastMap.put("Books", bookList);

        // Mostrar datos almacenados
        System.out.println("Data stored in Hazelcast under key 'Books':");
        List<Map<String, String>> storedBooks = (List<Map<String, String>>) hazelcastMap.get("Books");
        for (Map<String, String> bookEntry : storedBooks) {
            for (Map.Entry<String, String> entry : bookEntry.entrySet()) {
                System.out.println("ID: " + entry.getKey() + " Content: " + entry.getValue());
            }
        }
    }
    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    public void loadData() {
        // Obtener el mapa de Hazelcast
        Map<String, String> hazelcastMap = hazelcastInstance.getMap("datamart-map");

        // Listar carpetas dentro del Data Mart
        File baseDirectory = new File(dataMartPath);
        File[] folders = baseDirectory.listFiles(File::isDirectory);

        if (folders == null) {
            System.err.println("No se encontraron carpetas en la ruta: " + dataMartPath);
            return;
        }

        for (File folder : folders) {
            String folderName = folder.getName();
            String folderPath = folder.getAbsolutePath();
            System.out.println("Procesando carpeta: " + folderName);

            WordList wordList = new WordList(folderPath);
            List<Map<String, String>> wordsList = wordList.wordMapCreator();

            for (Map<String, String> wordData : wordsList) {
                for (Map.Entry<String, String> entry : wordData.entrySet()) {
                    hazelcastMap.put(entry.getKey(), entry.getValue());
                }
            }
        }

        // Mostrar datos cargados
        System.out.println("Contenido del mapa Hazelcast:");
        for (Map.Entry<String, String> entry : hazelcastMap.entrySet()) {
            System.out.println("Clave: " + entry.getKey() + " -> Valor: " + entry.getValue());
        }
    }
}
