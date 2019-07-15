package ymok;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.net.Socket;

class ServerConnectionTask implements Callable<Void>, MessageListener{
    Socket sc = null;

    public ServerConnectionTask(Socket sc ){
        this.sc = sc;
    }

    @Override
    public void onMessage(Message message){
        System.out.println("");
        System.out.println("=== receive ===");
        System.out.println(message.map);
    }

    @Override
    public Void call(){
        MessageReceiver receiver = new MessageReceiver(sc,this);
        receiver.start();

        try{
            while(true){
                try{
                    Thread.sleep(1000);
                    System.out.print(".");
                }catch( Exception e ){
                    e.printStackTrace();
                }
                /*
                    ObjectInputStream ois = new ObjectInputStream(sc.socket().getInputStream());
                    Message message = (Message)ois.readObject();
                    System.out.println(message.map);

                    ObjectOutputStream oos = new ObjectOutputStream(sc.socket().getOutputStream());
                    oos.writeObject(message);
                    oos.flush();

                    */

            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally{
            //System.out.println( remoteAddr + ":disconnected");
            if ( sc != null && sc.isConnected()){
                try{
                    sc.close();
                }catch(IOException e){}
            }
        }

    }

}