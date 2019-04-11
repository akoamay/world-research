import java.nio.FloatBuffer;
import java.util.LinkedList;
import org.lwjgl.BufferUtils;

public class FloatBufferPool{
    private static LinkedList<FloatBuffer> content = new LinkedList<FloatBuffer>();
    private static FloatBufferPool fbp = new FloatBufferPool();
    private static int buffNum = 0;
    private static int max_cap = 100;
    private static int defSize = 49600;
    private static int cnt = 0;

    public static FloatBufferPool getInstance(){
        return fbp;
    }

    private FloatBufferPool(){
        if ( content == null ){
                System.out.println( "NULLLl" );
        }else{
                System.out.println( "not null" );
        }
    }

    public static FloatBuffer getBuffer(){
//        System.out.println( "getBuffer" );
        System.out.println( content.size() );
        synchronized(content){
            if ( content.size() == 0 && buffNum<max_cap ){
                buffNum++;
                System.out.println( "buffNum=" + buffNum );
                //System.out.println( "add:cnt=" + cnt);
                return BufferUtils.createFloatBuffer( defSize );
            }
            while ( content.size() == 0 ){
                try{
                cnt++;
                System.out.println( "wait:"+cnt );
                    content.wait();
                cnt--;
                }catch( InterruptedException e ){
                    e.printStackTrace();
                }
            }
            return content.removeFirst();
        }
    }

    public static void recycleBuffer( FloatBuffer fb ){
    //    System.out.println( ".recycled" );
        synchronized( content ){
    //    System.out.println( "..recycled" );
            if ( fb == null )System.out.println( "fb null!" );
            fb.clear();
            content.add(fb);
            content.notify();
        }
    }

}