package org.example;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
    private static final Logger logger = Logger.getLogger(Crawler.class.getName());
    private static int currentBookId = 1;
    private final String datalakePath;
    private static final int MAX_THREADS = 4;
    private static final int BOOKS_PER_RUN = 5;

    public Crawler(String datalakePath) {
        this.datalakePath = datalakePath;
        this.setupLogger();
    }

    public void crawlerRunner() {
        this.setupStorage();
        logger.info("Starting to download books from ID " + currentBookId + "...");
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for(int i = 0; i < 100; ++i) {
            int bookId = currentBookId++;
            executor.submit(() -> {
                this.fetchBookContent(bookId);
            });
        }

        executor.shutdown();

        try {
            if (!executor.awaitTermination(10L, TimeUnit.MINUTES)) {
                executor.shutdownNow();
                logger.warning("Some threads were forcibly terminated due to timeout.");
            }
        } catch (InterruptedException var4) {
            executor.shutdownNow();
            logger.severe("Execution interrupted: " + var4.getMessage());
        }

        logger.info("Execution completed. Waiting for the next scheduled run.");
    }

    private void setupStorage() {
        File dir = new File(this.datalakePath, "Datalake");
        if (!dir.exists()) {
            dir.mkdirs();
            logger.info("Storage directory created at: " + dir.getAbsolutePath());
        } else {
            logger.info("Storage directory exists: " + dir.getAbsolutePath());
        }

    }

    private void fetchBookContent(int bookId) {
        String[] extensions = new String[]{"txt", "html", "epub", "mobi"};
        String baseUrl = "https://www.gutenberg.org/cache/epub/" + bookId + "/pg" + bookId;
        String[] var4 = extensions;
        int var5 = extensions.length;
        int var6 = 0;

        while(var6 < var5) {
            String ext = var4[var6];
            String downloadUrl = baseUrl + "." + ext;
            String filePath = this.datalakePath + "/Datalake/book_" + bookId + "." + ext;
            if (Files.exists(Paths.get(filePath), new LinkOption[0])) {
                logger.info("The file " + filePath + " already exists. Skipping...");
                return;
            }

            try {
                InputStream in = (new URL(downloadUrl)).openStream();

                try {
                    String content = new String(in.readAllBytes());
                    String title = this.extractTitle(content);
                    if (title == null) {
                        logger.warning("Could not extract the title for book " + bookId);
                        title = "unknown_title_" + bookId;
                    }

                    String sanitizedTitle = title.replaceAll("[\\\\/:*?\"<>|]", "_");
                    String outputPath = this.datalakePath + "/Datalake/book_" + sanitizedTitle + "." + ext;
                    Files.write(Paths.get(outputPath), content.getBytes(), new OpenOption[0]);
                    logger.info("Downloaded: " + outputPath);
                } catch (Throwable var16) {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Throwable var15) {
                            var16.addSuppressed(var15);
                        }
                    }

                    throw var16;
                }

                if (in != null) {
                    in.close();
                }

                return;
            } catch (IOException var17) {
                IOException e = var17;
                logger.warning("Failed to download " + downloadUrl + ": " + e.getMessage());
                ++var6;
            }
        }

        logger.warning("Failed to download book " + bookId + " in any supported format.");
    }

    private String extractTitle(String content) {
        String[] lines = content.split(System.lineSeparator());
        Pattern titlePattern = Pattern.compile("Title:\\s*(.*)");
        String[] var4 = lines;
        int var5 = lines.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String line = var4[var6];
            Matcher matcher = titlePattern.matcher(line);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }

        return null;
    }

    private void setupLogger() {
        try {
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            Handler[] var3 = handlers;
            int var4 = handlers.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Handler handler = var3[var5];
                rootLogger.removeHandler(handler);
            }

            FileHandler fileHandler = new FileHandler(this.datalakePath + "/crawler.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(Level.ALL);
            logger.addHandler(consoleHandler);
            logger.setLevel(Level.ALL);
            rootLogger.setLevel(Level.ALL);
        } catch (IOException var7) {
            IOException e = var7;
            logger.severe("Failed to set up logger: " + e.getMessage());
        }

    }
}
