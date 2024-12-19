package server;

import java.util.Map;

// Definición de la interfaz
public interface HazelMap<K, V> {
        Map<K, V> updateMap(K key, V value);
}
