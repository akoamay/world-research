package ymok;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

class GameCanvas extends Canvas{

    int len = 256;

    BufferedImage offImage = new BufferedImage(len,len, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = (Graphics2D)offImage.getGraphics();

    public int map[] = new int[ len*len ];

    public GameCanvas(){
        setSize(600,600);
        setVisible(true);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        init();
    }

    public void init(){
    }

    public void run(){
        WritableRaster raster;
        try{
            while (true){
                for ( int i = 0; i < len; i++ ){
                    for ( int j = 0; j < len; j++ ){
                        map[i*len+j] = ( (int)(Math.random()*255) << 16 ) | 100 << 8 | 100;
                    }
                }
//                offImage.setRGB(startX, startY, w, h, rgbArray, offset, scansize);
                offImage.setRGB(0, 0, len, len, map, 0, len);

//                raster = (WritableRaster)offImage.getRaster();
 //               raster.setDataElements(0,0,len,len,map);
                //raster.setPixels(0,0,len,len,map);
                Thread.sleep(100);
                repaint();
                System.out.println("print");
            }
        }catch( Exception e ){
            e.printStackTrace();
        }
    }

    public void update(Graphics g){ paint(g); }
    public void paint(Graphics g){
        g.drawImage(offImage,0,0,600,600,this);
    }
}