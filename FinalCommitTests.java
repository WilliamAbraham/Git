import org.junit.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;

public class FinalCommitTests {
    public void createFiles() throws IOException{
        File directory;
        File subFile;
        FileWriter myWriter;
        for (int i = 0; i < 4; i++){
            directory = new File("directory" + i);
            directory.mkdir();
            for (int k = 0; k < 2; k++){
                subFile = new File(directory.toString() + "/subFile" + k);
                myWriter = new FileWriter(subFile.toString());
                myWriter.write(Integer.toString(i) + Integer.toString(k));
                myWriter.close();
            }
        }

    }

    @Test
    public void oneCommit() throws IOException{
        createFiles();
    }

    // @Test
    // void twoCommit(){
        
    // }

    // @Test
    // void fourCommit(){
        
    // }

}
