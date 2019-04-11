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
    float[] m;

    private static int max_level;

    public Tile( int level, int tsize, float bs){
        this.level = level;
        this.tsize = tsize;
        vs = new VoxelSet( 0, 0, bs, tsize, tsize, 1, level);
        m = new float[16];
    }

    public TileSet getChileTileSet(){
        return childTileSet;
    }

    public void addChildTileSet(){
        childTileSet = new TileSet( level + 1, 2, tsize );
        hasChild = true;
            System.out.println( "add " + level );
    }
    public void deleteChildTileSet(){
        childTileSet = null;
        hasChild = false;
        max_level = 0;
                System.out.println( "del " + level );
    }

    public void draw(){

        ByteBuffer temp = ByteBuffer.allocateDirect(64);
        temp.order(ByteOrder.nativeOrder());
        glGetFloat(GL_MODELVIEW_MATRIX, (FloatBuffer)temp.asFloatBuffer());
        temp.asFloatBuffer().get(m);
        float x = m[12]; 
        float y = m[13]; 
        float z = m[14]; 
        double d = Math.sqrt( x*x + y*y+z*z);

        if ( max_level < level ) max_level = level;

        if ( d < 1000/(level*level) ){
//            if ( level == 1 )
            if ( !hasChild && level < 8 ) addChildTileSet();
        }else{
            if ( hasChild ) deleteChildTileSet();
        }

        if ( hasChild ){
            childTileSet.draw();
        }else{
            //if ( max_level - level < 4 ) vs.drender((float)d);
            vs.drender((float)d);
        }
    }
}