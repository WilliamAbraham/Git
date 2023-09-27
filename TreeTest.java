import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;

public class TreeTest {
    public void deleteDirectory(String fileName){
        File directory = new File(fileName);
        if (!directory.exists()){
            return;
        }
        for (File subFile : directory.listFiles()){
            if (subFile.isDirectory()){
                deleteDirectory(subFile.toString());
            }
            subFile.delete();
        }
        directory.delete();
    }

    @Test
    public void testAdd() throws IOException {
        Tree tree = new Tree();
        tree.add("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");
        tree.add("blob : 01d82591292494afd1602d175e165f94992f6f5f : file2.txt");
        tree.add("blob : f1d82236ab908c86ed095023b1d2e6ddf78a6d83 : file3.txt");
        tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");
        tree.add("tree : e7d79898d3342fd15daf6ec36f4cb095b52fd976");

        // makes sure hash is correct
        assertEquals("649a3d7f1b034f8ec9954b7411c63818475b2385", tree.getHash());

        // tests if content is right
        assertEquals(
                "blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt\n" + //
                        "blob : 01d82591292494afd1602d175e165f94992f6f5f : file2.txt\n" + //
                        "blob : f1d82236ab908c86ed095023b1d2e6ddf78a6d83 : file3.txt\n" + //
                        "tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b\n" + //
                        "tree : e7d79898d3342fd15daf6ec36f4cb095b52fd976",
                Tree.read("objects/" + tree.getHash()));

        // cleanup cause the teardown didnt work for somereason:
        Files.deleteIfExists(Paths.get("objects/" + tree.getHash()));

        //Final teardown
        File index = new File("index");
        index.delete();
        deleteDirectory("directory");
        deleteDirectory("objects");

    }

    @Test
    public void testRemove() throws IOException {
        Tree tree = new Tree();
        tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");

        tree.add("tree : e7d79898d3342fd15daf6ec36f4cb095b52fd976");
        tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");
        tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");
        tree.add("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");
        tree.add("blob : 01d82591292494afd1602d175e165f94992f6f5f : file2.txt");
        tree.remove("bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");
        tree.remove("file1.txt");

        // tests hash
        assertEquals("aa3abb9509cfed41b4cda151c92e31bcd054d311", tree.getHash());

        // tests content being rihgt
        assertEquals(
                "tree : e7d79898d3342fd15daf6ec36f4cb095b52fd976\nblob : 01d82591292494afd1602d175e165f94992f6f5f : file2.txt",
                Tree.read("objects/" + tree.getHash()));

        // cleanup cause the teardown didnt work for somereason:
        Files.deleteIfExists(Paths.get("objects/" + tree.getHash()));

        //Final teardown
        File index = new File("index");
        index.delete();
        deleteDirectory("directory");
        deleteDirectory("objects");

    }

    @Test
    public void testAddDirectoryBasic() throws IOException{
        File directory = new File("directory");
        directory.mkdir();
        File subFile;
        for (int i = 0; i < 3; i++){
            subFile = new File("directory/subFile" + i);
            subFile.createNewFile();
            FileWriter myWriter = new FileWriter(subFile.toString());
            myWriter.write("this is super FUN!!!!" + i);
            myWriter.close();
        }
        
        Tree test = new Tree();
        test.addDirectory("directory");

        File objectSubFile = new File("objects/d9fdb466f67c7875463e717878ee3404102f3e26");

        assertTrue(objectSubFile.exists());

        //Final teardown
        File index = new File("index");
        index.delete();
        deleteDirectory("directory");
        deleteDirectory("objects");
    }

    @Test
    public void testAddDirectoryAdvanced() throws IOException{
        File directory = new File("directory");
        directory.mkdir();
        File subFile;
        for (int i = 0; i < 3; i++){
            subFile = new File("directory/subFile" + i);
            subFile.createNewFile();
            FileWriter myWriter = new FileWriter(subFile.toString());
            myWriter.write("this is alkjsdfhasldfja;sldfkj FUN!!!!" + i);
            myWriter.close();
        }
        File subDirectory;
        for (int i = 0; i < 2; i++){
            subDirectory = new File("directory/subDirectory" + i);
            subDirectory.mkdir();
        }
        File subSubFile = new File("directory/subDirectory0/peepeepoopoo");
        FileWriter myWriter = new FileWriter(subSubFile.toString());
            myWriter.write("it is what it is");
            myWriter.close();

        Tree test = new Tree();
        test.addDirectory("directory");

        File mainTree = new File("objects/" + test.getHash());
        assertTrue(mainTree.exists());

        //Final teardown
        File index = new File("index");
        index.delete();
        deleteDirectory("directory");
        deleteDirectory("objects");
    }
}
