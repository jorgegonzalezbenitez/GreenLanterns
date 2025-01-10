package Server;

import API.QueryAPI;
import com.hazelcast.core.HazelcastInstance;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.TimeUnit;

public class Main {
    private static String datalakePath = "/data/datalake";
    private static String jsonDatamart = "/data/datamart";

    public static void main (String[]args){
            HazelCastProcessor hazelCastProcessor = new HazelCastProcessor(datalakePath,jsonDatamart);
            HazelcastInstance hazelcastInstance = hazelCastProcessor.getHazelcastInstance();

            System.out.println("=== INICIANDO PROCESOS EN PARALELO ===");

            ExecutorService executor = Executors.newFixedThreadPool(2);

            executor.execute(() -> {
                System.out.println("=== PROCESANDO DATALAKE ===");
                hazelCastProcessor.processData();
                System.out.println("=== FINALIZADO PROCESO DEL DATALAKE ===");
            });

            try {
                Thread.sleep(10000); // Espera de 10 segundos
            } catch (InterruptedException e) {
                System.err.println("Error al esperar entre hilos: " + e.getMessage());
            }

            executor.execute(() -> {
                System.out.println("=== PROCESANDO DATALAKE ===");
                hazelCastProcessor.loadData();
                System.out.println("=== FINALIZADO PROCESO DEL DATALAKE ===");
            });

            // Esperar que los hilos finalicen
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60,TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }

            int apiPort = 4567; // Puerto de la API
            QueryAPI queryAPI = new QueryAPI(hazelcastInstance, apiPort);
            queryAPI.startServer(); // Debe iniciar correctamente el servidor

            System.out.println("API iniciada en el puerto " + apiPort);

            System.out.println("\n=== FINALIZANDO TODOS LOS PROCESOS ===");
        }
    }