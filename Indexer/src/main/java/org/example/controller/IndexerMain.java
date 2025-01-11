package org.example.controller;

import org.example.model.InvertedIndexBuilder;
import org.example.model.InvertedIndexStorer;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

    public class IndexerMain {
        private static final Logger logger = Logger.getLogger(IndexerMain.class.getName());

        private static String datalakePath = "C:\\Users\\cgsos\\Documents\\Tercero\\Big Data\\JavaSearchEngine\\GreenLanterns\\Query-Engine\\Datalake";
        private static String jsonDatamartPath = "C:\\Users\\cgsos\\Documents\\Tercero\\Big Data\\JavaSearchEngine\\GreenLanterns\\Query-Engine";

        static {
            setupLogger();
        }

        public static void main(String[] args) {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

            InvertedIndexBuilder invertedIndexBuilder = new BuildInvertedIndex();
            InvertedIndexStorer invertedIndexStorer = new StoreInvertedIndex();

            logger.info("Starting indexing process...");

            scheduler.scheduleAtFixedRate(() -> {
                try {
                    logger.info("Building and storing inverted index in JSON Datamart...");
                    invertedIndexStorer.storeInvertedIndexJson(
                            invertedIndexBuilder.buildInvertedIndex(datalakePath),
                            jsonDatamartPath
                    );
                    logger.info("Books have been successfully indexed in the JSON Datamart.");

                    logger.info("Building and storing inverted index in MongoDB Datamart...");
                    invertedIndexStorer.storeInvertedIndexMongo(
                            invertedIndexBuilder.buildInvertedIndex(datalakePath)
                    );
                    logger.info("Books have been successfully indexed in the MongoDB Datamart.");
                } catch (Exception e) {
                    logger.severe("Error during the indexing process: " + e.getMessage());
                    e.printStackTrace(); // Optional: Include stack trace for debugging
                }
            }, 1, 10, TimeUnit.SECONDS); // Executes every 30 seconds after an initial delay of 2 seconds

        }

        private static void setupLogger() {
            try {
                // Elimina handlers duplicados
                Logger rootLogger = Logger.getLogger("");
                Handler[] handlers = rootLogger.getHandlers();
                for (Handler handler : handlers) {
                    rootLogger.removeHandler(handler);
                }

                // Configurar el FileHandler para guardar logs en un archivo
                FileHandler fileHandler = new FileHandler("indexer_main.log", true);
                fileHandler.setFormatter(new SimpleFormatter());
                fileHandler.setLevel(Level.ALL);
                logger.addHandler(fileHandler);

                // Configurar el ConsoleHandler para imprimir logs en la consola
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setFormatter(new SimpleFormatter());
                consoleHandler.setLevel(Level.ALL);
                logger.addHandler(consoleHandler);

                // Configurar nivel de logs
                logger.setLevel(Level.ALL);
                rootLogger.setLevel(Level.ALL);

            } catch (IOException e) {
                logger.severe("Failed to set up logger: " + e.getMessage());
            }
        }
    }