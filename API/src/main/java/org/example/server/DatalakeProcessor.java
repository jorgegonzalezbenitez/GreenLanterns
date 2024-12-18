package org.example.server;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.example.controller.BookList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatalakeProcessor {

    void Process() {
        Config config = new Config();
        JoinConfig joinConfig = config.getNetworkConfig().getJoin();

        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(true)
                .addMember("192.168.1.146")
                .addMember("192.168.1.144");

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        Map<String, Object> hazelcastMap = hazelcastInstance.getMap("datalake-map");

        // Obtener el mapa de libros desde BookList
        Map<String, String> bookMap = BookList.bookMapCreator();

        // Crear una lista de mapas donde cada mapa tiene un ID como clave y contenido como valor
        List<Map<String, String>> bookList = new ArrayList<>();
        for (Map.Entry<String, String> entry : bookMap.entrySet()) {
            Map<String, String> singleBookMap = new HashMap<>();
            singleBookMap.put(entry.getKey(), entry.getValue());
            bookList.add(singleBookMap);
        }

        // Cargar la lista de mapas en Hazelcast con la clave "Books"
        hazelcastMap.put("Books", bookList);

        // Verificar los datos almacenados en Hazelcast
        System.out.println("Data stored in Hazelcast under key 'Books':");
        List<Map<String, String>> storedBooks = (List<Map<String, String>>) hazelcastMap.get("Books");
        for (Map<String, String> bookEntry : storedBooks) {
            for (Map.Entry<String, String> entry : bookEntry.entrySet()) {
                System.out.println("ID: " + entry.getKey());
            }
        }
    }
}


