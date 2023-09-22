import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Tree {

    private String hash = "";

    public static void main(String[] args) throws IOException {
        Tree t = new Tree();
        t.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");
        t.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");
        t.add("tree : penis");
        t.add("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");
        t.remove("file1.txt");

    }

    public String getHash() {
        return hash;
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
        System.out.println(content);
        // byte[] compressed = Blob.compress(content);
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

    public static void write(String fileName, byte[] content, String directory, boolean append) {
        try {
            try (FileOutputStream fos = new FileOutputStream("Objects/" + fileName, append)) {
                fos.write(content);
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
