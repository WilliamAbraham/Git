import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Formatter;
import java.util.Scanner;

public class Commit {
    private String shaPrevious = "";
    private String shaNext = "";
    private String author;
    private String date;
    private String summary;
    private Tree tree;
    private String fileContents;
    private Date dateObj;

    public static void main(String[] ags) throws IOException{
        Index toAddDirectory = new Index();
        toAddDirectory.init();
        toAddDirectory.add("directory");

        Commit toCommit = new Commit("a", "this is so cool");
    }

    //A commit constructor takes an optional String of the SHA1 of a parent Commit, and two Strings for author and summary
    public Commit (String author, String summary) throws IOException {
        Index toInit = new Index();
        toInit.init();
        this.tree = new Tree("index");
        this.author = author;   
        this.summary = summary;
        dateObj = new Date();
        date = dateObj.toString();
        createFile();
    }

    public Commit (String parent, String author, String summary) throws IOException {
        Index toInit = new Index();
        toInit.init();
        this.tree = new Tree("index");
        this.shaPrevious = parent;
        this.author = author;
        this.summary = summary;
        dateObj = new Date();
        date = dateObj.toString();
        createFile();
    }

    public void setContents() {
        this.fileContents = tree.getSha(new File("index"))
        + "\n" + shaPrevious
        + "\n" + shaNext
        + "\n" + author
        + "\n" + date
        + "\n" + summary;
    }

    public void createFile() throws IOException {
        setContents();
        File file = new File("objects/" + convertToSha1(fileContents));
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

    public String getCommitTree(String commitSha) throws FileNotFoundException{
        String commitTree = "";
        File commit = new File("objects/" + commitSha);
        Scanner myReader = new Scanner(commit);
        commitTree = myReader.nextLine();
        myReader.close();
        return commitTree;
    }
}
