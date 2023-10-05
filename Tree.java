import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    public Tree(String index, String previousTree) throws IOException{
        if (!index.equals("index")){
            return;
        }
        File myObj = new File("index");
        try {
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                this.add(data);
            }
            if (previousTree != ""){
                this.add(previousTree);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
}
