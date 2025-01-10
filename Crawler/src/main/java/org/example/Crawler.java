package org.example;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

public class Crawler {
    private static final Logger logger = Logger.getLogger(Crawler.class.getName());

    private static int currentBookId = 1;
    private final String datalakePath;
    private static final int MAX_THREADS = 4;  // Number of threads for parallel execution
    private static final int BOOKS_PER_RUN = 100; // Number of books to download per run
    private static final int N_BOOKS = 2000; // Number of books to download per run



    public Crawler(String datalakePath) {
        this.datalakePath = datalakePath;
        setupLogger();
    }

    int nbooks = 0;
    public void crawlerRunner() {
        while (nbooks < N_BOOKS) {
            setupStorage();

            logger.info("Starting to download books from ID " + currentBookId + "...");
            ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

            for (int i = 0; i < BOOKS_PER_RUN; i++) {
                int bookId = currentBookId++;
                executor.submit(() -> fetchBookContent(bookId));
                nbooks ++;
            }

            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
                    executor.shutdownNow();
                    logger.warning("Some threads were forcibly terminated due to timeout.");
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                logger.severe("Execution interrupted: " + e.getMessage());
            }

            logger.info("Execution completed. Waiting for the next scheduled run.");
        }
        logger.info("Execution completed. 2000 books downloaded.");
    }
    // Ensure the storage directory exists
    private void setupStorage() {
        File dir = new File(datalakePath, "Datalake");
        if (!dir.exists()) {
            dir.mkdirs();
            logger.info("Storage directory created at: " + dir.getAbsolutePath());
        } else {
            logger.info("Storage directory exists: " + dir.getAbsolutePath());
        }
    }

    // Attempt to download a book in multiple formats and extract its title
    private void fetchBookContent(int bookId) {
        String[] extensions = {"txt"};
        String baseUrl = "https://www.gutenberg.org/cache/epub/" + bookId + "/pg" + bookId;

        for (String ext : extensions) {
            String downloadUrl = baseUrl + "." + ext;
            String filePath = datalakePath + "/Datalake/book_" + bookId + "." + ext;

            if (Files.exists(Paths.get(filePath))) {
                logger.info("The file " + filePath + " already exists. Skipping...");
                return;
            }

            try (InputStream in = new URL(downloadUrl).openStream()) {
                // Read content to extract title
                String content = new String(in.readAllBytes());
                String title = extractTitle(content);
                if (title == null) {
                    logger.warning("Could not extract the title for book " + bookId);
                    title = "unknown_title_" + bookId;
                }

                // Save content using the title as the file name
                String sanitizedTitle = title.replaceAll("[\\\\/:*?\"<>|]", "_");
                String outputPath = datalakePath + "/Datalake/book_" + sanitizedTitle + "." + ext;
                Files.write(Paths.get(outputPath), content.getBytes());
                logger.info("Downloaded: " + outputPath);
                return;
            } catch (IOException e) {
                logger.warning("Failed to download " + downloadUrl + ": " + e.getMessage());
            }
        }

        logger.warning("Failed to download book " + bookId + " in any supported format.");
    }

    // Extract the title from the content
    private String extractTitle(String content) {
        String[] lines = content.split(System.lineSeparator());
        Pattern titlePattern = Pattern.compile("Title:\\s*(.*)");

        for (String line : lines) {
            Matcher matcher = titlePattern.matcher(line);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }

        return null; // Title not found
    }

    // Set up the logger
    private void setupLogger() {
        try {
            // Eliminar todos los handlers existentes para evitar duplicados
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }

            // Configurar el FileHandler para guardar logs en un archivo
            FileHandler fileHandler = new FileHandler(datalakePath + "/crawler.log", true);
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
    }}