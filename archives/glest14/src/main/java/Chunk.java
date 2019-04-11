import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.GLUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.io.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;


public class Chunk{
    public int cx;
    public int cy;
    public int cz;
    int[][][] data;

    int vboVertexHandle;
    FloatBuffer vertexBuffer;
    public float[] vtxFloatArray;

    private final int FRONT = 0;
    private final int BACK = 4;
    private final int LEFT = 2;
    private final int RIGHT = 3;
    private final int TOP = 6;
    private final int BOTTOM = 5;
    ArrayList<Float> vtxArraylist = new ArrayList<Float>(); 

    public Chunk( int x, int y, int z ){
        this.cx = x;
        this.cy = y;
        this.cz = z;
        data = new int[x][z][y];
        init();
    }

    public void render(){
       glEnableClientState(GL_VERTEX_ARRAY);

        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L );

        glDrawArrays(GL_QUADS, 0, vtxFloatArray.length/3 );

        glDisableClientState(GL_VERTEX_ARRAY);
    }

    public void init(){
        for ( int i = 0; i < cx; i++ ){
            for ( int j = 0; j < cz; j++ ){
                int h = 1;
                if (Math.random()>.95){
                    h = (int)(Math.random()*(cy-1)+1);
                }
                for ( int k = 0; k < h; k++ ){
                    data[i][j][k] = 1;
                }
            }
        }

        genFloatArray();

        vtxFloatArray = new float[vtxArraylist.size()];
        for (int i = 0; i < vtxArraylist.size(); i++ ){
            vtxFloatArray[i] = vtxArraylist.get(i).floatValue();
        }

        vboVertexHandle = glGenBuffers();

        vertexBuffer = BufferUtils.createFloatBuffer(vtxFloatArray.length);
        vertexBuffer.put( vtxFloatArray ).flip();

        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void genFloatArray(){

        for (int i = 0; i < cx; i++){
            for (int j = 0; j < cz; j++){
                for (int k = 0; k < cy; k++){
                    int h = data[i][j][k];

                    float  o = (cx - 1) / 2.0f;

                    if ( h != 0 ){
                        //front
                        if ( j > 0 && data[i][j-1][k] != h ){
                            vtxArraylist.addAll( getFacetArray(FRONT,i-o,j-o,k) );
                        }
                        //back
                        if ( j < cz-1 && data[i][j+1][k] != h ){
                            vtxArraylist.addAll( getFacetArray(BACK,i-o,j-o,k) );
                        }
                        //left
                        if ( i > 0 && data[i-1][j][k] != h ){
                            vtxArraylist.addAll( getFacetArray(LEFT,i-o,j-o,k) );
                        }
                        //right
                        if ( i < cx-1 && data[i+1][j][k] != h ){
                            vtxArraylist.addAll( getFacetArray(RIGHT,i-o,j-o,k) );
                        }
                        //top
                        if ( k >= cy-1 || data[i][j][k+1] != h ){
                            vtxArraylist.addAll( getFacetArray(TOP,i-o,j-o,k) );
                        }


                        //bottom
                        if ( k == 0 || data[i][j][k-1] != h ){
                            vtxArraylist.addAll( getFacetArray(BOTTOM,i-o,j-o,k) );
                        }
                    }
                }
            }
        }


    }


    private float funx( float x, float y, float z ){
        float m = (float)(Math.sqrt(2)/2);
        float mx=x, my=m, mz=z;
        float bs = (float)(Math.sqrt(2)/cx);
        float u = bs/2.0f;
        float r = 1+(y-m)+u;
        return (float)( r*x / Math.sqrt(mx*mx+mz*mz+my*my));
    }
    private float funy( float x, float y, float z ){
        float m = (float)(Math.sqrt(2)/2);
        float mx=x, my=m, mz=z;
        float bs = (float)(Math.sqrt(2)/cx);
        float u = bs/2.0f;
        float r = 1+(y-m)+u;
        return (float)( r*m / Math.sqrt(mx*mx+mz*mz+my*my));
    }
    private float funz( float x, float y, float z ){
        float m = (float)(Math.sqrt(2)/2);
        float mx=x, my=m, mz=z;
        float bs = (float)(Math.sqrt(2)/cx);
        float u = bs/2.0f;
        float r = 1+(y-m)+u;
        return (float)( r*z / Math.sqrt(mx*mx+mz*mz+my*my));
    } 
    /*
    private float fun( float x, float y, float z, float u ){
        float m = (float)(Math.sqrt(2)/2);
        float mx=x, my=m, mz=z;
        return (float)( u*r / Math.sqrt(mx*mx+mz*mz+my*my));
    }
    */

    private ArrayList<Float> getFacetArray(int direction, float _x, float _z, float _y ){
        float bs = (float)(Math.sqrt(2)/cx);
        float u = bs/2.0f;
        float m = (float)(Math.sqrt(2)/2);

        float x = _x * bs;
        float z = _z * bs;
        float y = _y * bs;

        y += m+u;

        float px1=0,px2=0,px3=0,px4=0;
        float py1=0,py2=0,py3=0,py4=0;
        float pz1=0,pz2=0,pz3=0,pz4=0;

        int[] direction_trans = {};
        switch (direction){
            case FRONT:
                px1 = funx(x-u,y-u,z+u);
                py1 = funy(x-u,y-u,z+u);
                pz1 = funz(x-u,y-u,z+u);

                px2 = funx(x+u,y-u,z+u);
                py2 = funy(x+u,y-u,z+u);
                pz2 = funz(x+u,y-u,z+u);

                px3 = funx(x+u,y+u,z+u);
                py3 = funy(x+u,y+u,z+u);
                pz3 = funz(x+u,y+u,z+u);

                px4 = funx(x-u,y+u,z+u);
                py4 = funy(x-u,y+u,z+u);
                pz4 = funz(x-u,y+u,z+u);
                break;
            case BACK:
                px1 = funx(x-u,y+u,z-u);
                py1 = funy(x-u,y+u,z-u);
                pz1 = funz(x-u,y+u,z-u);

                px2 = funx(x+u,y+u,z+u);
                py2 = funy(x+u,y+u,z+u);
                pz2 = funz(x+u,y+u,z+u);

                px3 = funx(x+u,y-u,z-u);
                py3 = funy(x+u,y-u,z-u);
                pz3 = funz(x+u,y-u,z-u);

                px4 = funx(x-u,y-u,z-u);
                py4 = funy(x-u,y-u,z-u);
                pz4 = funz(x-u,y-u,z-u);
                break;
//                return new ArrayList( Arrays.asList( x-u, y+u, z-u, x+u, y+u, z-u, x+u, y-u, z-u, x-u, y-u, z-u ) );
            case LEFT:
                px1 = funx(x-u,y-u,z+u);
                py1 = funy(x-u,y-u,z+u);
                pz1 = funz(x-u,y-u,z+u);

                px2 = funx(x-u,y+u,z+u);
                py2 = funy(x-u,y+u,z+u);
                pz2 = funz(x-u,y+u,z+u);

                px3 = funx(x-u,y+u,z-u);
                py3 = funy(x-u,y+u,z-u);
                pz3 = funz(x-u,y+u,z-u);

                px4 = funx(x-u,y-u,z-u);
                py4 = funy(x-u,y-u,z-u);
                pz4 = funz(x-u,y-u,z-u);
                break;
//                return new ArrayList( Arrays.asList( x-u, y-u, z+u, x-u, y+u, z+u, x-u, y+u, z-u, x-u, y-u, z-u ) );
            case RIGHT:
                px1 = funx(x+u,y-u,z+u);
                py1 = funy(x+u,y-u,z+u);
                pz1 = funz(x+u,y-u,z+u);

                px2 = funx(x+u,y-u,z-u);
                py2 = funy(x+u,y-u,z-u);
                pz2 = funz(x+u,y-u,z-u);

                px3 = funx(x+u,y+u,z-u);
                py3 = funy(x+u,y+u,z-u);
                pz3 = funz(x+u,y+u,z-u);

                px4 = funx(x+u,y+u,z-u);
                py4 = funy(x+u,y+u,z-u);
                pz4 = funz(x+u,y+u,z-u);
                break;
//                return new ArrayList( Arrays.asList( x+u, y-u, z+u, x+u, y-u, z-u, x+u, y+u, z-u, x+u, y+u, z+u ) );
            case TOP:
                px1 = funx(x-u,y+u,z-u);
                py1 = funy(x-u,y+u,z-u);
                pz1 = funz(x-u,y+u,z-u);

                px2 = funx(x-u,y+u,z+u);
                py2 = funy(x-u,y+u,z+u);
                pz2 = funz(x-u,y+u,z+u);

                px3 = funx(x+u,y+u,z+u);
                py3 = funy(x+u,y+u,z+u);
                pz3 = funz(x+u,y+u,z+u);

                px4 = funx(x+u,y+u,z-u);
                py4 = funy(x+u,y+u,z-u);
                pz4 = funz(x+u,y+u,z-u);
                break;
                //return new ArrayList( Arrays.asList( x-u, y+u, z-u, x-u, y+u, z+u, x+u, y+u, z+u, x+u, y+u, z-u ) );
            case BOTTOM:
                px1 = funx(x-u,y-u,z+u);
                py1 = funy(x-u,y-u,z+u);
                pz1 = funz(x-u,y-u,z+u);

                px2 = funx(x-u,y-u,z-u);
                py2 = funy(x-u,y-u,z-u);
                pz2 = funz(x-u,y-u,z-u);

                px3 = funx(x+u,y-u,z-u);
                py3 = funy(x+u,y-u,z-u);
                pz3 = funz(x+u,y-u,z-u);

                px4 = funx(x+u,y-u,z+u);
                py4 = funy(x+u,y-u,z+u);
                pz4 = funz(x+u,y-u,z+u);
                break;
//                return new ArrayList( Arrays.asList( x-u, y-u, z+u, x-u, y-u, z-u, x+u, y-u, z-u, x+u, y-u, z+u ) );
        }
        return new ArrayList( Arrays.asList(px1,py1,pz1,px2,py2,pz2,px3,py3,pz3,px4,py4,pz4) );
    }
}