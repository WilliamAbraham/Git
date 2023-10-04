import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
        assertEquals(line, "a21c1a01468d39deaab7a846669af06cc7b77f39");
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
        assertEquals(indexContent, "tree : eb810977429291e80ade077efa29d8ca535ffb53 : directory0blob : 9567eb55822e84fd927f38e325ea730d4a1c2658 : subFile0");
        
        
        toAdd.add("directory2");
        toAdd.add("directory1/subFile1");
        String indexContent1 = Blob.read(index);

        indexContent = Blob.read(index);

        Commit commit1 = new Commit("William1", "test commit 1"); 

        //test Tree contents
        assertEquals(indexContent, "tree : 114a8595cf285bca43f52f5ec140d469c43604c8 : directory2blob : 63cc87bc35cf08a3cad48f62168678700c3643f0 : subFile1");
       
        //test commit shas
        BufferedReader readCommit = new BufferedReader(new FileReader("objects/" + commit0.getCommitSha()));
        String line = readCommit.readLine();

        assertEquals(line, "dfe585f56baef5f098528b7bb96372334e6b161c");
        line = readCommit.readLine();
        assertEquals(line, "");
        line = readCommit.readLine();
        assertEquals(line, commit1.getCommitSha());
        readCommit.close();

        readCommit = new BufferedReader(new FileReader("objects/" + commit1.getCommitSha()));
        line = readCommit.readLine();
        assertEquals(line, "04515b16c41af1e3dcafeec3180a30294a358450");
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
        assertEquals(indexContent, "blob : cee88b236a3f765ebe7711008dfb24fd5b9bcd6d : subFile0blob : 7add34e050b507e13049a7e68888f2805d2b4e8f : subFile1");
        
        toAdd.add("directory1");
        toAdd.add("directory2/subFile0");
        toAdd.add("directory2/subFile1");
        indexContent = Blob.read(index);

        Commit commit1 = new Commit("William0", "test commit 1");

        //test Tree
        assertEquals(indexContent, "tree : 17431eb266087514f8c51f90fdd240146b9fddad : directory1blob : 0f13d513e508da1f5211b481d77d9b6efc55ffff : subFile0blob : 3683906ef3550284138cc0616d065621189d0dc1 : subFile1");
        
        toAdd.add("directory3/subFile0");
        toAdd.add("directory3/subFile1");
        indexContent = Blob.read(index);

        Commit commit2 = new Commit("William0", "test commit 2");

        //test Tree
        assertEquals(indexContent, "blob : 9f291810d041f2498391cf573e6bff718e479165 : subFile0blob : e8fc73be476dbbfbf7a6540eb3954065f286987b : subFile1");
        
        toAdd.add("directory4");
        toAdd.add("directory5/subFile0");
        toAdd.add("directory5/subFile1");
        indexContent = Blob.read(index);

        Commit commit3 = new Commit("William0", "test commit 3");

        //test Tree
        assertEquals(indexContent, "tree : 86e7ecaf37021c86eea3f4b335094b128665fafe : directory4blob : 06a5b6c99b6f3a9988b1b2de7242a957f1dc502f : subFile0blob : 7a42ac5e76afd07524b885b18a43258eda2c8e7c : subFile1");
        
        //test commit shas
        BufferedReader readCommit = new BufferedReader(new FileReader("objects/" + commit0.getCommitSha()));
        String line = readCommit.readLine();
        assertEquals(line, "a21c1a01468d39deaab7a846669af06cc7b77f39");
        line = readCommit.readLine();
        assertEquals(line, "");
        line = readCommit.readLine();
        assertEquals(line, commit1.getCommitSha());
        readCommit.close();

        readCommit = new BufferedReader(new FileReader("objects/" + commit1.getCommitSha()));
        line = readCommit.readLine();
        assertEquals(line, "d6f332cce2b66f6db091b8e41544f41756a2b213");
        line = readCommit.readLine();
        assertEquals(line, commit0.getCommitSha());
        line = readCommit.readLine();
        assertEquals(line, commit2.getCommitSha());
        readCommit.close();

        readCommit = new BufferedReader(new FileReader("objects/" + commit2.getCommitSha()));
        line = readCommit.readLine();
        assertEquals(line, "f5da699b539aef876e2d18a16d98daf28141c9e5");
        line = readCommit.readLine();
        assertEquals(line, commit1.getCommitSha());
        line = readCommit.readLine();
        assertEquals(line, commit3.getCommitSha());
        readCommit.close();

        readCommit = new BufferedReader(new FileReader("objects/" + commit3.getCommitSha()));
        line = readCommit.readLine();
        assertEquals(line, "f1195fde20ba75335996bab1618d9a98fff17eb2");
        line = readCommit.readLine();
        assertEquals(line, commit2.getCommitSha());
        line = readCommit.readLine();
        assertEquals(line, "");
        readCommit.close();

        deleteFiles();
    }
}
