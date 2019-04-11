import java.util.concurrent.*;
import java.io.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

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

public class TileLoader implements Callable<Void>{

    String fname;
    int level;
    int s;
    int is = 0;
    VoxelSet vs;

    float bs;
    public TileLoader( String fname, int level, int s, VoxelSet vs){
        this.fname = fname;
        this.level = level;
        this.s = s;
        this.vs = vs;
        this.bs = 10f / (float)Math.pow( 2,  level - 1 );
       // System.out.println("TileLoader");
    }

    @Override
    public Void call(){
        System.out.println("called");
        int h = 2;
        byte[][][] data = new byte[s][s][h];
        for ( int i = 0; i < s; i++ ){
            for ( int j = 0; j < s; j++ ){
                for ( int k = 0; k < h; k++ ){
                    data[j][i][k] = 1;
                }
            }
        }

        if ( vs != null ){
            vs.onLoad( genFloatArray( data ) );
        }else{
            System.out.println("vs null");
        }

        return null;
    }
    private final int FRONT = 0;
    private final int BACK = 1;
    private final int LEFT = 2;
    private final int RIGHT = 3;
    private final int TOP = 4;
    private final int BOTTOM = 5;
    private ArrayList<Float> getFacetArray(int direction, float _x, float _y, float _z ){
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
    public FloatArrays genFloatArray( byte data[][][] ){
       ArrayList<Float> vtxArraylist = new ArrayList<Float>(); 
       ArrayList<Float> texArraylist = new ArrayList<Float>(); 
       ArrayList<Float> nrmArraylist = new ArrayList<Float>(); 
       ArrayList<Float> colArraylist = new ArrayList<Float>();


       int cc = 0;
       int x = data.length;
       int y = data[0].length;
       int z = data[0][0].length;
        for (int i = 0; i < y; i++){
            for (int j = 0; j < x; j++){
                for (int k = 0; k < z; k++){
                    int h = data[j][i][k];
                    int ci = x*z*j + x*k + k;

                    float cr = 0.2f*level;
                    float cg = 0.1f;
                    float cb = 0.1f;
                    /*
                    depth = 200*100;
                    float cr = ( depth / 200.0f );
                    float cg = cr;
                    float cb = cr;
                    */

                    float  o = (x - 1) / 2.0f;
                    if ( h != 0 ){
                        //front
                        if ( i > 0 && data[j][i-1][k] != h ){
                            vtxArraylist.addAll( getFacetArray(FRONT,j-o,i-o,k) );
                            texArraylist.addAll( getTexArray(FRONT,h) );
                            nrmArraylist.addAll( getNrmArray(FRONT) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //back
                        if ( i < x-1 && data[j][i+1][k] != h ){
                            vtxArraylist.addAll( getFacetArray(BACK,j-o,i-o,k) );
                            texArraylist.addAll( getTexArray(BACK,h) );
                            nrmArraylist.addAll( getNrmArray(BACK) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //left
                        if ( j > 0 && data[j-1][i][k] != h ){
                            vtxArraylist.addAll( getFacetArray(LEFT,j-o,i-o,k) );
                            texArraylist.addAll( getTexArray(LEFT,h) );
                            nrmArraylist.addAll( getNrmArray(LEFT) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //right
                        if ( j < y-1 && data[j+1][i][k] != h ){
                            vtxArraylist.addAll( getFacetArray(RIGHT,j-o,i-o,k) );
                            texArraylist.addAll( getTexArray(RIGHT,h) );
                            nrmArraylist.addAll( getNrmArray(RIGHT) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //top
                        if ( k >= z-1 || data[j][i][k+1] != h ){
                            vtxArraylist.addAll( getFacetArray(TOP,j-o,i-o,k) );
                            texArraylist.addAll( getTexArray(TOP,h) );
                            nrmArraylist.addAll( getNrmArray(TOP) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                        //bottom
                        if ( k == 0 || data[j][i][k-1] != h ){
                            vtxArraylist.addAll( getFacetArray(BOTTOM,j-o,i-o,k) );
                            texArraylist.addAll( getTexArray(BOTTOM,h) );
                            nrmArraylist.addAll( getNrmArray(BOTTOM) );
                            colArraylist.addAll( getColArray( cr, cg, cb ) );
                        }
                    }
                }
            }
        }

//        if ( vtxArraylist.size() != 0)
       //System.out.println( "val.size=" + vtxArraylist.size() + " cc=" + cc );
        float[] vtxFloatArray = new float[vtxArraylist.size()];
        for (int i = 0; i < vtxArraylist.size(); i++ ){
            vtxFloatArray[i] = vtxArraylist.get(i).floatValue();
        }
        float[] texFloatArray = new float[texArraylist.size()];
        for (int i = 0; i < texArraylist.size(); i++ ){
            texFloatArray[i] = texArraylist.get(i).floatValue();
        }
        float[] nrmFloatArray = new float[nrmArraylist.size()];
        for (int i = 0; i < nrmArraylist.size(); i++ ){
            nrmFloatArray[i] = nrmArraylist.get(i).floatValue();
        }
        float[] colFloatArray = new float[colArraylist.size()];
        for (int i = 0; i < colArraylist.size(); i++ ){
            colFloatArray[i] = colArraylist.get(i).floatValue();
        }

        return new FloatArrays(vtxFloatArray,texFloatArray,nrmFloatArray,colFloatArray);
    }
}