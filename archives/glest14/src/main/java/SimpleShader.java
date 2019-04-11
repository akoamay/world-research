import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class SimpleShader extends Shader{

    public int modelMatLoc;
    public int vpMatLoc;

    public SimpleShader( String vs, String fs ){
            super( vs,fs, true );
            modelMatLoc = glGetUniformLocation(getID(),"modelMat");
            vpMatLoc = glGetUniformLocation(getID(),"vpMat");
    }

    @Override
    public void setupAttributes(){
        int loc = 0;
        int prop = 1;
        int norm = 2;
        /*
        int loc = glGetAttribLocation(this.getID(),"loc");
        System.out.println( "loc=" + loc );
        int prop = glGetAttribLocation(this.getID(),"prop");
        System.out.println( "prop=" + prop );
        */
     //   int bright = glGetAttribLocation(this.getID(),"bright");

        glVertexAttribPointer(loc,3,GL_FLOAT,false,36,0);
        glEnableVertexAttribArray(loc);

        glVertexAttribPointer(prop,3,GL_FLOAT,false,36,12);
        glEnableVertexAttribArray(prop);

        glVertexAttribPointer(norm,3,GL_FLOAT,false,36,24);
        glEnableVertexAttribArray(norm);

    }
    @Override
    public void dispose(){

    }
}