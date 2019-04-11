import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class LoaderQueue{
    private static LoaderQueue queue = new LoaderQueue();
    private static ExecutorService service;

    private LoaderQueue(){
        //service = Executors.newCachedThreadPool();
        service = Executors.newFixedThreadPool(10);
    }

    public static LoaderQueue getInstance(){
        return queue;
    }

    public Future load( String fname, int level, int x, VoxelSet vs ){
        if ( service.isShutdown() ){
            System.out.println( "shutdowne" );
        }

        try{
        //    System.out.println( "Loader.load" );
            return service.submit( new TileLoader( fname, level, x, vs ) );
        }catch( Exception e ){
            e.printStackTrace();
        }
        return null;
    }
}