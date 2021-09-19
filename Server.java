import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private final String HOSTNAME;
    private final int PORT_NUMBER;
    private final int BUFFER_SIZE;
    private final String TERMINATE;
    private final ServerSocketChannel listenSocket;
    private final Selector selector;
    private int clientCounter = 0;

    public Server(String hostname, int portnumber, int buffersize, String terminate) throws IOException {
        this.HOSTNAME = hostname;
        this.PORT_NUMBER = portnumber;
        this.BUFFER_SIZE = buffersize;
        this.TERMINATE = terminate;
        this.listenSocket = ServerSocketChannel.open();
        this.listenSocket.bind(new InetSocketAddress(HOSTNAME,PORT_NUMBER));
        this.listenSocket.configureBlocking(false);
        this.selector = Selector.open();
        this.listenSocket.register(this.selector,SelectionKey.OP_ACCEPT,null);
    }

    public void begin() throws IOException{
        System.out.println("Server ready to accept incoming connections...");
        while(true) {
            if (this.selector.select() == 0)
                continue;
            Set<SelectionKey> selectedKeys = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isAcceptable()){
                    acceptingClient();
                }
                else if(key.isReadable()){
                    readingFromClient(key);
                }
                else if(key.isWritable()){
                    writingToClient(key);
                }
            }
        }//end_main_while
    }//end_main_server_method

    private void acceptingClient() throws IOException{
        SocketChannel activeSocket = listenSocket.accept();
        activeSocket.configureBlocking(false);
        this.clientCounter++;
        System.out.println(clientCounter+" Client connected!");
        ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        activeSocket.register(this.selector,SelectionKey.OP_READ,buffer);
    }

    private void readingFromClient(SelectionKey key) throws IOException{
        if(key == null)
            throw new NullPointerException();
        SocketChannel activeSocket = (SocketChannel) key.channel();
        activeSocket.configureBlocking(false);
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        buffer.clear();
        activeSocket.read(buffer);
        StringBuilder toServer = new StringBuilder();
        buffer.flip();
        while(buffer.hasRemaining()){
            toServer.append((char)buffer.get());
        }
        if(toServer.toString().equalsIgnoreCase(TERMINATE)){
            System.out.println("Disconnecting from client "+activeSocket.getRemoteAddress());
            activeSocket.close();
        }
        else {
            System.out.println("Client: " + toServer);
            activeSocket.register(this.selector, SelectionKey.OP_WRITE, buffer);
        }
    }

    private void writingToClient(SelectionKey key) throws IOException{
        if(key == null)
            throw new NullPointerException();
        SocketChannel activeSocket = (SocketChannel) key.channel();
        activeSocket.configureBlocking(false);
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        buffer.flip();
        activeSocket.write(buffer);

        //activeSocket.close();
        activeSocket.register(this.selector,SelectionKey.OP_READ,buffer);
    }

}
