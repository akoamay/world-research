package ymok;

import java.io.Serializable;
import java.util.HashMap;

class Message implements Serializable{

    private static final long serialVersionUID = 1L;

    public String type;
    public HashMap<String,String> map;

    public Message( String type, HashMap map ){
        this.type = type;
        this.map = map;
    }


}