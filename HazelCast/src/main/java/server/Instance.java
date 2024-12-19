package server;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import controller.BookList;
import model.Book;
import server.HazelMap;

import java.util.*;

public class Instance {
    private static BookList bookList;

    public static void main(String[] args) {
        Config config = new Config();
        JoinConfig joinConfig = config.getNetworkConfig().getJoin();

        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(true)
                .addMember("192.168.39.189")
                .addMember("192.168.39.138");

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        Map map = hazelcastInstance.getMap("example-map");

        List<Book> books = bookList.bookListCreator();
        for (Book book:books) {
            map.put("Book1_10" ,books);
            System.out.println("Value for key1: " + map.get("Book1_10"));
        }
    }
}