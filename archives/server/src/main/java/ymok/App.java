package ymok;

import java.util.ArrayList;

public class App 
{
    public static void main( String[] args )
    {
        new App(args[0]);
    }
    public App(String mode){
        if ( mode.equals("s")){
            Server server = new Server();
            server.start();
        }else if( mode.equals("c") ){
            Client client = new Client();
            client.start();
            
        }
    }

}
