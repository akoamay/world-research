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


public class WorldCube{
    public int cx;
    public int cy;
    public int cz;
    int[][][][] cdata;

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

    public Chunk[] chunks = new Chunk[6];

    public WorldCube( int x, int y, int z ){
        this.cx = x;
        this.cy = y;
        this.cz = z;

        for ( int i=0;i<6;i++){
            chunks[i] = new Chunk(40,10,40);
        }
    }

    public void render(){
        for ( int i=0;i<6;i++){
            glPushMatrix();
            if (i==1) glRotatef(90, 1, 0, 0);
            if (i==2) glRotatef(180, 1, 0, 0);
            if (i==3) glRotatef(270, 1, 0, 0);
            if (i==4) glRotatef(90, 0, 0, 1);
            if (i==5) glRotatef(-90, 0, 0, 1);

            chunks[i].render();
            glPopMatrix();
        }
    }

}