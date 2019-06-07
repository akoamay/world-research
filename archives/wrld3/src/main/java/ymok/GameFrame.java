package ymok;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class GameFrame extends Frame{

    public GameFrame(){

        this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				e.getWindow().setVisible(false);
			}
        });
        GameCanvas canvas = new GameCanvas();
        add( canvas );
        pack();
        setVisible(true);
        canvas.run();
        
    }
}