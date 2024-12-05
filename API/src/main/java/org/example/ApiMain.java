package org.example;

public class ApiMain {
    private  static final String jsonDatamartPath = "C:\\Users\\jorge gonzalez\\Documents\\Tercero 2024-2025\\1er Cuatri\\Big Data\\JavaSearchEngine\\SearchEngine\\jsonDatamart";
    public static void main(String[] args) {
        QueryAPI queryAPI = new QueryAPI(jsonDatamartPath, 4567);
        queryAPI.startServer();

    }
}
