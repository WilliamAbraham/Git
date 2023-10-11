import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
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
        File toDelete;
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 2; j++){
                toDelete = new File("subFile" + Integer.toString(i) + Integer.toString(j));
                toDelete.delete();
            }
        }
        File randomAhh = new File("subFile50");
        randomAhh.delete();
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

        cool.add("directory2/subFile20");
        cool.add("directory2/subFile21");
        cool.add("directory5/subFile50");

        Commit commit2 = new Commit("William Abraham", "this is commit 2");

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
        cool.delete("directory5/subFile50");
        
        Commit commit3 = new Commit("William Abraham", "this is commit 3");

        cool.add("directory3/subFile30");
        cool.add("directory3/subFile31");
        cool.delete("directory2/subFile21");
        
        Commit commit4 = new Commit("William Abraham", "this is commit 4");

        cool.delete("directory2/subFile20");
        cool.delete("directory3/subFile31");

        Commit commit5 = new Commit("William Abraham", "this is commit 5");

        //Checks contents of tree while traversing it
        String commitSha = commit5.getCommitSha();
        String treeOfCommit = Tree.getTree(commitSha);
        String treeContent = Blob.read(new File("objects/" + treeOfCommit));

        assertEquals("blob : 9f291810d041f2498391cf573e6bff718e479165 : subFile30"+ 
                    "tree : 78b448ed1671f2f5c19fb5c86f75f3dfd2257b56", treeContent);
        
        commitSha = commit5.getPreviousCommit();
        treeOfCommit = Tree.getTree(commitSha);
        treeContent = Blob.read(new File("objects/" + treeOfCommit));

        assertEquals("blob : 9f291810d041f2498391cf573e6bff718e479165 : subFile30" + 
                    "blob : e8fc73be476dbbfbf7a6540eb3954065f286987b : subFile31" + 
                    "blob : dd9565d7ef76d6a575f427c815e954428b70cf98 : subFile20" + 
                    "tree : 78b448ed1671f2f5c19fb5c86f75f3dfd2257b56", treeContent);
        
        commitSha = commit4.getPreviousCommit();
        treeOfCommit = Tree.getTree(commitSha);
        treeContent = Blob.read(new File("objects/" + treeOfCommit));

        assertEquals("blob : dd9565d7ef76d6a575f427c815e954428b70cf98 : subFile20" + 
                    "blob : 30ebce416a3997a8ebe00e113f8009587636ea9e : subFile21" + 
                    "tree : 78b448ed1671f2f5c19fb5c86f75f3dfd2257b56", treeContent);
        
        commitSha = commit3.getPreviousCommit();
        treeOfCommit = Tree.getTree(commitSha);
        treeContent = Blob.read(new File("objects/" + treeOfCommit));

        assertEquals("blob : 0f13d513e508da1f5211b481d77d9b6efc55ffff : subFile20" + 
                    "blob : 3683906ef3550284138cc0616d065621189d0dc1 : subFile21" + 
                    "blob : 06a5b6c99b6f3a9988b1b2de7242a957f1dc502f : subFile50" + 
                    "tree : 78b448ed1671f2f5c19fb5c86f75f3dfd2257b56", treeContent);
        
        commitSha = commit2.getPreviousCommit();
        treeOfCommit = Tree.getTree(commitSha);
        treeContent = Blob.read(new File("objects/" + treeOfCommit));

        assertEquals("blob : cee88b236a3f765ebe7711008dfb24fd5b9bcd6d : subFile00" + 
                    "blob : 7add34e050b507e13049a7e68888f2805d2b4e8f : subFile01" + 
                    "blob : 9567eb55822e84fd927f38e325ea730d4a1c2658 : subFile10" + 
                    "blob : 63cc87bc35cf08a3cad48f62168678700c3643f0 : subFile11", treeContent);
        
        //Checkout on commit 2
        Blob.checkOut(commit3.getPreviousCommit());

        recursivelyCheck(Tree.getTree(commit3.getPreviousCommit()));
        deleteFiles();
    }

    public void recursivelyCheck(String treeSha) throws IOException{
        File treeToRead = new File("objects/" + treeSha);
        File toCheck;
        BufferedReader reader = new BufferedReader(new FileReader(treeToRead));
        String currentLine;
        while ((currentLine = reader.readLine()) != null){
            if (currentLine.contains("tree :")){
                recursivelyCheck(currentLine.substring(7, 47));
            }
            if (currentLine.contains ("blob :")){
                toCheck = new File(currentLine.substring(50));
                assertTrue(toCheck.exists());
                String checkContents = Blob.decompress("objects/" + currentLine.substring(7, 47));
                String actualContents = Blob.read(toCheck);
                assertEquals(checkContents, actualContents);
                
            }
        }
        reader.close();
    }
}
