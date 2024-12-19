import org.example.Crawler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CrawlerTest {

    private Crawler crawler;
    private String datalakePath;

    @Before
    public void setUp() {
        datalakePath = "test_datalake"; // Ruta simulada para el datalake
        crawler = new Crawler(datalakePath);

        // Crear directorio de prueba
        new File(datalakePath, "Datalake").mkdirs();
    }

    @Test
    public void testGetResponseFromUrlSuccess() throws IOException {
        String testUrl = "https://www.gutenberg.org/cache/epub/1/pg1.txt";
        String mockResponse = "This is a test book content.";

        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(mockResponse.getBytes()));

        URL mockUrl = mock(URL.class);
        when(mockUrl.openConnection()).thenReturn(mockConnection);

        // Mockear el comportamiento de URL

        String response = crawler.getResponseFromUrl(testUrl);
        assertEquals(mockResponse, response);
    }



    @Test
    public void testSaveBookSuccess() throws IOException {
        String bookContent = "Sample book content.";
        String bookTitle = "Sample_Book";
        File outputFolder = new File(datalakePath, "Datalake");

        boolean result = crawler.saveBook(bookContent, bookTitle, outputFolder);

        assertTrue(result);

        Path bookPath = Path.of(outputFolder.getPath(), "book_Sample_Book.txt");
        assertTrue(Files.exists(bookPath));

        String savedContent = Files.readString(bookPath);
        assertEquals(bookContent, savedContent);
    }

    @Test
    public void testSaveBookInvalidTitle() throws IOException {
        String bookContent = "Sample book content.";
        String invalidTitle = "Invalid:/\\*?<>|";

        File outputFolder = new File(datalakePath, "Datalake");
        boolean result = crawler.saveBook(bookContent, invalidTitle, outputFolder);

        assertTrue(result);

        // Verificar que el archivo fue creado con un título válido
        File[] files = outputFolder.listFiles();
        assertNotNull(files);
        assertEquals(1, files.length);

        assertTrue(files[0].getName().startsWith("book_Invalid_"));
    }



}