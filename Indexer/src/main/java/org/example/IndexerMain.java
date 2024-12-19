package org.example;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class IndexerMain {
    private final String datalakePath ;
    private final String jsonDatamartPath;

    public IndexerMain(String datalakePath, String jsonDatamartPath) {
        this.datalakePath = datalakePath;
        this.jsonDatamartPath = jsonDatamartPath;
    }

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        InvertedIndexBuilder invertedIndexBuilder = new BuiltInvertedIndex();
        InvertedIndexStorer invertedIndexStorer = new StoreInvertedIndex();

        System.out.println("Iniciando la indexación...");

        scheduler.scheduleAtFixedRate(() -> {
            invertedIndexStorer.storeInvertedIndexJson(invertedIndexBuilder.buildInvertedIndex(args[0]),args[1]);
            System.out.println("Los nuevos libros se han indexado correctamente en el JSON DATAMART");

            invertedIndexStorer.storeInvertedIndexMongo(invertedIndexBuilder.buildInvertedIndex(args[0]));
            System.out.println("Los nuevos libros se han indexado correctamente en el Mongo DATAMART");
        }, 2, 30 , TimeUnit.SECONDS); // Ejecuta cada 20 segundos

    }

}
