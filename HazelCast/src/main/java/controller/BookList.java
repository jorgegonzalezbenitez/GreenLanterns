package controller;

import model.Book;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookList {
    private static final String datalakePath = "C:\\Users\\aadel\\Desktop\\GCID\\Tercero\\BD\\TrabajoFinal\\GreenLanterns";
    public static List<String> getUUIDsFromFiles() {
        List<String> uuids = new ArrayList<>();
        File folder = new File(datalakePath);

        // Verificar si la ruta es válida
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        if (fileName.startsWith("book_")) {
                            // Extraer el UUID eliminando el prefijo "book_"
                            String uuid = fileName.substring(5);
                            uuids.add(uuid);
                        }
                    }
                }
            }
        } else {
            System.err.println("La ruta proporcionada no es un directorio válido: " + datalakePath);
        }

        return uuids;
    }
    public static List<Book> bookListCreator() {
        List<Book> books = new ArrayList<>();
        List<String> booklist = getUUIDsFromFiles();
        for (String id: booklist) {
            Book book = new Book(id);
            books.add(book);
        }
        return books;
    }

    public static void main(String[] args) {
        List<Book> books = bookListCreator();
        int n = 1;
        System.out.println(books);
        for (Book book:books) {
            System.out.println("Book :" + book.getId());
            n +=1;
        }
    }
}
