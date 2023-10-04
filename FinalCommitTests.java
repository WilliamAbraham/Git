import org.junit.Test;

import static org.junit.Assert.assertThrows;
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
        for (int i = 0; i < 6; i++){
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
        createFiles();
        File index = new File("index");

        Index toAdd = new Index();
        toAdd.add("directory0");
        toAdd.add("directory1/subFile0");

        String indexContent = Blob.read(index);
        
        Commit commit0 = new Commit("William0", "test commit 0");
        
        //Test tree contents
        assertEquals(indexContent, "Tree : 66855061ab05f70e57bbb46ee7033e0a94c4dc0f : directory0Blob : 9567eb55822e84fd927f38e325ea730d4a1c2658 : subFile0");
        
        
        toAdd.add("directory2");
        toAdd.add("directory1/subFile1");
        String indexContent1 = Blob.read(index);

        indexContent = Blob.read(index);

        Commit commit1 = new Commit("William1", "test commit 1"); 

        //test Tree contents
        assertEquals(indexContent, "Tree : 79f5565e57ca816039035d37a6a333ea68bb67ad : directory2Blob : 63cc87bc35cf08a3cad48f62168678700c3643f0 : subFile1");

        //test commit shas
        BufferedReader readCommit = new BufferedReader(new FileReader("objects/" + commit0.getCommitSha()));
        String line = readCommit.readLine();

        assertEquals(line, "bbd8ee8a8273025ef98fb4d293d2f19e2dceb977");
        line = readCommit.readLine();
        assertEquals(line, "");
        line = readCommit.readLine();
        assertEquals(line, commit1.getCommitSha());
        readCommit.close();

        readCommit = new BufferedReader(new FileReader("objects/" + commit1.getCommitSha()));
        line = readCommit.readLine();
        assertEquals(line, "a1bf578cd41d6b34758bf17b09c9716df12bc355");
        line = readCommit.readLine();
        assertEquals(line, commit0.getCommitSha());
        line = readCommit.readLine();
        assertEquals(line, "");
        readCommit.close();

        deleteFiles();
    }

    @Test
    public void fourCommit() throws IOException{
        createFiles();
        File index = new File("index");

        Index toAdd = new Index();
        toAdd.add("directory0/subFile0");
        toAdd.add("directory0/subFile1");
        String indexContent = Blob.read(index);

        Commit commit0 = new Commit("William0", "test commit 0");

        //test Tree
        assertEquals(indexContent, "Blob : cee88b236a3f765ebe7711008dfb24fd5b9bcd6d : subFile0Blob : 7add34e050b507e13049a7e68888f2805d2b4e8f : subFile1");

        toAdd.add("directory1");
        toAdd.add("directory2/subFile0");
        toAdd.add("directory2/subFile1");
        indexContent = Blob.read(index);

        Commit commit1 = new Commit("William0", "test commit 1");

        //test Tree
        assertEquals(indexContent, "Tree : 8b39131ffc87356366e939702cee63f828793295 : directory1Blob : 0f13d513e508da1f5211b481d77d9b6efc55ffff : subFile0Blob : 3683906ef3550284138cc0616d065621189d0dc1 : subFile1");

        toAdd.add("directory3/subFile0");
        toAdd.add("directory3/subFile1");
        indexContent = Blob.read(index);

        Commit commit2 = new Commit("William0", "test commit 2");

        //test Tree
        assertEquals(indexContent, "Blob : 9f291810d041f2498391cf573e6bff718e479165 : subFile0Blob : e8fc73be476dbbfbf7a6540eb3954065f286987b : subFile1");

        toAdd.add("directory4");
        toAdd.add("directory5/subFile0");
        toAdd.add("directory5/subFile1");
        indexContent = Blob.read(index);

        Commit commit3 = new Commit("William0", "test commit 3");

        //test Tree
        assertEquals(indexContent, "Tree : a83aa66ff92d0affb67efd4d2e3a37037dd00b95 : directory4Blob : 06a5b6c99b6f3a9988b1b2de7242a957f1dc502f : subFile0Blob : 7a42ac5e76afd07524b885b18a43258eda2c8e7c : subFile1");

        //test commit shas
        BufferedReader readCommit = new BufferedReader(new FileReader("objects/" + commit0.getCommitSha()));
        String line = readCommit.readLine();

        assertEquals(line, "7542b35579da3b3b579af48972901683aaa42cba");
        line = readCommit.readLine();
        assertEquals(line, "");
        line = readCommit.readLine();
        assertEquals(line, commit1.getCommitSha());
        readCommit.close();

        readCommit = new BufferedReader(new FileReader("objects/" + commit1.getCommitSha()));
        line = readCommit.readLine();
        assertEquals(line, "bfba6efb80704f1e332d2f8de04d3d62646412bd");
        line = readCommit.readLine();
        assertEquals(line, commit0.getCommitSha());
        line = readCommit.readLine();
        assertEquals(line, commit2.getCommitSha());
        readCommit.close();

        readCommit = new BufferedReader(new FileReader("objects/" + commit2.getCommitSha()));
        line = readCommit.readLine();
        assertEquals(line, "81c22842e37dc388321be974f144f9c60ffcd19f");
        line = readCommit.readLine();
        assertEquals(line, commit1.getCommitSha());
        line = readCommit.readLine();
        assertEquals(line, commit3.getCommitSha());
        readCommit.close();

        readCommit = new BufferedReader(new FileReader("objects/" + commit3.getCommitSha()));
        line = readCommit.readLine();
        assertEquals(line, "ec45e0c15947dcb42da35c2308ababc1512be4c7");
        line = readCommit.readLine();
        assertEquals(line, commit2.getCommitSha());
        line = readCommit.readLine();
        assertEquals(line, "");
        readCommit.close();

        deleteFiles();
    }

}
