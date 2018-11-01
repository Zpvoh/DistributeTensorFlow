import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public final class Constants {
    public static final Integer WORKER_REQUIRE_PARAMETER=0;
    public static final Integer WORKER_SEND_PARAMETER=1;

    public static final Integer KEEP_ALIVE=0;
    public static final Integer CLOSE_PS=1;

    public static final int READY=1;

    public static final int NUM_VALIDATE=80;
    public static final int BATCH_SIZE=10;
    public static final int TRAIN_TIMES=5000;

    public static HashMap<String, String> configs=new HashMap<>();

    public static void readConfig(){
        try {
            FileInputStream configReader=new FileInputStream("config");
            Scanner scanner=new Scanner(configReader);
            while(scanner.hasNext()){
                String key=scanner.next();
                String value=scanner.next();
                configs.put(key, value);
            }
            scanner.close();
            configReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("There is no configuration file");
        } catch (IOException e) {
            System.out.println("IO problem.");
        }
    }
}
