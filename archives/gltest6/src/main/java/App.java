import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTTextureBufferObject;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

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

    int wl = 16;
    Texture texture;
    float px,py,pz;
    int tick;

    int vss = 16;
    float bs = 10;

    float ofx;
    float ofz;

    float th_o;

    float oox;
    float ooz;

    float opx;
    float opz;

    float pdx,pdy,pdz;

    float scale = 1.0f;

    VoxelSet[] vs;
    float rx = 0.0f, ry = 0.0f, rz = 0.0f;

    int idx[] = new int[wl*wl];

    float height = 50;

    int mx, my;

    boolean mouse = false;

    int scx = 0;
    int scy = 0;

    final static int UND = -1; 
    final static int DEC = 0; 
    final static int INC = 1; 
    int way_x = UND;
    int way_y = UND;

    public String getGreeting() {
        return "Hello world.";
    }

    int WIDTH = 1600, HEIGHT = 1000;

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

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glFrustum(-1,1,-1,1,1,1000);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();


        GLU.gluLookAt(0.0f, 10.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2f, 0.0f, 0.2f);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE); 
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_ALPHA_TEST);

        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);  
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        FloatBuffer blue = BufferUtils.createFloatBuffer(4);
        blue.put(new float[]{0.5f, 0.6f, 1.0f, 1.0f}).flip();
        
        glEnable(GL_FOG);
        glFog(GL_FOG_COLOR , blue);
        glFogf(GL_FOG_DENSITY, 0.5f);
        glFogi(GL_FOG_MODE , GL_LINEAR);
        glFogi(GL_FOG_START , 250 );
        glFogi(GL_FOG_END , 300);


        texture = loadTexture();

        vs = new VoxelSet[wl*wl];
        int wx = 1000;
        int wy = 1000;
        int wh = 100;
        Terrain terrain = new Terrain(wx,wy,wh);
        for (int i = 0; i< wl; i++){
            for (int j = 0; j< wl; j++){
                vs[i*wl+j] = new VoxelSet(i,bs,vss,vss,wh,
                    terrain,
                    wx/2 - vss * (wl/2) + j * vss,
                    wy/2 - vss * (wl/2) + i * vss
                );
                idx[i*wl+j] = i*wl+j;
            }
        }

        px = 0.0f;
        py = 0.0f;
        pz = 0.0f;

        ofx = 0.0f;
        ofz = 0.0f;

        oox = 0.0f;
        ooz = 0.0f;

        th_o = 0.0f;
        float dth = 0.0f;

        boolean ymove = false;
        boolean tmove = false;

        FloatBuffer pos = BufferUtils.createFloatBuffer(4);
        pos.put( new float[]{0.0f,50.0f, 0.0f,1.0f} ).flip();
        glLight(GL_LIGHT0, GL_POSITION, pos );

        IntBuffer sel = BufferUtils.createIntBuffer(100);
        glSelectBuffer(sel);



        // FBO
        IntBuffer frameBuff = ByteBuffer.allocateDirect(1*4).order(ByteOrder.nativeOrder()).asIntBuffer();
        int frameBuffId = EXTFramebufferObject.glGenFramebuffersEXT();
        EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, frameBuffId );

        // TEX
        int texBuffId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texBuffId);
        glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA, WIDTH, HEIGHT, 0, GL_RGBA, GL_UNSIGNED_INT, (ByteBuffer)null );
        glBindTexture(GL_TEXTURE_2D, 0);

        // RBO
        IntBuffer renderBuff = ByteBuffer.allocateDirect(1*4).order(ByteOrder.nativeOrder()).asIntBuffer();
        int renderBuffId = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, renderBuffId );

        // RBO allocate memory
        EXTFramebufferObject.glRenderbufferStorageEXT( EXTFramebufferObject.GL_RENDERBUFFER_EXT, GL_DEPTH_COMPONENT, WIDTH, HEIGHT );
        // RBO default binding
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0 );

        // RBO -> FBO
        EXTFramebufferObject.glFramebufferRenderbufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                                                            EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                                                                EXTFramebufferObject.GL_RENDERBUFFER_EXT, renderBuffId );

        // FBO default binding
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0 );

        System.out.println( EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT) );


        System.out.println( "frameBuffid=" + frameBuffId + " renderBuffId=" + renderBuffId );

        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE );

        while (!Display.isCloseRequested()) {

            glClearColor(0.5f, 0.6f, 1.0f, 1.0f);;

            glDepthMask(true);
            glClear(GL_COLOR_BUFFER_BIT| GL_DEPTH_BUFFER_BIT);

            glLoadIdentity();


            glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            glTranslatef(0.0f, -150.0f, 0.0f);


            glRotatef(rx,-1.0f,0.0f,0f);

            shift();
            glScalef(scale,scale,scale);
            glTranslatef(px+ofx+oox, 0, -(pz+ofz+ooz));
            glRotatef(ry,0f,1f,0f);


            if ( Mouse.isButtonDown(0)){
                int dx = Mouse.getDX();
                int dy = Mouse.getDY();

                mouse = true;
                mx = Mouse.getX();
                my = Mouse.getY();

                if ( Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ){
                    if ( dx != 0 ){
                        ry+=dx;

                        ymove = toggley(true, ymove );
                        tmove = togglem(false, tmove);

                        float dox = opx+oox;
                        float doz = opz+ooz;

                        dth = (float)(Math.PI*ry/180.0f) - th_o;
                        ofx = -dox-(float)( -dox*Math.cos(dth) + doz*Math.sin(dth) );
                        ofz = -doz-(float)( -dox*Math.sin(dth) - doz*Math.cos(dth) );
                    }else{
                        if ( dy != 0 ) rx+=dy;
                    }
                }else{
                    if (dx!=0 || dy != 0 ){
                        float ddx = (float)dx/10.0f;
                        float ddy = (float)dy/10.0f;

                        float ang = (float)(180*Math.atan2(ddy,ddx)/Math.PI);
                        float len = (float)(Math.sqrt(ddx*ddx+ddy*ddy));

                        /*
                        float tt = ry % 360;
                        if ( tt < 0 ) tt = 360 - tt;
                        float th = (float)(Math.abs( tt * Math.PI/180 ));
                        */
                        float th = -(float)( ry * Math.PI/180 );

                        float sx,sy;
                        /*
                        if ( tt > 0f && tt < 180f ){
                            sx = (float)( ddx * Math.cos(th) + ddy * Math.sin(th) );
                            sy = (float)(-ddx * Math.sin(th) + ddy * Math.cos(th) );
                        }else{
                            */
                            sx = (float)( ddx * Math.cos(th) - ddy * Math.sin(th) );
                            sy = (float)( ddx * Math.sin(th) + ddy * Math.cos(th) );
                        //}

                        ddx*= 1.0f/scale;
                        ddy*= 1.0f/scale;
                        px+=ddx;
                        pz+=ddy;
                        opx += ddx;
                        opz += ddy;

                        pdx+=sx;
                        pdz-=sy;
               // System.out.println("pdx="+pdx+",pdz="+pdz);
                        //shift();

                        tmove = togglem(true, tmove);
                    }
                    ymove = toggley(false, ymove);
                }
            }else{
                mouse = false;
            }


            int dw = Mouse.getDWheel();
            if ( dw != 0 ){
                if ( dw > 0 ){
                    scale *= 1.1f;
                }else{
                    scale *= 0.9f;
                }
                System.out.println(scale);
            }


            /*
            EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, frameBuffId );
            EXTFramebufferObject.glFramebufferTexture2DEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                                                                EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
                                                                GL_TEXTURE_2D, texBuffId, 0 );

            EXTFramebufferObject.glFramebufferRenderbufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                                                                EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                                                                EXTFramebufferObject.GL_RENDERBUFFER_EXT, renderBuffId );
            glClearColor(0f,0f,0f,0f);
            glClear(GL_COLOR_BUFFER_BIT| GL_DEPTH_BUFFER_BIT);

            glDisable(GL_BLEND);
            glDisable(GL_FOG);
            glDisable(GL_LIGHTING);
            glDisable(GL_LIGHT0);  
            render2();
            if (mouse){ 
                ByteBuffer pixels = BufferUtils.createByteBuffer(3);
                glReadPixels(mx, my, 1, 1, GL_RGB, GL_UNSIGNED_BYTE, pixels);
                int r = (pixels.get(0)&0xff);
                int g = (pixels.get(1)&0xff);
                int b = (pixels.get(2)&0xff);
                System.out.println( "(" + r + "," + g + "," + b + ")" );
            }

            EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0 );

            glEnable(GL_FOG);
            glEnable(GL_LIGHTING);
            glEnable(GL_LIGHT0);  
            */


            glDisable(GL_BLEND);
            glAlphaFunc(GL_EQUAL, 1.0f);
            render();

            glEnable(GL_BLEND);
            glDepthMask(false);
            glAlphaFunc(GL_LESS, 1.0f);
            render();


            
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
        System.exit(0);
    }

    public boolean togglem( boolean flg, boolean tmove){
        if ( !flg ){
            if ( tmove ){
                System.out.println( "togglem off" );
                return false;
            }
            return false;
        }else{
            if ( !tmove ){
                System.out.println( "togglem on" );
                return true;
            }
            return true;
        }
    }
    public boolean toggley( boolean flg, boolean ymove){
        if ( !flg ){
            if ( ymove ){ 
                th_o = (float)(Math.PI*ry/180.0f);
                oox += ofx;
                ooz += ofz;
                ofx = 0.0f;
                ofz = 0.0f;
                System.out.println( "toggley off" );
                return false;
            }
            return false;
        }else{
            if ( !ymove ){ 
                System.out.println( "toggley on" );
                return true;
            }
            return true;
        }
    }


    private void shift(){
        float th = (float)(ry * Math.PI/180 );

        //if ( pdz >= bs*vss*scale ){
        if ( pdz >= bs*vss*scale ){
            int t[] = new int[wl];

            if ( way_y == UND ){
                scy = wl - 1;
            }else{
                if ( way_y == DEC ){
                    scy--;
                    if ( scy < 0 ) scy = wl - 1;
                }
            }
            way_y = DEC;

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

                    if ( i == scy ){
                        vs[wl*i+j].py-=vss*wl;
                        vs[wl*i+j].update();
                    }
                }
            }

            float ttx = (float)(-pdz*Math.sin(th));
            float tty = (float)(-pdz*Math.cos(th));

            System.out.println("pz+");


            //s1

            px += ttx/scale;
            pz -= tty/scale;

            opx += ttx/scale;
            opz -= tty/scale;
            pdz = 0;

        };


        if ( pdz <= -bs*vss*scale ){
            int t[] = new int[wl];

           if ( way_y == UND ){
                scy = 0;
            }else{
                if ( way_y == INC ){
                    scy++;
                    if ( scy > wl-1 ) scy = 0;
                }
            }
            way_y = INC;

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

                    if ( i == scy ){
                        vs[wl*i+j].py+=vss*wl;
                        vs[wl*i+j].update();
                    }
                }
            }
            float ttx = (float)(-pdz*Math.sin(th));
            float tty = (float)(-pdz*Math.cos(th));
 //           pdx -= ttx;

            //s2
            px += ttx/scale;
            pz -= tty/scale;

            opx += ttx/scale;
            opz -= tty/scale;
            pdz = 0;


            System.out.println("pz-");
        };

        if ( pdx >= bs*vss*scale ){
            int t[] = new int[wl];

            if ( way_x == UND ){
                scx = wl - 1;
            }else{
                if ( way_x == DEC ){
                    scx--;
                    if ( scx < 0 ) scx = wl - 1;
                }
            }
            way_x = DEC;

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

                    if ( i == scx ){
                        vs[wl*j+i].px-=vss*wl;
                        vs[wl*j+i].update();
                    }
                }
            }



            float ttx = (float)(-pdx*Math.cos(th));
            float tty = (float)(-pdx*Math.sin(th));
  //          pdz -= tty;

            //s3
            px += ttx/scale;
            pz += tty/scale;

            opx += ttx/scale;
            opz += tty/scale;
            pdx = 0;

            System.out.println("px+");
        };

        if ( pdx <= -bs*vss*scale ){
            int t[] = new int[wl];

            if ( way_x == UND ){
                scx = 0;
            }else{
                if ( way_x == INC ){
                    scx++;
                    if ( scx > wl-1 ) scx = 0;
                }
            }
            way_x = INC;


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

                    if ( i == scx ){
                        vs[wl*j+i].px+=vss*wl;
                        vs[wl*j+i].update();
                    }

                }

            }
            float ttx = (float)(-pdx*Math.cos(th));
            float tty = (float)(-pdx*Math.sin(th));

            //s4
            px += ttx/scale;
            pz += tty/scale;

            opx += ttx/scale;
            opz += tty/scale;

            pdx = 0;

            System.out.println("px-");
        };
    }

    private void render2(){
        for (int i = 0; i< wl*wl; i++){
            float o = ( 1/2.0f - wl/2.0f ) * vss * bs;

            int j = idx[i];
            float xx = o + i%wl * vss * bs;
            float zz = o + i/wl * vss * bs;

            glPushMatrix();

            glTranslatef(xx,0,zz);

            vs[j].render();
            glPopMatrix();
        }
    }

    private void render(){
        for (int i = 0; i< wl*wl; i++){
            float o = ( 1/2.0f - wl/2.0f ) * vss * bs;

            int j = idx[i];
            float xx = o + i%wl * vss * bs;
            float zz = o + i/wl * vss * bs;

            glPushMatrix();

            glTranslatef(xx,0,zz);

            vs[j].render( texture.getTextureID());
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
