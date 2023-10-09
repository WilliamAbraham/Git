import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class Index {
    private boolean deleted = false;
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
        try (FileWriter file = new FileWriter("index", true);
            BufferedWriter b = new BufferedWriter(file);
            PrintWriter p = new PrintWriter(b);) {
            p.println(toAdd);
        }
    }

    public void addDirectory(String fileName) throws IOException{
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
        cool.add("directory0/subFile00");
        cool.add("directory0/subFile01");

        Commit one = new Commit("Will", "1");

        cool.delete("directory0/subFile00");
        cool.add("directory1/subFile10");

        Commit two = new Commit("Will", "2");
    }

    public void delete(String fileName) throws IOException{
        String deletedTree = Tree.delete(fileName);
        File index = new File("index");
        FileWriter writer = new FileWriter(index, true);
        writer.append(deletedTree);
        writer.append("*Deleted" + fileName.substring(fileName.lastIndexOf("/") + 1) + "\n");
        writer.close();
        this.deleted = true;
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
}
