import java.io.File;
import java.io.IOException;

public class Head {
    String commitSha = "";

    public void init() throws IOException{
        File head = new File("head");
        if (!head.exists()){
            head.createNewFile();
        }
    }

    public void overWriteHead(){
        File head = new File("head");
        // head.
    }

}
