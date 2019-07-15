package ymok;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

class Client implements MessageListener{
    Socket sc = null;

    @Override
    public void onMessage(Message message){
        System.out.println("");
        System.out.println("=== receive ===");
        System.out.println(message.map);
    }

    public Client(){}

    public void start(){

        try{
//            sc = SocketChannel.open(new InetSocketAddress("localhost", 1234));
            sc = new Socket("localhost",1234);


            BufferedReader keyin = new BufferedReader(new InputStreamReader(System.in));
            MessageReceiver receiver = new MessageReceiver(sc, this);
            receiver.start();

            while( sc.isConnected() ){

                String line = keyin.readLine();

                HashMap map = new HashMap();
                map.put("foo", "bar");
                map.put("hoge", "piyo");
                Message message = new Message("test", map);

                System.out.println( "aaaa");
                ObjectOutputStream oos = new ObjectOutputStream(sc.getOutputStream());
                System.out.println( "bbb");
                oos.writeObject(message);
                System.out.println( "ccc");
                oos.flush();
                System.out.println( "ddd");

                System.out.println("=== send ===");
                System.out.println(message.map);
            }


        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if ( sc == null && sc.isConnected() ){
                try{
                    sc.close();
                }catch(IOException e){}
            }
        }
    }
}