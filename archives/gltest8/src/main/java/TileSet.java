import static org.lwjgl.opengl.GL11.*;

class TileSet{
    public int size;
    public int tsize;

    public int ss;
    public Tile[] tiles;
    public int level;
    public float bs = 10;


    public TileSet( int level, int size, int tsize){
        this.level = level;
        this.size = size;
        this.tsize = tsize;
        ss = size * size;
        bs = (float)(bs /(Math.pow(size,level-1)));
        tiles = new Tile[ss];
        for ( int i = 0; i < ss; i++ ){
            tiles[i] = new Tile( level, tsize, bs);
        }
    }


    public void draw(){
        float vss = tsize;

        for ( int i = 0; i < ss; i++ ){
            float o = -( size - 1 ) * ( vss * bs ) / 2.0f;

            float xx = o + i%size * (vss * bs);
            float zz = o + i/size * (vss * bs);

            glPushMatrix();
            glTranslatef(xx,0f,zz);
            tiles[i].draw();
            glPopMatrix();

        }
    }

}