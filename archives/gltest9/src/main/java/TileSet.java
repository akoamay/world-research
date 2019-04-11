import static org.lwjgl.opengl.GL11.*;

class TileSet {
    public int size;
    public int tsize;

    public int ss;
    public Tile[] tiles;
    public int level;
    public float bs = 10;


    public TileSet( int level, int size, int tsize, String preffix ){
        this.level = level;
        this.size = size;
        this.tsize = tsize;
        ss = size * size * size;
        bs = (float)(bs /(Math.pow(size,level-1)));
        tiles = new Tile[ss];

        for ( int i = 0; i < size; i++ ){
            for ( int j = 0; j < size; j++ ){
                for ( int k = 0; k < size; k++ ){
                    int idx = k * size * size + i * size + j;
                    String prx = preffix == "" ? "":preffix+"_";
                    tiles[idx] = new Tile( level, tsize, bs, prx + String.valueOf(idx) );
                }
            }
        }
        /*
        for ( int i = 0; i < ss; i++ ){
            String prx = preffix == "" ? "" : preffix + "_";
            tiles[i] = new Tile( level, tsize, bs,
                String.valueOf(level-1) + "_" + prx + String.valueOf(i) );
        }
        */
    }


    public void draw(){
        float vss = tsize;

        for ( int i = 0; i < size; i++ ){
            for ( int j = 0; j < size; j++ ){
                for ( int k = 0; k < size; k++ ){

                    float o = -( size - 1 ) * ( vss * bs ) / 2.0f;

                    float xx = o + j * (vss * bs);
                    float zz = o + i * (vss * bs);
                    float yy = o + k * (vss * bs);
    //                float xx = o + i%size * (vss * bs);
    //               float zz = o + i/size * (vss * bs);

                    glPushMatrix();
                    glTranslatef(xx,yy,-zz);
                    tiles[k*size*size + i*size+j].draw();
                    glPopMatrix();

                }
            }
        }

    }
}