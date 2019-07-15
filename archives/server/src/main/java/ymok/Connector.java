package ymok;

class Connector{

    public Connector(){

    }

    public void connect(){
        Client client = new Client();
        client.start();
    }

    public boolean isConnected(){
        return false;
    }
}