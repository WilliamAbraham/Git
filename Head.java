import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Head {
    String commitSha = "";

    public void init() throws IOException{
        File head = new File("head");
        if (!head.exists()){
            head.createNewFile();
        }
    }

    public void overWriteHead(String shaString) throws IOException{
        init();
        commitSha = shaString;
        FileWriter myWriter = new FileWriter("head");
        myWriter.write(shaString);
        myWriter.close();
    }

}
