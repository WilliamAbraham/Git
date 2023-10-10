import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

public class DeleteAndEditTests {
    public void createFiles() throws IOException{
        File directory;
        File subFile;
        FileWriter myWriter;
        for (int i = 0; i < 6; i++){
            directory = new File("directory" + i);
            directory.mkdir();
            for (int k = 0; k < 2; k++){
                subFile = new File(directory.toString() + "/subFile" + Integer.toString(i) + Integer.toString(k));
                myWriter = new FileWriter(subFile.toString());
                myWriter.write(Integer.toString(i) + Integer.toString(k));
                myWriter.close();
            }
        }
    }

    public void deleteFiles(){
        File directory;
        for (int i = 0; i < 6; i++){
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
    void fiveCommits() throws IOException{
        createFiles();
        
        Index cool = new Index();
        cool.add("directory0/subFile00");
        cool.add("directory0/subFile01");
        cool.add("directory1/subFile10");
        cool.add("directory1/subFile11");

        Commit commit1 = new Commit("William Abraham", "this is commit 1");
        //works

        cool.add("directory2/subFile20");
        cool.add("directory2/subFile21");

        Commit commit2 = new Commit("William Abraham", "this is commit 2");
        //works

        File directory2subFile20 = new File("directory2/subFile20");
        FileWriter writer1 = new FileWriter(directory2subFile20);
        writer1.append("this has been edited1");
        writer1.close();

        cool.edit("directory2/subFile20");

        File directory2subFile21 = new File("directory2/subFile21");
        FileWriter writer2 = new FileWriter(directory2subFile21);
        writer2.append("this has been edited2");
        writer2.close();

        cool.edit("directory2/subFile21");
        
        Commit commit3 = new Commit("William Abraham", "this is commit 3");
        //works

        cool.add("directory3/subFile30");
        cool.add("directory3/subFile31");
        cool.delete("directory2/subFile21");
        
        Commit commit4 = new Commit("William Abraham", "this is commit 4");
        //works

        cool.delete("directory2/subFile20");
        cool.delete("directory3/subFile31");

        Commit commit5 = new Commit("William Abraham", "this is commit 5");
        //works
    }
}
