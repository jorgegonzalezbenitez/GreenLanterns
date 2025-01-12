//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.example;



public class CrawlerMain {
    private static String datalakePath = "C:\\Users\\aadel\\Desktop\\GCID\\Tercero\\BD\\TrabajoFinal\\GreenLanterns\\Query-Engine";

    public CrawlerMain() {
    }

    public static void main(String[] args) {
        Crawler crawler = new Crawler(datalakePath);
        System.out.println("Starting book downloads...\n");
        crawler.crawlerRunner();
    }
}
