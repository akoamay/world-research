package ymok;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public class Server{
    public Server(){

    }

    public void start(){
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(1234));

        while(true){
            SocketChannel sc = ssc.accept();
            if ( sc != null ){

            }
        }

    }
}