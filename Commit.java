import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Formatter;

public class Commit {
    private String shaPrevious = "";
    private String shaNext = "";
    private String author;
    private String date;
    private String summary;
    private Tree tree;
    private String fileContents;
    private Date dateObj;
    private String commitSha = "";

    //A commit constructor takes an optional String of the SHA1 of a parent Commit, and two Strings for author and summary
    public Commit (String author, String summary) throws IOException {
        File head = new File("Head");
        if (head.exists()){
            shaPrevious = Blob.read(head);
        }
        Index toInit = new Index();
        toInit.init();
        this.tree = new Tree("index", getPreviousTreeSha());
        this.author = author;   
        this.summary = summary;
        dateObj = new Date();
        date = dateObj.toString();
        createFile();
        setNext();
        overWriteHead();
        toInit.clear();

        File temp = new File("tempIndex");
        temp.delete();
    }

    public Commit (String parent, String author, String summary) throws IOException {
        Index toInit = new Index();
        toInit.init();
        this.tree = new Tree("index", getPreviousTreeSha());
        this.shaPrevious = parent;
        this.author = author;
        this.summary = summary;
        dateObj = new Date();
        date = dateObj.toString();
        createFile();
        setNext();
        overWriteHead();
        toInit.clear();
        File temp = new File("tempIndex");
        temp.delete();
    }

    public void setContents() {
        this.fileContents = this.tree.getHash()
        + "\n" + shaPrevious
        + "\n" + shaNext
        + "\n" + author
        + "\n" + date
        + "\n" + summary;
    }

    public void createFile() throws IOException {
        setContents();
        commitSha = convertToSha1(fileContents);
        File file = new File("objects/" + commitSha);
        if (!file.exists()) {
            file.createNewFile();
        }
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        pw.print(fileContents);
        pw.close();
    }

    public String getDate() {
        return date;
    }

    public static String convertToSha1(String fileContents) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(fileContents.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sha1;
    }

    // Used for sha1
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public String getFileContents(){
        return fileContents;
    }

    public void init() throws IOException{
        File head = new File("Head");
        if (!head.exists()){
            head.createNewFile();
        } else {
            head.delete();
            head.createNewFile();
        }
    }

    public void overWriteHead() throws IOException{
        FileWriter myWriter = new FileWriter("Head");
        myWriter.write(commitSha);
        myWriter.close();
    }

    public String getCommitSha(){
        return commitSha;
    }

    public void setNext() throws IOException{
        File head = new File("Head");
        if (!head.exists()){
            return;
        }

        File inputFile = new File("objects/" + shaPrevious);
        File tempFile = new File("objects/myTempFile");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine = "";
        for (int i = 0; i < 5; i++){
            if (i == 2){
                currentLine = commitSha;
                writer.write(currentLine + "\n");
                currentLine = reader.readLine();
            } else {
                currentLine = reader.readLine();
                writer.write(currentLine + "\n");
            }
        }
        currentLine = reader.readLine();
        writer.write(currentLine);

        writer.close();
        reader.close();
        boolean successful = tempFile.renameTo(inputFile);
    }

    public String getPreviousTreeSha() throws IOException{
        if (shaPrevious == ""){
            return "";
        }
        File previousCommit = new File("objects/" + shaPrevious);
        BufferedReader reader = new BufferedReader(new FileReader(previousCommit));
        String previousTree = reader.readLine();
        reader.close();
        return "tree : " + previousTree;
    }
}
