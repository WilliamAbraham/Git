import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
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

    public void deleteFiles(){
        File directory;
        for (int i = 0; i < 4; i++){
            directory = new File("directory" + i);
            for (File file : directory.listFiles()){
                file.delete();
            }
            directory.delete();
        }
        File objects = new File("objects");
        for (File file : objects.listFiles()){
            file.delete();
        }
        objects.delete();
        File index = new File("index");
        index.delete();
        File head = new File("Head");
        head.delete();
    }

    @Test
    public void oneCommit() throws IOException{
        createFiles();
        Index toAdd = new Index();
        toAdd.add("directory0/subFile0");
        toAdd.add("directory0/subFile1");

        Commit commit0 = new Commit("William", "test commit 0");

        File commitSha = new File(commit0.getCommitSha());
        BufferedReader readCommit = new BufferedReader(new FileReader("objects/" + commitSha));
        String line = readCommit.readLine();
        assertEquals(line, "7542b35579da3b3b579af48972901683aaa42cba");
        line = readCommit.readLine();
        assertEquals(line, "");
        line = readCommit.readLine();
        assertEquals(line, "");
        readCommit.close();
        deleteFiles();
    }

    @Test
    public void twoCommit() throws IOException{
        // createFiles();

        // Index toAdd = new Index();
        // toAdd.add("directory0");
        // toAdd.add("directory1/subFile0");

        // // String indexContent0;

        // Commit commit0 = new Commit("William0", "test commit 0");

        // toAdd.add("directory2");
        // toAdd.add("directory1/subFile1");

        // Commit commit1 = new Commit("William1", "test commit 1"); 

        // Confirm tree content



        deleteFiles();
    }

    // @Test
    // void fourCommit(){
        
    // }

}
