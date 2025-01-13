package Server;

import Controller.BookList;
import Controller.WordList;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;


import java.io.File;
import java.util.*;

public class HazelCastProcessor {
    private final String dataLakePath;
    private final String dataMartPath;
    private HazelcastInstance hazelcastInstance;

    public HazelCastProcessor(String dataLakePath, String dataMartPath) {
        this.dataLakePath = dataLakePath;
        this.dataMartPath = dataMartPath;

        // Configuración optimizada de Hazelcast
        Config config = new Config();
        JoinConfig joinConfig = config.getNetworkConfig().getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(true)
                .addMember("192.168.0.28");

        this.hazelcastInstance = Hazelcast.newHazelcastInstance(config);
    }

    public void processData() {
        Map<String, List<String>> hazelcastMap = hazelcastInstance.getMap("datalake-map");

        Map<String, String> bookMap = BookList.bookMapCreator(dataLakePath);

        int batchSize = 100;
        List<Map.Entry<String, String>> entries = new ArrayList<>(bookMap.entrySet());
        for (int i = 0; i < entries.size(); i += batchSize) {
            int end = Math.min(i + batchSize, entries.size());
            List<Map.Entry<String, String>> batch = entries.subList(i, end);

            for (Map.Entry<String, String> entry : batch) {
                hazelcastMap.put(entry.getKey(), Collections.singletonList(entry.getValue()));
            }

            System.out.println("Lote cargado: " + (i / batchSize + 1));
        }

        System.out.println("Carga completa de datos.");
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    public void loadData() {
        // Obtener el mapa de Hazelcast
        Map<String, String> hazelcastMap = hazelcastInstance.getMap("datamart-map");

        File baseDirectory = new File(dataMartPath);
        File[] folders = baseDirectory.listFiles(File::isDirectory);

        if (folders == null) {
            System.err.println("No se encontraron carpetas en la ruta: " + dataMartPath);
            return;
        }

        int totalEntries = 0;
        for (File folder : folders) {
            String folderName = folder.getName();
            String folderPath = folder.getAbsolutePath();
            System.out.println("Procesando carpeta: " + folderName);

            WordList wordList = new WordList(folderPath);
            List<Map<String, String>> wordsList = wordList.wordMapCreator();

            for (Map<String, String> wordData : wordsList) {
                for (Map.Entry<String, String> entry : wordData.entrySet()) {
                    if (totalEntries >= 1000) break;
                    hazelcastMap.put(entry.getKey(), entry.getValue());
                    totalEntries++;
                }
            }
        }

        // Mostrar algunos datos cargados (limitado a 10 para evitar spam en consola)
        System.out.println("Contenido del mapa Hazelcast:");
        int count = 0;
        for (Map.Entry<String, String> entry : hazelcastMap.entrySet()) {
            System.out.println("Clave: " + entry.getKey() + " -> Valor: " + entry.getValue());
            if (++count >= 10) break;
        }
    }
}

