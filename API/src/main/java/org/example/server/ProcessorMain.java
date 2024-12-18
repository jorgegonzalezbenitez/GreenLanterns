package org.example.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessorMain {
    private static DatalakeProcessor datalakeProcessor = new DatalakeProcessor();
    private static DatamartProcessor datamartProcessor = new DatamartProcessor();

    public static void main(String[] args) {
        System.out.println("=== INICIANDO PROCESOS EN PARALELO ===");

        // Crear un pool de hilos con 2 hilos
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Ejecutar los procesos en paralelo
        executor.execute(() -> {
            System.out.println("=== PROCESANDO DATALAKE ===");
            datalakeProcessor.Process();
            System.out.println("=== FINALIZADO PROCESO DEL DATALAKE ===");
        });

        executor.execute(() -> {
            System.out.println("=== PROCESANDO DATAMART ===");
            datamartProcessor.Process();
            System.out.println("=== FINALIZADO PROCESO DEL DATAMART ===");
        });

        // Cerrar el executor
        executor.shutdown();

        System.out.println("\n=== FINALIZANDO TODOS LOS PROCESOS ===");
    }
}
