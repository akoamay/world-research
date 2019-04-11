import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.validation.ValidatorHandler;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.GLUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;


class VoxelSet{
    int[][][] data;

    int vboVertexHandle;
    int vboTextureHandle;
    int vboNormalHandle;
    int vboColorHandle;

    FloatBuffer vertexBuffer;
    FloatBuffer textureBuffer;
    FloatBuffer normalBuffer;
    FloatBuffer colorBuffer;

    public float[] vtxFloatArray;
    public float[] texFloatArray;
    public float[] nrmFloatArray;
    public float[] colFloatArray;

    public int x;
    public int y;
    public int z;

    public float tx;
    public float ty;
    public float tz;

    float bs;
    int sx = 1, sy = 1, sz = 1;

    double tick = 0.0;
    int itick = 0;

    int is = 0;

    int id;
    float acx;
    float acz;

    Terrain terrain;
    public int px, py;

    public VoxelSet( int id, float bs, int x,int y, int z,
            Terrain terrain, int px, int py ){
        this.bs = bs;
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.terrain = terrain;
        this.px = px;
        this.py = py;
        //genTerrain();
        genFloatArray2();
        init();
    }

    public void update(){
        
        genFloatArray2();

        vertexBuffer = BufferUtils.createFloatBuffer(vtxFloatArray.length);
        vertexBuffer.put( vtxFloatArray ).flip();

        textureBuffer = BufferUtils.createFloatBuffer(texFloatArray.length);
        textureBuffer.put( texFloatArray ).flip();

        normalBuffer = BufferUtils.createFloatBuffer(nrmFloatArray.length);
        normalBuffer.put( nrmFloatArray ).flip();

        colorBuffer = BufferUtils.createFloatBuffer(colFloatArray.length);
        colorBuffer.put( colFloatArray ).flip();

        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, vboNormalHandle);
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

    }

    public void render(){
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L );

        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0 );

        glDrawArrays(GL_QUADS, 0, texFloatArray.length );

        glDisableClientState(GL_COLOR_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);
    }

    public void render(int texId){

        glEnableClientState(GL_VERTEX_ARRAY);
        
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);

        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L );

        glBindTexture(GL_TEXTURE_2D, texId);
        glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
        glTexCoordPointer(2, GL_FLOAT, 0, 0L);

        glBindBuffer(GL_ARRAY_BUFFER, vboNormalHandle);
        glNormalPointer(GL_FLOAT, 0, 0L );

        glDrawArrays(GL_QUADS, 0, vtxFloatArray.length/3);

        glDisableClientState(GL_NORMAL_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);
    }

    public void init(){
        vboVertexHandle = glGenBuffers();
        vboTextureHandle = glGenBuffers();
        vboNormalHandle = glGenBuffers();
        vboColorHandle = glGenBuffers();

        vertexBuffer = BufferUtils.createFloatBuffer(vtxFloatArray.length);
        vertexBuffer.put( vtxFloatArray ).flip();

        textureBuffer = BufferUtils.createFloatBuffer(texFloatArray.length);
        textureBuffer.put( texFloatArray ).flip();

        normalBuffer = BufferUtils.createFloatBuffer(nrmFloatArray.length);
        normalBuffer.put( nrmFloatArray ).flip();

        colorBuffer = BufferUtils.createFloatBuffer(colFloatArray.length);
        colorBuffer.put( colFloatArray ).flip();

        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, vboNormalHandle);
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

    }



    public void move(){
        tick += 10f;
        genTerrain();
    }

    public void genTerrain(){
        //lake(sx,sy,sz);
//        cube2(x,y,z);
        //cube3(sx,sy,sz);
        //cube(sx,sy,sz);
        sin(x,y,z);
        genFloatArray();
    }

    public void cube2(int x,int y, int z){
        data = new int[x][y][z];
        for (int i = 0; i < x; i++){
            for (int j = 0; j < y; j++){
                int h = (int)(Math.random()*2)+1;
                for (int k = 0; k < h; k++){
                    if (Math.random()<0.95){
                        data[i][j][k] = 1;
                    }else{
                        data[i][j][k] = (int)(Math.random()*30)+2;
                    }
                    /*
                    if ( k < z-3 ){
                        data[i][j][k] = 1;
                    }else{
                        data[i][j][k] = (int)(Math.random()*30)+2;
                        //data[i][j][k] = 7;
                    }
                    */
                }
            }
        }
    }
       public void cube3(int x,int y, int z){
        data = new int[x][y][z];
        for (int i = 0; i < x; i++){
            for (int j = 0; j < y; j++){
                for (int k = 0; k < z; k++){
                    if ( j < y/2 ){
                        data[i][j][k] = 1;
                    }else{
                        data[i][j][k] = 5;
                    }
                }
            }
        }
    }

    public void lake(int x, int y, int z){
        data = new int[x][y][z];
        for (int i = 0; i < x; i++){
            for (int j = 0; j < y; j++){
                int h = (int)(z*(Math.pow( i - x / 2, 2 )*Math.pow( j - y / 2, 2 ))/(Math.pow(x/2,2)*Math.pow(y/2,2)));
                for (int k = 0; k < z; k++){
                    if ( k > h ){
                        data[i][j][k] = 1;
                    }else{
                        data[i][j][k] = 6;
                    }
                } 
            }
        }
                
    }

    public void sin(int x,int y, int z){
        data = new int[x][y][z];
        for (int i = 0; i < x; i++){
            for (int j = 0; j < y; j++){
                double dist = Math.sqrt(Math.pow(i-x/2,2)+Math.pow(j-y/2,2)) * 10;
                int h = (int)(z * ((Math.sin((dist+tick)*Math.PI/180)+1.0)/2.0) );
                for (int k = 0; k < z; k++){
                    if ( k < h ){
                        data[i][j][k] = 1;
                    }else{
                        if ( k < z / 2 ){
                            data[i][j][k] = 2;
                        }
                    }
                }
            }
        }

    } 

    public void ss(){
        if (itick==0)data = new int[10][1][1];
        for (int i = 0; i < 9 - itick; i++){
            data[i][0][0]=itick+1;
        }
        itick++;
        if (itick>9)itick=1;

    }

    public void cube(int x,int y, int z){
        x = (int)tick%2 + 1;
        tick++;
        data = new int[x][y][z];
        for (int i = 0; i < x; i++){
            for (int j = 0; j < y; j++){
                for (int k = 0; k < z; k++){
                    data[i][j][k] = 2;
                }
            }
        }
    }


    public void genFloatArray2(){
       ArrayList<Float> vtxArraylist = new ArrayList<Float>(); 
       ArrayList<Float> texArraylist = new ArrayList<Float>(); 
       ArrayList<Float> nrmArraylist = new ArrayList<Float>(); 
       ArrayList<Float> colArraylist = new ArrayList<Float>();

        for (int i = 0; i < x; i++){
            for (int j = 0; j < y; j++){
                for (int k = 0; k < z; k++){
                    int tx = px + i;
                    int ty = py + y-j;

                    int ww = terrain.data[0].length;
                    int h = terrain.data[tx][ty][k];

                    int ci = x*z*j + x*k + k;
                    float cr = (float)i/x;
                    float cg = (float)j/y;
                    float cb = (float)k/z;

                    if ( h != 0 ){
                        //front
                        if ( terrain.data[tx][ty+1][k] != h ){
                            vtxArraylist.addAll( getFacetArray(FRONT,i-x/2,j-y/2,k) );
                            texArraylist.addAll( getTexArray(FRONT,h) );
                            nrmArraylist.addAll( getNrmArray(FRONT) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //back
                        if ( terrain.data[tx][ty-1][k] != h ){
                            vtxArraylist.addAll( getFacetArray(BACK,i-x/2,j-y/2,k) );
                            texArraylist.addAll( getTexArray(BACK,h) );
                            nrmArraylist.addAll( getNrmArray(BACK) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //left
                        if ( terrain.data[tx-1][ty][k] != h ){
                            vtxArraylist.addAll( getFacetArray(LEFT,i-x/2,j-y/2,k) );
                            texArraylist.addAll( getTexArray(LEFT,h) );
                            nrmArraylist.addAll( getNrmArray(LEFT) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //right
                        if ( terrain.data[tx+1][ty][k] != h ){
                            vtxArraylist.addAll( getFacetArray(RIGHT,i-x/2,j-y/2,k) );
                            texArraylist.addAll( getTexArray(RIGHT,h) );
                            nrmArraylist.addAll( getNrmArray(RIGHT) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //top
                        if ( k >= z-1 || terrain.data[tx][ty][k+1] != h ){
                            vtxArraylist.addAll( getFacetArray(TOP,i-x/2,j-y/2,k) );
                            texArraylist.addAll( getTexArray(TOP,h) );
                            nrmArraylist.addAll( getNrmArray(TOP) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //bottom
                        if ( k == 0 || terrain.data[tx][ty][k-1] != h ){
                            vtxArraylist.addAll( getFacetArray(BOTTOM,i-x/2,j-y/2,k) );
                            texArraylist.addAll( getTexArray(BOTTOM,h) );
                            nrmArraylist.addAll( getNrmArray(BOTTOM) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                    }
                }
            }
        }

        vtxFloatArray = new float[vtxArraylist.size()];
        for (int i = 0; i < vtxArraylist.size(); i++ ){
            vtxFloatArray[i] = vtxArraylist.get(i).floatValue();
        }
        texFloatArray = new float[texArraylist.size()];
        for (int i = 0; i < texArraylist.size(); i++ ){
            texFloatArray[i] = texArraylist.get(i).floatValue();
        }
        nrmFloatArray = new float[nrmArraylist.size()];
        for (int i = 0; i < nrmArraylist.size(); i++ ){
            nrmFloatArray[i] = nrmArraylist.get(i).floatValue();
        }
        colFloatArray = new float[colArraylist.size()];
        for (int i = 0; i < colArraylist.size(); i++ ){
            colFloatArray[i] = colArraylist.get(i).floatValue();
        }
    }

    public void genFloatArray(){
       ArrayList<Float> vtxArraylist = new ArrayList<Float>(); 
       ArrayList<Float> texArraylist = new ArrayList<Float>(); 
       ArrayList<Float> nrmArraylist = new ArrayList<Float>(); 
       ArrayList<Float> colArraylist = new ArrayList<Float>();

       int x = data.length;
       int y = data[0].length;
       int z = data[0][0].length;
        for (int i = 0; i < x; i++){
            for (int j = 0; j < y; j++){
                for (int k = 0; k < z; k++){
                    int h = data[i][j][k];
                    int ci = x*z*j + x*k + k;
                    float cr = (float)i/x;
                    float cg = (float)j/y;
                    float cb = (float)k/z;
                    if ( h != 0 ){
                        //front
                        if ( j > 0 && data[i][j-1][k] != h ){
                            //vtxArraylist.addAll( getFacetArray(FRONT,i,j,k) );
                            vtxArraylist.addAll( getFacetArray(FRONT,i-x/2,j-y/2,k) );
                            texArraylist.addAll( getTexArray(FRONT,h) );
                            nrmArraylist.addAll( getNrmArray(FRONT) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //if ( vtxArraylist.size() == 50000 )break;
                        //back
                        if ( j < y-1 && data[i][j+1][k] != h ){
                            //vtxArraylist.addAll( getFacetArray(BACK,i,j,k) );
                            vtxArraylist.addAll( getFacetArray(BACK,i-x/2,j-y/2,k) );
                            texArraylist.addAll( getTexArray(BACK,h) );
                            nrmArraylist.addAll( getNrmArray(BACK) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //if ( vtxArraylist.size() > 50000 )break;
                        //left
                        if ( i > 0 && data[i-1][j][k] != h ){
                            //vtxArraylist.addAll( getFacetArray(LEFT,i,j,k) );
                            vtxArraylist.addAll( getFacetArray(LEFT,i-x/2,j-y/2,k) );
                            texArraylist.addAll( getTexArray(LEFT,h) );
                            nrmArraylist.addAll( getNrmArray(LEFT) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //if ( vtxArraylist.size() > 50000 )break;
                        //right
                        if ( i < x-1 && data[i+1][j][k] != h ){
                            //vtxArraylist.addAll( getFacetArray(RIGHT,i,j,k) );
                            vtxArraylist.addAll( getFacetArray(RIGHT,i-x/2,j-y/2,k) );
                            texArraylist.addAll( getTexArray(RIGHT,h) );
                            nrmArraylist.addAll( getNrmArray(RIGHT) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //if ( vtxArraylist.size() > 50000 )break;
                        //top
                        if ( k >= z-1 || data[i][j][k+1] != h ){
                            //vtxArraylist.addAll( getFacetArray(TOP,i,j,k) );
                            vtxArraylist.addAll( getFacetArray(TOP,i-x/2,j-y/2,k) );
                            texArraylist.addAll( getTexArray(TOP,h) );
                            nrmArraylist.addAll( getNrmArray(TOP) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //if ( vtxArraylist.size() > 50000 )break;
                        //bottom
                        if ( k == 0 || data[i][j][k-1] != h ){
                            //vtxArraylist.addAll( getFacetArray(BOTTOM,i,j,k) );
                            vtxArraylist.addAll( getFacetArray(BOTTOM,i-x/2,j-y/2,k) );
                            texArraylist.addAll( getTexArray(BOTTOM,h) );
                            nrmArraylist.addAll( getNrmArray(BOTTOM) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //if ( vtxArraylist.size() > 50000 )break;
                    }
                }
            }
        }
        if ( is == 0 ) is = vtxArraylist.size();
        vtxFloatArray = new float[vtxArraylist.size()];
        for (int i = 0; i < vtxArraylist.size(); i++ ){
            vtxFloatArray[i] = vtxArraylist.get(i).floatValue();
        }
        texFloatArray = new float[texArraylist.size()];
        for (int i = 0; i < texArraylist.size(); i++ ){
            texFloatArray[i] = texArraylist.get(i).floatValue();
        }
        nrmFloatArray = new float[nrmArraylist.size()];
        for (int i = 0; i < nrmArraylist.size(); i++ ){
            nrmFloatArray[i] = nrmArraylist.get(i).floatValue();
        }
        colFloatArray = new float[colArraylist.size()];
        for (int i = 0; i < colArraylist.size(); i++ ){
            colFloatArray[i] = colArraylist.get(i).floatValue();
        }
    }

    private final int FRONT = 0;
    private final int BACK = 1;
    private final int LEFT = 2;
    private final int RIGHT = 3;
    private final int TOP = 4;
    private final int BOTTOM = 5;
    private ArrayList<Float> getFacetArray(int direction, int _x, int _y, int _z ){
        float u = bs/2.0f;
        /*
        float x = (_x - sx/2) * bs;
        float y = (_z - sz/2) * bs;
        float z = -(_y - sy/2) * bs;
        */
        float x = _x * bs;
        float y = _z * bs;
        float z = -_y * bs;

        int[] direction_trans = { };
        switch (direction){
            case FRONT:
                return new ArrayList( Arrays.asList( x-u, y-u, z+u, x+u, y-u, z+u, x+u, y+u, z+u, x-u, y+u, z+u ) );
                //return new ArrayList( Arrays.asList( new float[]{ x-u, y+u, z-u, x+u, y+u, z-u, x+u, y-u, z-u, x-u, y-u, z-u } ) );
            case BACK:
                return new ArrayList( Arrays.asList( x-u, y+u, z-u, x+u, y+u, z-u, x+u, y-u, z-u, x-u, y-u, z-u ) );
                //return new ArrayList( Arrays.asList( new float[]{ x-u, y-u, z+u, x+u, y-u, z+u, x+u, y+u, z+u, x-u, y+u, z+u } ) );
            case LEFT:
                return new ArrayList( Arrays.asList( x-u, y-u, z+u, x-u, y+u, z+u, x-u, y+u, z-u, x-u, y-u, z-u ) );
                //return new ArrayList( Arrays.asList( new float[]{ x-u, y-u, z+u, x-u, y+u, z+u, x-u, y+u, z-u, x-u, y-u, z-u } ) );
            case RIGHT:
                return new ArrayList( Arrays.asList( x+u, y-u, z+u, x+u, y-u, z-u, x+u, y+u, z-u, x+u, y+u, z+u ) );
                //return new ArrayList( Arrays.asList( new float[]{ x+u, y-u, z+u, x+u, y-u, z-u, x+u, y+u, z-u, x+u, y+u, z+u } ) );
            case TOP:
                return new ArrayList( Arrays.asList( x-u, y+u, z-u, x-u, y+u, z+u, x+u, y+u, z+u, x+u, y+u, z-u ) );
                //return new ArrayList( Arrays.asList( new float[]{ x-u, y+u, z-u, x-u, y+u, z+u, x+u, y+u, z+u, x+u, y+u, z-u } ) );
            case BOTTOM:
            default:
                return new ArrayList( Arrays.asList( x-u, y-u, z+u, x-u, y-u, z-u, x+u, y-u, z-u, x+u, y-u, z+u ) );
                //return new ArrayList( Arrays.asList( new float[]{ x-u, y-u, z+u, x-u, y-u, z-u, x+u, y-u, z-u, x+u, y-u, z+u } ) );
        }
    }

    private ArrayList<Float> getTexArray(int direction, int idx){
        float w = 0.03f;
        float x = w*(idx % 10)+0.002f;
        float y = w*(idx / 10)+0.002f;
        switch (direction){
            default:
                return new ArrayList( Arrays.asList( x, y, x+w, y, x+w, y+w, x, y+w ) );
        }
    }
    
    private ArrayList<Float> getColArray(float cr, float cg, float cb){
        return new ArrayList( Arrays.asList( cr,cg,cb,cr,cg,cb,cr,cg,cb,cr,cg,cb ));
    }

    private ArrayList<Float> getNrmArray(int direction){
        switch (direction){
            case FRONT:
                return new ArrayList( Arrays.asList( 0.0f, 0.0f,-1.0f, 0.0f, 0.0f,-1.0f, 0.0f, 0.0f,-1.0f,0.0f, 0.0f,-1.0f ));
            case BACK:
                return new ArrayList( Arrays.asList( 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,0.0f, 0.0f, 1.0f ));
            case LEFT:
                return new ArrayList( Arrays.asList(-1.0f, 0.0f, 0.0f,-1.0f, 0.0f, 0.0f,-1.0f, 0.0f, 0.0f,-1.0f, 0.0f, 0.0f));
            case RIGHT:
                return new ArrayList( Arrays.asList(1.0f, 0.0f, 0.0f ,1.0f, 0.0f, 0.0f ,1.0f, 0.0f, 0.0f,1.0f, 0.0f, 0.0f));
            case TOP:
                return new ArrayList( Arrays.asList(0.0f, 1.0f, 0.0f ,0.0f, 1.0f, 0.0f ,0.0f, 1.0f, 0.0f,0.0f, 1.0f, 0.0f));
            case BOTTOM:
            default:
                return new ArrayList( Arrays.asList(0.0f,-1.0f, 0.0f,0.0f,-1.0f, 0.0f ,0.0f,-1.0f, 0.0f,0.0f,-1.0f, 0.0f));
        }
    }
}