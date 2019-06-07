class ChunkSet{
    private final static int LEN = 16; 
    public Chunk[] chunks = new Chunk[LEN*LEN];

    public ChunkSet(){
        for ( int i = 0; i < LEN * LEN; i++ ){
            chunks[i] = new Chunk();
        }
    }
}