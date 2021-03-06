import static org.lwjgl.opengl.GL11.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class Tile{
    public float distance;
    public boolean hasChild = false;
    public int level;

    private TileSet childTileSet;
    public VoxelSet vs;

    private int tick = 0;
    public int tsize;
    String preffix;
    float[] m;
    float bs;

    private static int max_level;

    public Tile( int level, int tsize, float bs, String preffix){
        this.level = level;
        this.tsize = tsize;
        this.preffix = preffix;
        this.bs = bs;
        m = new float[16];
        load();
    }

    public TileSet getChileTileSet(){
        return childTileSet;
    }

    public void load(){
        vs = new VoxelSet( 0, 0, bs, tsize, tsize, tsize, level, preffix );
    }

    public void unload(){
        vs.unload();
    }

    public void addChildTileSet(){
        childTileSet = new TileSet( level + 1, 2,2, tsize, preffix );
        hasChild = true;
        unload();
    }
    public void deleteChildTileSet(){
        childTileSet.delete();
        childTileSet = null;
        hasChild = false;
        max_level = 0;
        load();
    }

    public void draw( ){

        ByteBuffer temp = ByteBuffer.allocateDirect(64);
        temp.order(ByteOrder.nativeOrder());
        glGetFloat(GL_MODELVIEW_MATRIX, (FloatBuffer)temp.asFloatBuffer());
        temp.asFloatBuffer().get(m);
        float x = m[12]; 
        float y = m[13]; 
        float z = m[14]; 
        double d = Math.sqrt( x*x + y*y+z*z);

        if ( max_level < level ) max_level = level;

        if ( d < 1500/(level*level) ){
            if ( !hasChild && level < 5 ) addChildTileSet();
        }else{
            if ( hasChild ) deleteChildTileSet();
        }

        if ( hasChild ){
            childTileSet.draw( );
        }else{
            vs.crender();
        }
    }
}