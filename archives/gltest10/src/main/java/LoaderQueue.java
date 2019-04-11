import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LoaderQueue{
    private static LoaderQueue queue = new LoaderQueue();
    private static ExecutorService service;

    private LoaderQueue(){
        service = Executors.newFixedThreadPool(10);
    }

    public static LoaderQueue getInstance(){
        return queue;
    }

    public void load( String fname, int level, int x, VoxelSet vs ){
        service.submit( new TileLoader( fname, level, x, vs ) );
    }
}