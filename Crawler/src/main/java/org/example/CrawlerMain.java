//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.example;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CrawlerMain {
    private static String datalakePath = "C:\\Users\\jorge gonzalez\\Documents\\Tercero 2024-2025\\1er Cuatri\\Big Data\\JavaSearchEngine\\GreenLanterns";

    public CrawlerMain() {
    }

    public static void main(String[] args) {
        Crawler crawler = new Crawler(datalakePath);
        System.out.println("Starting book downloads...\n");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            crawler.crawlerRunner();
        }, 1L, 60L, TimeUnit.SECONDS);
    }
}
