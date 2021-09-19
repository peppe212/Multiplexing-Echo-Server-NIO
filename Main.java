import java.io.IOException;

public class Main {

    private final static int PORT_NUMBER = 60123;
    private final static String HOST_NAME = "localhost";
    private final static int BUFFER_SIZE = 1024;
    private final static String CLOSING_CONNECTION = "STOP";

    public static void main(String[] args) {
        try {
            new Server(HOST_NAME, PORT_NUMBER, BUFFER_SIZE, CLOSING_CONNECTION).begin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}//end_class
