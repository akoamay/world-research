import java.io.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

class FloatArrays{
    public float[] vtxFloatArray;
    public float[] nrmFloatArray;
    public float[] colFloatArray;
    public FloatArrays( float[] vtx, float[] nrm, float[] col){
        this.vtxFloatArray = vtx;
        this.nrmFloatArray = nrm;
        this.colFloatArray = col;
    }
}