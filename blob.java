import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.io.ByteArrayOutputStream;
import java.util.zip.*;
import java.io.FileReader;
import java.io.BufferedReader;

public class Blob {
    private String hash;
    private byte[] compressedContent;

    public static String decompress(String path) throws FileNotFoundException, IOException {
        try (
            FileInputStream fis = new FileInputStream(path);
            GZIPInputStream gis = new GZIPInputStream(fis);
            ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toString("UTF-8");
        }
    }

    // Creates a blob, which is a has of the compressed
    // contents of a given file, then writes it to objects folder
    public Blob(String fileName) throws IOException {
        Index init = new Index();
        init.init();
        hash = hash(fileName);
        write(hash, compressedContent, "objects");
    }

    public Blob(String fileName , boolean toWrite) throws IOException {
        Index init = new Index();
        init.init();
        hash = hash(fileName);
        if (toWrite == true){
            write(hash, compressedContent, "objects");
        }
    }

    public Blob(){
        
    }

    public String hash(String fileName) throws IOException{
        File obj = new File(fileName);
        String content = read(obj);
        compressedContent = compress(content);
        String methodHash = encryptThisString(compressedContent);
        return methodHash;
    }

    public String getHash() {
        return hash;
    }

    // Returns compressed version of String
    public static byte[] compress(String str) throws IOException {
        if ((str == null) || (str.length() == 0)) {
            return new byte[0];
        }
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(str.getBytes("UTF-8"));
        gzip.flush();
        gzip.close();
        return obj.toByteArray();
    }

    // Hashes String
    public static String encryptThisString(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(input);
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 40) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encryptThisString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 40) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Reads a file and returns it as a String
    public static String read(File txt) {
        String content = "";
        try {
            File myObj = txt;
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                content = content + data;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return content;
    }

    // Writes to given directory
    public static void write(String fileName, byte[] content, String directory) {
        try {
            try (FileOutputStream fos = new FileOutputStream("Objects/" + fileName)) {
                fos.write(content);
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    // Write to local directory
    public static void write(String fileName, String content) {
        try {
            File file = new File(fileName);
            FileWriter fw = new FileWriter(file);
            fw.write(content);
            fw.close();
            System.out.println("Successfully wrote to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Checkout writes reverted files to local path
    public static void checkOut(String sha) throws IOException{
        checkOutRecursion(Tree.getTree(sha));
    }

    public static void checkOutRecursion(String sha) throws IOException{
        File tree = new File("objects/" + sha);
        File toCreate;
        BufferedReader reader = new BufferedReader(new FileReader(tree));;
        FileWriter writer;
        String currentLine;
        while((currentLine = reader.readLine()) != null){
            if (currentLine.contains("tree")){
                checkOutRecursion(currentLine.substring(7, 47));
            } 
            if (currentLine.contains("blob")){
                toCreate = new File(currentLine.substring(50));
                toCreate.createNewFile();
                String contentToWrite = decompress("objects/" + currentLine.substring(7, 47));
                writer = new FileWriter(toCreate);
                writer.append(contentToWrite);
                writer.close();
            }
        }
        reader.close();
    }
}