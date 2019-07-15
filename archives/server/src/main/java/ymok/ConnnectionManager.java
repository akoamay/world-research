package ymok;

class ConnectionManager implements Runnable{


    final static int CNUM = 4;
    Connector[] connectors = new Connector[CNUM];

    public ConnectionManager(){
        for ( int i = 0; i < CNUM; i++ ){
                connectors[i] = new Connector();
        }
    }


    @Override
    public void run(){
        try{
            for ( int i = 0; i < CNUM; i++ ){
                if ( !connectors[i].isConnected() ){
                    connectors[i].connect();
                }
            }

            Thread.sleep(1000);
        }catch( Exception e){
            e.printStackTrace();
        }

    }
    

}