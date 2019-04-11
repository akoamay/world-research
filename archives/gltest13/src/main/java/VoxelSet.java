import java.io.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.validation.ValidatorHandler;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.GLUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipOutputStream;

import java.nio.file.Files;
import java.nio.file.Files.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;


class VoxelSet implements ILoadable{

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
    int wl = 0;
    float acx;
    float acz;

    Terrain terrain;
    public int px, py;

    float depth;

    int level;
    String fname;

    int olevel = -1;
    boolean onloaded = false;
    boolean oninit = false;

    boolean isEmpty = false;
    Future future = null;
    boolean mustdie = false;

    @Override
    public void onLoad( FloatArrays arrays ){
        if ( mustdie ) return;
        //System.out.println( "onLoad" );
        vtxFloatArray = arrays.vtxFloatArray;
        nrmFloatArray = arrays.nrmFloatArray;
        colFloatArray = arrays.colFloatArray;
        texFloatArray = arrays.texFloatArray;

        if ( vtxFloatArray.length == 0 ){
            System.out.println( "emp" );
            isEmpty = true;
            return;
        }

        //System.out.println( "len="+vtxFloatArray.length );
        vertexBuffer = FloatBufferPool.getBuffer();
        if ( vertexBuffer == null ) System.out.println( "vb null" );
        vertexBuffer.put( vtxFloatArray ).flip();

        textureBuffer = FloatBufferPool.getBuffer();
        if ( textureBuffer == null ) System.out.println( "vb null" );
        textureBuffer.put( texFloatArray ).flip();

        normalBuffer = FloatBufferPool.getBuffer();
        if ( normalBuffer == null ) System.out.println( "vb null" );
        normalBuffer.put( nrmFloatArray ).flip();

        colorBuffer = FloatBufferPool.getBuffer();
        if ( colorBuffer == null ) System.out.println( "vb null" );
        colorBuffer.put( colFloatArray ).flip();

        this.onloaded = true;
    }

    /*
    public void finish(){
        if (vertexBuffer!=null)
        FloatBufferPool.recycleBuffer( vertexBuffer );

        if (textureBuffer!=null)
        FloatBufferPool.recycleBuffer( textureBuffer );

        if (normalBuffer!=null)
        FloatBufferPool.recycleBuffer( normalBuffer );

        if (colorBuffer!=null)
        FloatBufferPool.recycleBuffer( colorBuffer );
    }
    */

    public VoxelSet( int wl, int id, float bs, int x,int y, int z, int level,
            String fname){
        this.wl = wl;
        this.bs = bs;
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.px = px;
        this.py = py;
        this.level = level;
        this.fname = String.valueOf(level-1) + "_" + fname;
        //genTerrain();
        //loadData();


        LoaderQueue.getInstance().load( this.fname, level, x, this );

    }

    public void unload(){
        if (vertexBuffer!=null) FloatBufferPool.recycleBuffer( vertexBuffer );
        vertexBuffer = null;
        if (textureBuffer!=null) FloatBufferPool.recycleBuffer( textureBuffer );
        textureBuffer = null;
        if (normalBuffer!=null) FloatBufferPool.recycleBuffer( normalBuffer );
        normalBuffer = null;
        if (colorBuffer!=null) FloatBufferPool.recycleBuffer( colorBuffer );
        colorBuffer = null;


        if ( onloaded && oninit ){
            glDeleteBuffers(vboVertexHandle);
            glDeleteBuffers(vboColorHandle);
            glDeleteBuffers(vboTextureHandle);
            glDeleteBuffers(vboNormalHandle);
        }

        mustdie = true;
    }

    public void crender( int texId){
        if ( !onloaded || isEmpty ){
            //System.out.println("!onloaded");
            return;
        }
        if ( !oninit ) init();
        //System.out.println("crender");

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
       // glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L );

        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0 );

        //glBindTexture(GL_TEXTURE_2D, texId);
        //glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
        //glTexCoordPointer(2, GL_FLOAT, 0, 0L);

        glDrawArrays(GL_QUADS, 0, vtxFloatArray.length/3 );

        glDisableClientState(GL_COLOR_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);
        //glDisableClientState(GL_TEXTURE_COORD_ARRAY);
    }
    public void drender( float depth ){
        if ( !onloaded ) return;
        if ( !oninit ) init();

//        if ( Math.abs(this.depth - depth ) > 500 ){
        if ( olevel != level ){
            olevel = level;
            this.depth = depth;
          //  update();
//            System.out.println( this.depth );
        }

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L );

        glBindBuffer(GL_ARRAY_BUFFER, vboNormalHandle);
        glNormalPointer(GL_FLOAT, 0, 0L );

        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0 );

        glDrawArrays(GL_QUADS, 0, vtxFloatArray.length/3 );

    }

    public void render(){
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L );


        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0 );

        glDrawArrays(GL_QUADS, 0, vtxFloatArray.length/3 );

        glDisableClientState(GL_COLOR_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);
    }

    public void render(int texId){
        if ( !onloaded ) return;
        if ( !oninit ) init();
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
        /*

        vertexBuffer = BufferUtils.createFloatBuffer(vtxFloatArray.length);
        vertexBuffer.put( vtxFloatArray ).flip();

        textureBuffer = BufferUtils.createFloatBuffer(texFloatArray.length);
        textureBuffer.put( texFloatArray ).flip();

        normalBuffer = BufferUtils.createFloatBuffer(nrmFloatArray.length);
        normalBuffer.put( nrmFloatArray ).flip();

        colorBuffer = BufferUtils.createFloatBuffer(colFloatArray.length);
        colorBuffer.put( colFloatArray ).flip();
        */

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

        if (vertexBuffer!=null) FloatBufferPool.recycleBuffer( vertexBuffer );
        vertexBuffer = null;
        if (textureBuffer!=null) FloatBufferPool.recycleBuffer( textureBuffer );
        textureBuffer = null;
        if (normalBuffer!=null) FloatBufferPool.recycleBuffer( normalBuffer );
        normalBuffer = null;
        if (colorBuffer!=null) FloatBufferPool.recycleBuffer( colorBuffer );
        colorBuffer = null;

        oninit = true;
    }



    public void move(){
        tick += 10f;
//        genTerrain();
    }

    public void genTerrain(){
        //lake(sx,sy,sz);
        //cube2(x,y,z);
        //cube3(sx,sy,sz);
        //grid(x,y);
        //cube(x,y,z);
 //       genFloatArray();
    }

    /*
    public void grid( int x, int y ){
        data = new byte[x][y][1];
        for (int i = 0; i < x; i++){
            for (int j = 0; j < y; j++){
                data[i][j][0] = 1;
            }
        }
    }
    */

}