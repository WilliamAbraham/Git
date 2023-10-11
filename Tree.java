import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class Tree {
    ArrayList<String> local;
    private String hash = "";
    private String directoryHash = "";

    public Tree(){
        Index toInit = new Index();
        toInit.init();
        local = new ArrayList<String>();
    }

    public Tree(String index) throws IOException{
        if (!index.equals("index")){
            return;
        }
        File myObj = new File("index");
        try {
            boolean deleted = checkForDeleted();
            if (deleted == true){
                formatIndex();
            }
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                this.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void formatIndex() throws IOException{
        File index = new File("index");
        File tempFile = new File("tempFile");

        BufferedReader reader = new BufferedReader(new FileReader(index));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;
        while ((currentLine = reader.readLine()) != null){
            if ((currentLine.length() > 8) && (!currentLine.substring(0, 8).equals("*Deleted"))){
                writer.append(currentLine + "\n");
            }
        }
        reader.close();
        writer.close();

        tempFile.renameTo(index);
    }

    public boolean checkForDeleted() throws IOException{
        boolean found = false;
        File index = new File("index");

        BufferedReader reader = new BufferedReader(new FileReader(index));
        String currentLine;
        while ((currentLine = reader.readLine()) != null){
            if (currentLine.contains("*Deleted")){
                return true;
            }
        }
        return found;
    }

    public void write(){

    }

    public String getHash() {
        return this.hash;
    }

    public void add(String content) throws IOException {        
        File Objects = new File("objects");
        if (!Objects.exists()) {
            Objects.mkdirs();
        }
        if (hash != "") {
            if (!checkIfUnique("objects/" + hash, content)) {
                return;
            }
            content = '\n' + content;
        }
        if (hash == "") {
            hash = Blob.encryptThisString(content.getBytes());
            write(hash, content.getBytes(), "objects", true);
        } else {
            File oldFile = new File("objects/" + hash);
            write(hash, content.getBytes(), "objects", true);
            hash = getSha(new File("objects/" + hash));
            File newFile = new File("objects/" + hash);
            oldFile.renameTo(newFile);
        }
    }

    public void stage(){

    }

    public static String read(String filename) throws FileNotFoundException {
        String submit = "";
        try (Scanner scan = new Scanner(new File(filename))) {
            while (scan.hasNext()) {
                String line = scan.nextLine().toString();
                submit += line;
                if (scan.hasNext()) {
                    submit += '\n';
                }
            }
        }
        return submit;
    }

    public static String getPreviousTree() throws IOException{
        File head = new File("Head");
        if (!head.exists()){
            return null;
        }
        BufferedReader reader = new BufferedReader(new FileReader(head));
        String commit = reader.readLine();
        reader.close();
        File commitFile = new File("objects/" + commit);
        reader = new BufferedReader(new FileReader(commitFile));
        String tree = reader.readLine();
        reader.close();
        return tree;
    }

    public void remove(String delteFileName) throws FileNotFoundException, IOException {
        String keepString = "";
        boolean didDeleteAnything = false;

        try (Scanner scan = new Scanner(new File("objects/" + hash))) {
            while (scan.hasNext()) {
                String line = scan.nextLine().toString();
                String[] split = line.split("\\s+");
                if (split[2].equals(delteFileName) || split[split.length - 1].equals(delteFileName)) {
                    didDeleteAnything = true;
                } else {
                    keepString += line;
                    keepString += '\n';
                }
            }
        }
        if (!didDeleteAnything) {
            return;
        }
        keepString = keepString.trim();

        // byte[] compressed = Blob.compress(keepString);
        File oldFile = new File("objects/" + hash);
        write(hash, keepString.getBytes(), "objects", false);
        hash = Blob.encryptThisString(keepString.getBytes());
        File newFile = new File("objects/" + hash);
        oldFile.renameTo(newFile);

    }

    public String getSha(File file) {
        String content = "";
        try {
            File myObj = file;
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                content = content + data;
                if (myReader.hasNextLine()) {
                    content += '\n';
                }

            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String encrypted = Blob.encryptThisString(content.getBytes());
        return encrypted;
    }

    public boolean checkIfUnique(String fileToSearchIn, String content) throws IOException {
        try (Scanner scan = new Scanner(new File(fileToSearchIn))) {
            while (scan.hasNext()) {
                String line = scan.nextLine().toString();
                if (line.equals(content)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void write(String fileName, byte[] content, String directory, boolean append) {
        try {
            try (FileOutputStream fos = new FileOutputStream("Objects/" + fileName, append)) {
                fos.write(content);
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public String addDirectory(String directory) throws IOException{
        File test = new File(directory);
        String toAdd = "";
        if (!test.isDirectory()){
            return "Input is not a directory";
        }
        Tree directoryBlobs = new Tree();

        String[] files = test.list();

        Blob addBlob;

        if (files.length == 0){
            toAdd = "";
            directoryBlobs.add(toAdd);
            return directoryBlobs.getHash();   
        }

        try {
			for (File subFile : test.listFiles()){
                if (subFile.isDirectory()){
                    toAdd = "tree : " + addDirectory(subFile.toString()) + " : " + subFile.toString().substring(subFile.toString().lastIndexOf("/") + 1);
                    directoryBlobs.add(toAdd);
                } else {
                    addBlob = new Blob(subFile.toString());
                    toAdd = "blob : " + addBlob.getHash() + " : " + subFile.toString().substring(subFile.toString().lastIndexOf("/") + 1);
                    directoryBlobs.add(toAdd);
                }
            }
		} catch (IOException e) {
			e.printStackTrace();
		}

        directoryHash = directoryBlobs.getHash();
        return directoryBlobs.getHash();   
    }

    public String getDirectoryHash(){
        return this.directoryHash;
    }
    
    public static String delete(String fileName, String initalTree) throws IOException{
        String cool = search(initalTree, fileName.substring(fileName.lastIndexOf("/") + 1));
        System.out.println(cool);
        return cool;
    }

    public static String search(String tree, String toSearch) throws IOException{
        String toWrite = "";
        File treeToSearch = new File(tree);
        
        BufferedReader toRead = new BufferedReader(new FileReader(treeToSearch));
        String currentLine = "";

        String treeSha = tree;
        while ((currentLine = toRead.readLine()) != null){
            if (currentLine.substring(0, 4).equals("tree")){
                treeSha = currentLine.substring(7, 47);
                toWrite += search("objects/" + treeSha, toSearch) + "\n";
                return toWrite;
            } 
            if (currentLine.substring(currentLine.lastIndexOf(":") + 2).equals(toSearch)){
                while ((currentLine = toRead.readLine()) != null){
                    toWrite += currentLine + "\n";
                }
                return toWrite;
            } else {
                toWrite += currentLine + "\n";
            }
        }
        toRead.close();

        System.out.println(toWrite);

        return toWrite;
    }

    public static String getTree(String commitSha) throws IOException{
        File commit = new File("objects/" + commitSha);
        BufferedReader reader = new BufferedReader(new FileReader(commit));
        String treeSha = reader.readLine();
        reader.close();
        return treeSha;
    }

    public static void delete(){
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
}
