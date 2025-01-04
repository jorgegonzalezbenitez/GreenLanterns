package org.example;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class IndexerMain {
    private static String datalakePath = "C:\\Users\\jorge gonzalez\\Documents\\Tercero 2024-2025\\1er Cuatri\\Big Data\\JavaSearchEngine\\GreenLanterns\\Datalake";
    private static String jsonDatamartPath = "C:\\Users\\jorge gonzalez\\Documents\\Tercero 2024-2025\\1er Cuatri\\Big Data\\JavaSearchEngine\\GreenLanterns";

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        InvertedIndexBuilder invertedIndexBuilder = new BuiltInvertedIndex();
        InvertedIndexStorer invertedIndexStorer = new StoreInvertedIndex();

        System.out.println("Iniciando la indexación...");

        scheduler.scheduleAtFixedRate(() -> {
            invertedIndexStorer.storeInvertedIndexJson(invertedIndexBuilder.buildInvertedIndex(datalakePath),jsonDatamartPath);
            System.out.println("Los nuevos libros se han indexado correctamente en el JSON DATAMART");


        }, 2, 30 , TimeUnit.SECONDS); // Ejecuta cada 20 segundos

    }

}
