import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {

    static final int PORT_NUMBER = 60123;
    static final String HOST_NAME = "localhost";
    static final int BUFFER_SIZE = 1024;
    static final String CLOSING_CONNECTION = "STOP";

    public static void main(String[] args) throws IOException {

        //Stream used to read from keyboard
        BufferedReader tastiera = new BufferedReader(new InputStreamReader(System.in));

        //Buffer to make possible writing and reading operations
        ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

        //Connecting to remote Server
        SocketChannel activeSocket = SocketChannel.open();
        activeSocket.connect(new InetSocketAddress(HOST_NAME, PORT_NUMBER));

        System.out.println("Send any message or write STOP to disconnect from the Server ");

        while (true) {
            //Sending a message to server
            String toServer = null;
            while((toServer = tastiera.readLine()).equals(""));
            buffer.clear();
            buffer.put(toServer.getBytes());
            buffer.flip();
            activeSocket.write(buffer);
            if (toServer.equalsIgnoreCase(CLOSING_CONNECTION)) {
                System.out.println("Closing connection...");
                System.out.println("Server: See you later..." + activeSocket.getRemoteAddress());
                activeSocket.close();
                break;
            }

            //Reading the server reply:
            buffer.clear();
            activeSocket.read(buffer);
            StringBuilder fromServer = new StringBuilder();
            buffer.flip();
            while (buffer.hasRemaining()) {
                fromServer.append((char) buffer.get());
            }
            System.out.println("Server: " + fromServer);
        }
    }
}
