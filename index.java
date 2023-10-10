import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class Index {
    //initilizes Repository with an index.txt and Objects dir
    public void init(){
        File index = new File("index");
        if (!index.exists()) {
            Blob.write("index", "");
        }
        File Objects = new File("objects");
        if (!Objects.exists()) {
            Objects.mkdirs();
        }
    }

    public void add(String fileName) throws IOException{
        File test = new File(fileName);
        if (test.isDirectory()){
            addDirectory(fileName);
            return;
        }
        //Creates a Blob of fileName that gets added to Objects
        Blob blob = new Blob(fileName);
        String toAdd = "blob : " + blob.getHash() + " : " + fileName.substring(fileName.lastIndexOf("/") + 1);
        if (!checkIfUnique("index", toAdd)) {
            System.out.println("File Found");
            return;
        }
        try (FileWriter file = new FileWriter("index", true)){
            file.append(toAdd + "\n");
        }

        //This links the commit's trees
        String toAddTree = Tree.getPreviousTree();
        File index = new File("index");
        File tempIndex = new File("tempIndex");
        BufferedReader reader = new BufferedReader(new FileReader(index));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempIndex));
        String currentLine;
        if (toAddTree != null){
            while ((currentLine = reader.readLine()) != null){
                if (!currentLine.equals("tree : " + toAddTree)){
                    writer.append(currentLine + "\n");
                }
            }
                writer.append("tree : " + toAddTree + "\n");
                reader.close();
                writer.close();
                tempIndex.renameTo(index);
        }
    }

    public void addDirectory(String fileName) throws IOException{
        File index = new File("index");
        if (index.length() == 0){
            String toAdd = Tree.getPreviousTree();
            if (toAdd != null){
                addSimple("tree : " + toAdd, true);
            }
        }
        Tree directory = new Tree();
        String sha = directory.addDirectory(fileName);
        try (FileWriter file = new FileWriter("index", true);
                BufferedWriter b = new BufferedWriter(file);
                PrintWriter p = new PrintWriter(b);) {
                String toAdd = "tree : " + sha + " : " + fileName;
                if (!checkIfUnique("index", toAdd)) {
                    System.out.println("File Found");
                    return;
                } else {
                    p.println(toAdd);
                }
        }
    }

    public static void main(String[] args) throws IOException{
        Index cool = new Index();
        // cool.add("directory4/subFile40");

        cool.delete("directory0/subFile00");
        cool.delete("directory2/subFile21");
    }

    public void delete(String fileName) throws IOException{
        File index = new File("index");

        BufferedReader readerOuter = new BufferedReader(new FileReader(index));
        if (readerOuter.readLine() == null){
            String deletedTree = Tree.delete(fileName, "objects/" + Tree.getPreviousTree());
            String[] thingsToAdd = deletedTree.split("\n");
            for (int i = 0; i < thingsToAdd.length; i++){
                addSimple(thingsToAdd[i], true);
            }
            readerOuter.close();
            return;
        }
        readerOuter.close();

        String deletedTree = Tree.delete(fileName, "index");
        String[] thingsToAdd = deletedTree.split("\n");
    
        String currentIndexContents = read(index);
        String[] currentIndexContentSplit = currentIndexContents.split("\n");

        if (!deleteMoreRecentTreeCheck(thingsToAdd, currentIndexContentSplit)){
            File tempIndex = new File("indexDelete");
            BufferedReader reader = new BufferedReader(new FileReader(index));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempIndex));
            String currentLine;
            while ((currentLine = reader.readLine()) != null){
                if (!currentLine.substring(0, 4).equals("tree")){
                    writer.append(currentLine + "\n");
                }
            }
        
            reader.close();
            writer.close();
            tempIndex.renameTo(index);
        
            for (int i = 0; i < thingsToAdd.length; i++){
                addSimple(thingsToAdd[i], true);
            }
        } else {
            // for (int i = 0; i < thingsToAdd.length; i++){
            //     addSimple(thingsToAdd[i], false);
            // }
            addArray(thingsToAdd);
        }
    }

    public void addArray(String[] contents) throws IOException{
        File index = new File("index");
        FileWriter writer = new FileWriter(index);
        for (int i = 0; i < contents.length; i++){
            writer.write(contents[i] + "\n");
        }
        writer.close();
    }

    public static boolean deleteMoreRecentTreeCheck(String[] contents1, String[] contents2){
        for (int i = 0; i < contents1.length; i++){
            if (contents1[i].contains("tree")){
                for (int k = 0; k < contents2.length; k++){
                    if (contents1[i].equals(contents2[k])){
                        return true;
                    }
                }
            }
        }
        for (int i = 0; i < contents1.length; i++){
            if (contents1[i].contains("tree")){
                return false;
            }
        }
        return true;
    }

    public void addSimple(String toAdd, boolean append) throws IOException{
        File index = new File("index");
        FileWriter writer = new FileWriter(index, append);
        if (checkIfUnique("index", toAdd)){
            writer.append(toAdd + "\n");
        }
        writer.close();
    }

    public void remove(String fileName) throws IOException{
        File inputFile = new File("index");
        File tempFile = new File("myTempFile.txt");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        File toRead = new File(fileName);
        String lineToRemove = "blob : " + Blob.encryptThisString(Blob.compress(Blob.read(toRead))) + " : " + fileName;
        String currentLine;

        while ((currentLine = reader.readLine()) != null) {
            // trim newline when comparing with lineToRemove
            String trimmedLine = currentLine.trim();
            if (trimmedLine.equals(lineToRemove))
                continue;
            writer.write(currentLine + System.getProperty("line.separator"));
        }
        writer.close();
        reader.close();
        boolean successful = tempFile.renameTo(inputFile);
    }

    public boolean checkIfUnique(String fileToSearchIn, String toSearch) throws IOException{
        Scanner scan = new Scanner(new File(fileToSearchIn));
        while (scan.hasNext()) {
            String line = scan.nextLine().toString();
            System.out.println(line.equals(toSearch));
            if (line.equals(toSearch)) {
                return false;
            }
        }
        return true;
    }

    public void clear() throws IOException{
        File index = new File("index");
        index.delete();
        index.createNewFile();
    }

    public static String read(File txt) throws FileNotFoundException {
        String content = "";
            File myObj = txt;
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine() + "\n";
                content = content + data;
            }
            myReader.close();
        return content;
    }
}
