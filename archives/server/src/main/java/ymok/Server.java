package ymok;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.Socket;


public class Server{

    ExecutorService exec = Executors.newCachedThreadPool();

    public Server(){

    }

    public void start(){
        ServerSocketChannel ssc = null;
        ServerSocket svs = null;

        try{
            svs = new ServerSocket(1234);
//            ssc = ServerSocketChannel.open();
 //           ssc.socket().bind(new InetSocketAddress(1234));

            while(true){
                System.out.println("waiting");
//                SocketChannel sc = ssc.accept();
                Socket sock = svs.accept();
                System.out.println("accepted");
                    exec.submit( new ServerConnectionTask(sock) );
                /*
                if ( sc != null ){
                    exec.submit( new ServerConnectionTask(sc) );
                }
                */
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if (ssc != null && ssc.isOpen()){
                try{
                    System.out.println("Stop server.");
                    ssc.close();
                }catch(IOException e){}
            }
        }

    }
}