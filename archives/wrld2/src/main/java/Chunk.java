class Chunk{
    private final static int LEN = 32;
    public Cell[] cells = new Cell[LEN*LEN];

    public Chunk(){
        for ( int i = 0; i < LEN * LEN; i++ ){
            cells[i] = new Cell();
        }
    }
}