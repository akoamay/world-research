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


class VoxelSet{

    int vboVertexHandle;

    FloatBuffer vertexBuffer;

    public float[] vtxFloatArray;

    public int x;
    public int y;
    public int z;

    public float tx;
    public float ty;
    public float tz;

    float bs=0.1f;
    int sx = 1, sy = 1, sz = 1;

    double tick = 0.0;
    int itick = 0;

    int is = 0;

    int id;
    int wl = 0;
    float acx;
    float acz;

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

    int[][][] data;

//        vertexBuffer = FloatBufferPool.getBuffer();
 //       vertexBuffer.put( vtxFloatArray ).flip();
    private final int FRONT = 0;
    private final int BACK = 1;
    private final int LEFT = 2;
    private final int RIGHT = 3;
    private final int TOP = 4;
    private final int BOTTOM = 5;
    public VoxelSet( int x, int y, int z ){
        this.x = x;
        this.y = y;
        this.z = z;
        this.bs = (float)(Math.sqrt(2)/x);
        init();
    }

    //public void render( SimpleShader ss ){
    public void render(  ){
        glEnableClientState(GL_VERTEX_ARRAY);

        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L );

        glDrawArrays(GL_QUADS, 0, vtxFloatArray.length/3 );

        glDisableClientState(GL_VERTEX_ARRAY);
    }

    public void init(){
        genTerrain();
        vboVertexHandle = glGenBuffers();

        vertexBuffer = BufferUtils.createFloatBuffer(vtxFloatArray.length);
        vertexBuffer.put( vtxFloatArray ).flip();

        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }



    public void genTerrain(){
        grid(x,y);
        genFloatArray();
    }

    public void grid( int x, int y ){
        z = 10;
        data = new int[x][y][z];
        for (int i = 0; i < x; i++){
            for (int j = 0; j < y; j++){
                int h = (int)(((float)i/x)*z);
                for (int k = 0; k < h; k++){
                    data[i][j][k] = 1;
                }
            }
        }
    }

    public void genFloatArray(){
       ArrayList<Float> vtxArraylist = new ArrayList<Float>(); 

       int cc = 0;
       int x = data.length;
       int y = data[0].length;
       int z = data[0][0].length;
        for (int i = 0; i < y; i++){
            for (int j = 0; j < x; j++){
                for (int k = 0; k < z; k++){
                    int h = data[j][i][k];
                    int ci = x*z*j + x*k + k;

                    float  o = (x - 1) / 2.0f;


                    if ( h != 0 ){
                        //front
                        if ( i > 0 && data[j][i-1][k] != h ){
                            vtxArraylist.addAll( getFacetArray(FRONT,j-o,i-o,k) );
                        }
                        //back
                        if ( i < x-1 && data[j][i+1][k] != h ){
                            vtxArraylist.addAll( getFacetArray(BACK,j-o,i-o,k) );
                        }
                        //left
                        if ( j > 0 && data[j-1][i][k] != h ){
                            vtxArraylist.addAll( getFacetArray(LEFT,j-o,i-o,k) );
                        }
                        //right
                        if ( j < y-1 && data[j+1][i][k] != h ){
                            vtxArraylist.addAll( getFacetArray(RIGHT,j-o,i-o,k) );
                        }
                        //top
                        if ( k >= z-1 || data[j][i][k+1] != h ){
                            vtxArraylist.addAll( getFacetArray(TOP,j-o,i-o,k) );
                        }
                        //bottom
                        if ( k == 0 || data[j][i][k-1] != h ){
                            vtxArraylist.addAll( getFacetArray(BOTTOM,j-o,i-o,k) );
                        }
                    }
                }
            }
        }

//        if ( vtxArraylist.size() != 0)
       //System.out.println( "val.size=" + vtxArraylist.size() + " cc=" + cc );
        vtxFloatArray = new float[vtxArraylist.size()];
        for (int i = 0; i < vtxArraylist.size(); i++ ){
            vtxFloatArray[i] = vtxArraylist.get(i).floatValue();
        }

    }

    private float funcu( float x, float y, float z, float u ){
        float m = (float)(Math.sqrt(2)/2);
        float l = (y - m + 1);
        return (float)( l*m / Math.sqrt(x*x+z*z +m*m));
    }
    private float func( float x, float y, float z, float u ){
        float m = (float)(Math.sqrt(2)/2);
        float l = (y - m + 1);
        return (float)( l*u / Math.sqrt(x*x+z*z +m*m));
    }
    private ArrayList<Float> getFacetArray(int direction, float _x, float _y, float _z ){
        float u = bs/2.0f;

        /*
        float x = (_x - sx/2) * bs;
        float y = (_z - sz/2) * bs;
        float z = -(_y - sy/2) * bs;
        */
        float m = (float)(Math.sqrt(2)/2);
        float x = _x * bs;
        float y = _z * bs + m + u;
        float z = -_y * bs;

        float px1=0,px2=0,px3=0,px4=0;
        float py1=0,py2=0,py3=0,py4=0;
        float pz1=0,pz2=0,pz3=0,pz4=0;

        int[] direction_trans = {};
        switch (direction){
            case FRONT:
                px1 = func(x-u,y-u,z+u,x-u);
                py1 = funcu(x-u,y-u,z+u,y-u);
                pz1 = func(x-u,y-u,z+u,z+u);

                px2 = func(x+u,y-u,z+u,x+u);
                py2 = funcu(x+u,y-u,z+u,y-u);
                pz2 = func(x+u,y-u,z+u,z+u);

                px3 = func(x+u,y+u,z+u,x+u);
                py3 = funcu(x+u,y+u,z+u,y+u);
                pz3 = func(x+u,y+u,z+u,z+u);

                px4 = func(x-u,y+u,z+u,x+u);
                py4 = funcu(x-u,y+u,z+u,y+u);
                pz4 = func(x-u,y+u,z+u,z+u);
                break;
            case BACK:
                px1 = func(x-u,y+u,z-u,x-u);
                py1 = funcu(x-u,y+u,z-u,y+u);
                pz1 = func(x-u,y+u,z-u,z-u);

                px2 = func(x+u,y+u,z+u,x+u);
                py2 = funcu(x+u,y+u,z+u,y+u);
                pz2 = func(x+u,y+u,z+u,z+u);

                px3 = func(x+u,y-u,z-u,x+u);
                py3 = funcu(x+u,y-u,z-u,y-u);
                pz3 = func(x+u,y-u,z-u,z-u);

                px4 = func(x-u,y-u,z-u,x-u);
                py4 = funcu(x-u,y-u,z-u,y-u);
                pz4 = func(x-u,y-u,z-u,z-u);
                break;
//                return new ArrayList( Arrays.asList( x-u, y+u, z-u, x+u, y+u, z-u, x+u, y-u, z-u, x-u, y-u, z-u ) );
            case LEFT:
                px1 = func(x-u,y-u,z+u,x-u);
                py1 = funcu(x-u,y-u,z+u,y-u);
                pz1 = func(x-u,y-u,z+u,z+u);

                px2 = func(x-u,y+u,z+u,x-u);
                py2 = funcu(x-u,y+u,z+u,y+u);
                pz2 = func(x-u,y+u,z+u,z+u);

                px3 = func(x-u,y+u,z-u,x-u);
                py3 = funcu(x-u,y+u,z-u,y+u);
                pz3 = func(x-u,y+u,z-u,z-u);

                px4 = func(x-u,y-u,z-u,x-u);
                py4 = funcu(x-u,y-u,z-u,y-u);
                pz4 = func(x-u,y-u,z-u,z-u);
                break;
//                return new ArrayList( Arrays.asList( x-u, y-u, z+u, x-u, y+u, z+u, x-u, y+u, z-u, x-u, y-u, z-u ) );
            case RIGHT:
                px1 = func(x+u,y-u,z+u,x+u);
                py1 = funcu(x+u,y-u,z+u,y-u);
                pz1 = func(x+u,y-u,z+u,z+u);

                px2 = func(x+u,y-u,z-u,x+u);
                py2 = funcu(x+u,y-u,z-u,y-u);
                pz2 = func(x+u,y-u,z-u,z-u);

                px3 = func(x+u,y+u,z-u,x+u);
                py3 = funcu(x+u,y+u,z-u,y+u);
                pz3 = func(x+u,y+u,z-u,z-u);

                px4 = func(x+u,y+u,z-u,x+u);
                py4 = funcu(x+u,y+u,z-u,y+u);
                pz4 = func(x+u,y+u,z-u,z-u);
                break;
//                return new ArrayList( Arrays.asList( x+u, y-u, z+u, x+u, y-u, z-u, x+u, y+u, z-u, x+u, y+u, z+u ) );
            case TOP:
                px1 = func(x-u,y+u,z-u,x-u);
                py1 = funcu(x-u,y+u,z-u,y+u);
                pz1 = func(x-u,y+u,z-u,z-u);

                px2 = func(x-u,y+u,z+u,x-u);
                py2 = funcu(x-u,y+u,z+u,y+u);
                pz2 = func(x-u,y+u,z+u,z+u);

                px3 = func(x+u,y+u,z+u,x+u);
                py3 = funcu(x+u,y+u,z+u,y+u);
                pz3 = func(x+u,y+u,z+u,z+u);

                px4 = func(x+u,y+u,z-u,x+u);
                py4 = funcu(x+u,y+u,z-u,y+u);
                pz4 = func(x+u,y+u,z-u,z-u);
                break;
                //return new ArrayList( Arrays.asList( x-u, y+u, z-u, x-u, y+u, z+u, x+u, y+u, z+u, x+u, y+u, z-u ) );
            case BOTTOM:
                px1 = func(x-u,y-u,z+u,x-u);
                py1 = funcu(x-u,y-u,z+u,y-u);
                pz1 = func(x-u,y-u,z+u,z+u);

                px2 = func(x-u,y-u,z-u,x-u);
                py2 = funcu(x-u,y-u,z-u,y-u);
                pz2 = func(x-u,y-u,z-u,z-u);

                px3 = func(x+u,y-u,z-u,x+u);
                py3 = funcu(x+u,y-u,z-u,y-u);
                pz3 = func(x+u,y-u,z-u,z-u);

                px4 = func(x+u,y-u,z+u,x+u);
                py4 = funcu(x+u,y-u,z+u,y-u);
                pz4 = func(x+u,y-u,z+u,z+u);
                break;
//                return new ArrayList( Arrays.asList( x-u, y-u, z+u, x-u, y-u, z-u, x+u, y-u, z-u, x+u, y-u, z+u ) );
        }
        return new ArrayList( Arrays.asList(px1,py1,pz1,px2,py2,pz2,px3,py3,pz3,px4,py4,pz4) );
    }
}