package org.example;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CrawlerMain {
    private final String datalakePath;

    public CrawlerMain(String datalakePath) {
        this.datalakePath = datalakePath;
    }

    public static void main(String[] args) {
        Crawler crawler = new Crawler(args[0]);
        System.out.println("Ininciando la descarga de libros...\n");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            crawler.crawlerRunner();
        }, 1, 60, TimeUnit.SECONDS);
    }
}