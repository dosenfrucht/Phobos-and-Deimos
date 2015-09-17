import net.demus_intergalactical.serverman.instance.ServerInstance;
import java.util.ArrayList;

/**
 * Created by Nikodemus on 14.09.2015.
 */
public class InstancePool {


    private static volatile ArrayList<ServerInstance> pool;

    public synchronized static void init() {
         pool = new ArrayList<>();
    }

    public synchronized static int add(ServerInstance si) {
        int i = pool.size();
        pool.add(si);
        return i;
    }

    public synchronized static ServerInstance get(int i) {
        return pool.get(i);
    }

    public synchronized static void set(int i, ServerInstance si) {
        pool.set(i, si);
    }

    public synchronized static void remove(int i) {
        set(i, null);
    }
}
