package org.example.server;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.example.controller.WordList;

import java.io.File;
import java.util.*;

public class DatamartProcessor {

    private static final String DATAMART_PATH = "C:\\Users\\jorge gonzalez\\Documents\\Tercero 2024-2025\\1er Cuatri\\Big Data\\JavaSearchEngine\\SearchEngine\\jsonDatamart"; // Ruta base

    void Process() {
        // Configurar Hazelcast
        Config config = new Config();
        JoinConfig joinConfig = config.getNetworkConfig().getJoin();

        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(true)
                .addMember("192.168.1.146")
                .addMember("192.168.1.144");

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        Map<String, List<Map<String, String>>> hazelcastMap = hazelcastInstance.getMap("datamart-map");

        // Procesar carpetas y cargar los datos en Hazelcast
        loadDataForRange("A-D", hazelcastMap);
        loadDataForRange("E-H", hazelcastMap);
        loadDataForRange("I-L", hazelcastMap);
        loadDataForRange("M-P", hazelcastMap);
        loadDataForRange("Q-T", hazelcastMap);
        loadDataForRange("U-Z", hazelcastMap);

    }

    /**
     * Carga los datos procesados de una carpeta específica en Hazelcast.
     */
    private static void loadDataForRange(String folderRange, Map<String, List<Map<String, String>>> hazelcastMap) {
        String folderPath = DATAMART_PATH + File.separator + folderRange;
        WordList wordList = new WordList(folderPath);

        List<Map<String, String>> wordsList = wordList.wordMapCreator();
        hazelcastMap.put("words" + folderRange, wordsList);

        System.out.println("Datos cargados en Hazelcast para clave: words" + folderRange);
    }


}
