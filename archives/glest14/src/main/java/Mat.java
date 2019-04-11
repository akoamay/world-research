import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Mat{

    public float[] m;

    public float get(int i, int j){
        return m[i*4+j];
    }

    public Mat(){
        m = new float[16];
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                if (i==j) m[i*4+j]=1.0f;
            }
        }
    }

    public Mat multiply( Mat m ){
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                for (int k = 0; k < 4; k++){
                    //this.get(k,i)*m.get()
                }
            }
        }
        return new Mat();
    }

    public FloatBuffer asBuffer(){
        FloatBuffer buffer = ByteBuffer.allocateDirect(4 * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer() ;
        buffer.put(m);
        buffer.flip();
        return buffer;
    }

}
