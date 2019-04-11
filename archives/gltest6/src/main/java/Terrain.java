class Terrain{
    int [][][] data;

    public Terrain( int x, int y, int z ){
        data = new int[x][y][z];
        for (int i = 0; i < x; i++){
            for (int j = 0; j < y; j++){
                double dist = Math.sqrt(Math.pow(i-x/2,2)+Math.pow(j-y/2,2)) * 10;

                int h = (int)(z * ((Math.cos(dist/5*Math.PI/180)+1.0)) )/4;

                for (int k = 0; k < z; k++){

  //                  if ( k == 10 )
 //                       data[i][j][k] = 1;
                        //data[i][j][k] = (int)(Math.random()*2);

                    if ( k < h ){
                        data[i][j][k] = 50;
                    }else if ( k < z/6 ){
                        data[i][j][k] = 1;
                    }

                }
            }
        }
    }
}