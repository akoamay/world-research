package ymok;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.SocketChannel;
import java.net.Socket;

class MessageReceiver extends Thread{
    private Socket sc = null;
    private boolean isStop = false;
    private MessageListener listener = null;



    public MessageReceiver( Socket sc, MessageListener listener ){
        this.sc = sc;
        this.listener = listener;
    }

    public void run(){
        while ( !isStop ){
            try{
                ObjectInputStream ois = new ObjectInputStream(sc.getInputStream());
                Message message = (Message)ois.readObject();
                listener.onMessage(message);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void terminate(){
        isStop = true;
    }


}