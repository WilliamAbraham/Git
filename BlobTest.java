import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.zip.GZIPInputStream;

public class BlobTest {

    @BeforeEach
    public void setup() throws IOException {
        Files.write(Paths.get("testBlob.txt"), "Hello World".getBytes());
        File Objects = new File("objects");
        if (!Objects.exists()) {
            Objects.mkdirs();
        }
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get("testBlob.txt"));
        File object = new File("objects");
        for (File subFile : object.listFiles()){
            subFile.delete();
        }
        object.delete();
        File index = new File("index");
        index.delete();
    }

    @Test
    public void testBlobCreation() throws IOException {
        Blob blob = new Blob("testBlob.txt");
        // tests if the blob was made
        assertNotNull(blob);
    }

    @Test
    public void testBlobContent() throws IOException {
        Blob blob = new Blob("testBlob.txt");
        // tests if the blob was made
        assertNotNull(blob);

        String fileName = blob.getHash();

        String content = decompress("objects/" + fileName);

        // tests if the content in it is correct
        assertEquals("Hello World", content);
    }

    public String decompress(String path) throws FileNotFoundException, IOException {
        try (
                FileInputStream fis = new FileInputStream(path);
                GZIPInputStream gis = new GZIPInputStream(fis);
                ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toString("UTF-8");
        }
    }

}
