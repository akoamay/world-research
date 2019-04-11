import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {

    int wl = 16;
    Texture texture;
    float px,py,pz;
    float pdx,pdy,pdz;
    float pmx, pmy, pmz;
    int tick;

    int vss = 1;
    float bs = 1;

    VoxelSet[] vs;
    float rx = 0.0f, ry = 0.0f, rz = 0.0f;

    int idx[] = new int[wl*wl];
    SimpleShader ss;

    public String getGreeting() {
        return "Hello world.";
    }

    public String readFile( String path ){
        InputStream is = this.getClass().getResourceAsStream(path);
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public App(){
       try {
            Display.setDisplayMode(new DisplayMode(640, 480));
            Display.setTitle("Episode 3 - OpenGL Rendering");
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glFrustum(-1,1,-1,1,1,1000);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE); 
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_ALPHA_TEST);


//        glEnable(GL_LIGHTING);
 //       glEnable(GL_LIGHT0);  
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        FloatBuffer blue = BufferUtils.createFloatBuffer(4);
        blue.put(new float[]{0.5f, 0.6f, 1.0f, 1.0f}).flip();
        
        glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        glTranslatef(0.0f, -20.0f, 0.0f);

        FloatBuffer pos = BufferUtils.createFloatBuffer(4);
        pos.put( new float[]{0.0f,200.0f, 0.0f,1.0f} ).flip();
        //glLight(GL_LIGHT0, GL_POSITION, pos );

        texture = loadTexture();

        vs = new VoxelSet[wl*wl];
        for (int i = 0; i< wl; i++){
            for (int j = 0; j< wl; j++){
                vs[i*wl+j] = new VoxelSet(i,bs,vss,vss,4);
                idx[i*wl+j] = i*wl+j;
            }
        }


        px = 0.0f;
        py = 0.0f;
        pz = 0.0f;

        pdx = 0.0f;
        pdy = 0.0f;
        pdz = 0.0f;

        pmx = 0.0f;
        pmy = 0.0f;
        pmz = 0.0f;

        ss = new SimpleShader(readFile("/Shader_v.glsl"),readFile("Shader_f.glsl"));
        ss.enable();


        while (!Display.isCloseRequested()) {

            glClearColor(0.5f, 0.6f, 1.0f, 1.0f);;

            glDepthMask(true);
            glClear(GL_COLOR_BUFFER_BIT| GL_DEPTH_BUFFER_BIT);


            if ( Keyboard.isKeyDown(Keyboard.KEY_0) ){
                ry = 90;
            }

            if ( Mouse.isButtonDown(0)){
                int dx = Mouse.getDX();
                int dy = Mouse.getDY();
                if ( Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ){
                    if ( dx != 0 ){
                        //glRotatef( (float)dx, 0.0f, 1.0f, 0.0f );
                        ry+=dx;
                    }

                    if ( dy != 0 ){
                        glRotatef( (float)dy, -1.0f, 0.0f, 0.0f );
                        rx+=dy;
                    }
                }else{
                    if (dx!=0 || dy != 0 ){
                        float ddx = (float)dx/10.0f;
                        float ddy = (float)dy/10.0f;

                        float sx, sy;
                        float tt = ry % 360;
                        if ( tt < 0 ) tt = 360 - tt;

                        float th = (float)(Math.abs( tt * Math.PI/180 ));

                        if ( tt > 0f && tt < 180f ){
                            sx = (float)( ddx * Math.cos(th) + ddy * Math.sin(th) );
                            sy = (float)(-ddx * Math.sin(th) + ddy * Math.cos(th) );
                        }else{
                            sx = (float)( ddx * Math.cos(th) - ddy * Math.sin(th) );
                            sy = (float)( ddx * Math.sin(th) + ddy * Math.cos(th) );
                        }

                        glTranslatef( ddx, 0.0f, -ddy );
                        //glTranslatef(sx, 0.0f, sy );

                        px+=ddx;
                        pz+=ddy;

                        pdx+=sx;
                        pdz+=sy;

                        pmx += ddx;
                        pmz += ddy;

                        if ( -pdz >= bs*vss ){
                            int t[] = new int[wl];
                            for (int i = 0; i < wl; i++ ){
                                for (int j = 0; j < wl; j++ ){
                                    if ( i == 0 ){
                                        t[j] = idx[wl*(wl-1)+j];
                                        idx[wl*(wl-1)+j] = idx[wl*(wl-2)+j];
                                    }else if ( i == wl-1 ){
                                        idx[j] = t[j];
                                    }else{
                                        idx[wl*(wl-1-i)+j] = idx[wl*(wl-2-i)+j];
                                    }
                                }
                            }

                            float ttx = (float)(pdz*Math.sin(th));
                            float tty = (float)(pdz*Math.cos(th));
                            if ( tt > 0 && tt < 180 ) ttx *= -1;
 //                           if ( tt > 0 && tt < 180 ) tty *= -1;
//                            if ( tt > 180 && tt < 360 ) tty *= -1;

                            glTranslatef(-ttx, 0f, tty);


                            //pmx -= tty; pmz = 0;
                            pz = 0;
                            pdz = 0;
                            System.out.println("pz+");
                        };

                       if ( -pdz <= -bs*vss ){
                            int t[] = new int[wl];
                            for (int i = 0; i < wl; i++ ){
                                for (int j = 0; j < wl; j++ ){
                                    if ( i == 0 ){
                                        t[j] = idx[j];
                                        idx[j] = idx[wl+j];
                                    }else if ( i == wl-1 ){
                                        idx[wl*(wl-1)+j] = t[j];
                                    }else{
                                        idx[wl*i+j] = idx[wl*(i+1)+j];
                                    }
                                }
                            }
                            float ttx = (float)(pdz*Math.sin(th));
                            float tty = (float)(pdz*Math.cos(th));
                            if ( tt > 0 && tt < 180 ) ttx *= -1;
                            //if ( ( th > 0 && th < Math.PI/2.0f ) || ( th > Math.PI && th < ( 3.0f * Math.PI ) / 2.0f ) ) tty *= -1;
 //                           if ( tt > 180 && tt < 360 ) tty *= -1;
                            glTranslatef(-ttx, 0f, tty);
                            //pmx -= tty; pmz = 0;

                            pz = 0;
                            pdz = 0;
                            System.out.println("pz-");
                        };

                        if ( pdx >= bs*vss ){
                            int t[] = new int[wl];
                            for (int i = 0; i < wl; i++ ){
                                for (int j = 0; j < wl; j++ ){
                                    if ( i == 0 ){
                                        t[j] = idx[wl*j+(wl-1)];
                                        idx[wl*j+(wl-1)] = idx[wl*j+(wl-2)];
                                    }else if ( i == wl-1 ){
                                        idx[wl*j] = t[j];
                                    }else{
                                        idx[wl*j+(wl-i-1)] = idx[wl*j+(wl-i-2)];
                                    }
                                }
                            }
                            float ttx = (float)(pdx*Math.cos(th));
                            float tty = (float)(pdx*Math.sin(th));
                            if ( ( tt > 0 && tt < 90 ) || ( tt > 180 && tt < 270 ) ) tty *= -1;
  //                          if ( tt > 180 && tt < 360 ) tty *= -1;
                            glTranslatef(-ttx, 0f, tty);

                            /*
                            glTranslatef(-pmx, 0f, pmz );
                            */
                            //pmx = 0; pmz -= tty;

                            px = 0;
                            pdx = 0;
                            System.out.println("px+");
                        };

                       if ( pdx <= -bs*vss ){
                            int t[] = new int[wl];
                            for (int i = 0; i < wl; i++ ){
                                for (int j = 0; j < wl; j++ ){
                                    if ( i == 0 ){
                                        t[j] = idx[wl*j];
                                        idx[wl*j] = idx[wl*j+1];
                                    }else if ( i == wl-1 ){
                                        idx[wl*j+wl-1] = t[j];
                                    }else{
                                        idx[wl*j+i] = idx[wl*j+i+1];
                                    }
                                }
                            }
                            float ttx = (float)(pdx*Math.cos(th));
                            float tty = (float)(pdx*Math.sin(th));
                            if ( ( tt > 0 && tt < 90 ) || ( tt > 180 && tt < 270 ) ) tty *= -1;
                           // if ( ( tt > 0 && tt < 90 ) || ( tt > 180 && tt < 270 ) ) tty *= -1;
                            //if ( ( th > 0 && th < Math.PI/2.0f ) || ( th > Math.PI && th < ( 3.0f * Math.PI ) / 2.0f ) ) tty *= -1;
   //                         if ( tt > 180 && tt < 360 ) tty *= -1;
                            glTranslatef(-ttx, 0f, tty);
                            //glTranslatef(-pmx, 0f, pmz );
                            //pmx = 0; pmz -= tty;

                            px = 0;
                            pdx = 0;
                            System.out.println("px-");
                        };



                    }
                    
                }
            }


            int dw = Mouse.getDWheel();
            if ( dw != 0 ){
                float s;
                if ( dw > 0 ){
                    s = 1.1f;
                }else{
                    s = 0.9f;
                }
                glScalef(s, s, s);
            }

            /*
            glDisable(GL_BLEND);
            glAlphaFunc(GL_EQUAL, 1.0f);
            render();

            glEnable(GL_BLEND);
            glDepthMask(false);
            glAlphaFunc(GL_LESS, 1.0f);
            render();
            */
            render();
            
            
            //move();

            Display.update();
            Display.sync(60);
        }
        Display.destroy();
        System.exit(0);
    }


    private void render(){

        for (int i = 0; i< wl*wl; i++){
            float o = ( 1/2.0f - wl/2.0f ) * vss * bs;

            int j = idx[i];
            float xx = o + i%wl * vss * bs;
            float zz = o + i/wl * vss * bs;

            glPushMatrix();

                glRotatef( ry, 0.0f, 1.0f, 0.0f );
                glTranslatef(xx,0,zz);
                vs[j].render( texture.getTextureID(),ss );

            glPopMatrix();
        }

    }

    public static void main(String[] args) {
        new App();
    }

	private Texture loadTexture() {
		try {
			return TextureLoader.getTexture("PNG", new FileInputStream(new File("tile.png")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
