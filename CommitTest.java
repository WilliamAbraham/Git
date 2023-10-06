import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

public class CommitTest {
    @AfterAll
    static void deleteAll(){
        File index = new File("index");
        index.delete();
        File head = new File("Head");
        head.delete();
        File objects = new File("objects");
        for (File subfile : objects.listFiles()){
            subfile.delete();
        }
        objects.delete();

    }

    @Test
    void testConvertToSha1() {
        String random = "akljsdfhal;jkfkjasodufhjhhh";
        String sha = Commit.convertToSha1(random);

        assertEquals(sha, "d360184cc304e97e95e99c5418f6b740d6382578");
    }

    @Test
    void testCreateFile() throws IOException {
        Commit test = new Commit("d360184cc304e97e95e99c5418f6b740d6382578", "William", "this is cool!");
        
        String contentToCheck = "\n" + "d360184cc304e97e95e99c5418f6b740d6382578\n\n" + "William\n" + test.getDate() + "\nthis is cool!";

        //test fileSha - if sha correct then thecontents correct
        File fileToTest = new File("objects/" + Commit.convertToSha1(contentToCheck));
        assertTrue(fileToTest.exists());
    }
}
