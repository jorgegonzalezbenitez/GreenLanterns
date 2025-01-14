package Server;

import Controller.BookList;
import Controller.WordList;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.map.IMap;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class HazelCastProcessor {
    private final String dataLakePath;
    private final String dataMartPath;
    private HazelcastInstance hazelcastInstance;

    public HazelCastProcessor(String dataLakePath, String dataMartPath) {
        this.dataLakePath = dataLakePath;
        this.dataMartPath = dataMartPath;

        Config config = new Config();
        JoinConfig joinConfig = config.getNetworkConfig().getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(true)
                .addMember("192.168.0.28")
                .addMember("192.168.0.24")
                .addMember("192.168.0.25");

        this.hazelcastInstance = Hazelcast.newHazelcastInstance(config);
    }

    public void processData() {
        Map<String, List<String>> hazelcastMap = hazelcastInstance.getMap("datalake-map");

        Map<String, String> bookMap = BookList.bookMapCreator(dataLakePath);

        int batchSize = 50;  // 🔥 Reducimos el tamaño del lote para evitar picos de memoria
        List<Map.Entry<String, String>> entries = new ArrayList<>(bookMap.entrySet());

        for (int i = 0; i < entries.size(); i += batchSize) {
            int end = Math.min(i + batchSize, entries.size());
            List<Map.Entry<String, String>> batch = entries.subList(i, end);

            for (Map.Entry<String, String> entry : batch) {
                hazelcastMap.put(entry.getKey(), Collections.singletonList(entry.getValue()));
            }
        }

        System.out.println("Datalake charged.");
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    public void loadData() {
        Map<String, String> hazelcastMap = hazelcastInstance.getMap("datamart-map");
        IMap<String, Boolean> processedFolders = hazelcastInstance.getMap("processed-folders");  // Mapa de subcarpetas procesadas
        IAtomicLong currentFolderIndex = hazelcastInstance.getCPSubsystem().getAtomicLong("currentFolderIndex");  // Contador atómico para controlar el progreso
        File baseDirectory = new File(dataMartPath);
        File[] folders = baseDirectory.listFiles(File::isDirectory);

        if (folders == null) {
            System.err.println("Path not found: " + dataMartPath);
            return;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(1);  // Un hilo para procesar una carpeta a la vez
        List<Callable<Void>> tasks = new ArrayList<>();

        // Recorremos las carpetas y asignamos el procesamiento de manera secuencial
        for (File folder : folders) {
            tasks.add(() -> {
                // Obtener el índice de la carpeta que debe procesar el servidor
                long folderIndex = currentFolderIndex.incrementAndGet();  // Obtiene el siguiente índice de carpeta

                // Asigna la carpeta a un servidor basado en el índice
                if (folderIndex <= folders.length) {
                    // Verifica si la carpeta ya ha sido procesada
                    if (processedFolders.putIfAbsent(folder.getName(), true) == null) {  // Solo procesar si no se ha marcado como procesada
                        System.out.println("Processing folder: " + folder.getName());

                        WordList wordList = new WordList(folder.getAbsolutePath());
                        List<Map<String, String>> wordsList = wordList.wordMapCreator();

                        int batchSize = 100;  // Reducimos el tamaño del lote a 100 palabras
                        Map<String, String> batchMap = new HashMap<>();

                        for (Map<String, String> wordData : wordsList) {
                            for (Map.Entry<String, String> entry : wordData.entrySet()) {
                                hazelcastMap.putIfAbsent(entry.getKey(), entry.getValue());

                                batchMap.put(entry.getKey(), entry.getValue());

                                if (batchMap.size() >= batchSize) {
                                    hazelcastMap.putAll(new HashMap<>(batchMap));
                                    batchMap.clear();  // Liberar memoria inmediatamente
                                }
                            }
                        }

                        // Guardar cualquier palabra restante
                        if (!batchMap.isEmpty()) {
                            hazelcastMap.putAll(batchMap);
                            batchMap.clear();
                        }

                        System.out.println("Finished processing: " + folder.getName());
                    }
                }

                return null;
            });
        }

        try {
            executorService.invokeAll(tasks);
            executorService.shutdown();
            executorService.awaitTermination(20, TimeUnit.MINUTES);  // Asegurar que todos los hilos terminen correctamente
            System.out.println("Datamart fully loaded");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error during concurrent loading: " + e.getMessage());
        }

    }
}
