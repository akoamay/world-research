import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTTextureBufferObject;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;
/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {

    boolean mouse = false;
    int mx,my;
    float[] m;

    public String getGreeting() {
        return "Hello world.";
    }

    int WIDTH = 800, HEIGHT = 600;
    int dx, dy;

    public App(){
       try {
            Display.setDisplayMode(new DisplayMode( WIDTH, HEIGHT ));
            Display.setTitle("Episode 3 - OpenGL Rendering");
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }

        m = new float[16];

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glFrustum(-.5,.5,-.5,.5,1,100000);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE );

        //VoxelSet vs = new VoxelSet(20,20,1);
        WorldCube cube = new WorldCube(20,1,20);

            int cc = 0;

        Matrix4f modelViewMatrix = new Matrix4f();
        Matrix4f translateMatrix = new Matrix4f();
        Matrix4f rotateMatrix = new Matrix4f();
        translateMatrix.m32 = -3.0f;

        FloatBuffer buff = BufferUtils.createFloatBuffer(16);

        while (!Display.isCloseRequested()) {

            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);;

            glDepthMask(true);
            glClear(GL_COLOR_BUFFER_BIT| GL_DEPTH_BUFFER_BIT);

           // glLoadIdentity();
            modelViewMatrix.setIdentity();
            Matrix4f.mul(modelViewMatrix,translateMatrix,modelViewMatrix);


            float n = (float)Math.sqrt(dx*dx+dy*dy);
            if (n!=0){
                Matrix4f rot = genRotateMatrix(n*Math.PI/180, dy/n, -dx/n);
                //Matrix4f.mul(rotateMatrix,rot,rotateMatrix);
                Matrix4f.mul(rot,rotateMatrix,rotateMatrix);
            }


            Matrix4f.mul(modelViewMatrix,rotateMatrix,modelViewMatrix);



            modelViewMatrix.store(buff);
            buff.rewind();

            glLoadMatrix(buff);




            cc++;

            dx = 0;
            dy = 0;

//            glRotatef(my/10,1.0f,0.0f,0.0f);
//            glRotatef(m+x/10,0.0f,1.0f-Math.abs(a), -Math.abs(a));

            if ( Mouse.isButtonDown(0)){
                dx = Mouse.getDX();
                dy = Mouse.getDY();

                mouse = true;
                /*
                mx+=dx;
                my+=dy;
                */
                mx = Mouse.getX()-WIDTH/2;
                my = Mouse.getY()-HEIGHT/2;

            }else{
                mouse = false;
            }

            //vs.render();
            cube.render();

            int dw = Mouse.getDWheel();
            if ( dw != 0 ){
                if ( dw < 0 ){
                }else{
                }
            }

            Display.update();
            Display.sync(60);
        }
        Display.destroy();
        System.exit(0);
    }

    private Matrix4f genRotateMatrix( double t, float x, float y ){
        float c = (float)Math.cos(t);
        float s = (float)Math.sin(t);
        Matrix4f m = new Matrix4f();
        m.m00=c+x*x*(1-c);  m.m10=x*y*(1-c);    m.m20=y*s;  m.m30=0f;
        m.m01=x*y*(1-c);    m.m11=c+y*y*(1-c);  m.m21=-x*s; m.m31=0f;
        m.m02=-y*s;         m.m12=x*s;          m.m22=c;    m.m32=0f;
        m.m03=0f;           m.m13=0f;           m.m23=0f;   m.m33=1f;
        return m;
    }


    public static void main(String[] args) {
        new App();
    }

}
