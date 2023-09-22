import java.io.IOException;

public class CommitTester {
    public static void main (String [] args) throws IOException {
        Commit commit = new Commit("839d9cca14977875ebf2471638392aef7f0e0159", "natalie lim", "this is so fun");
        commit.createFile();
        System.out.println (commit.getDate());
    }
}
