import java.util.HashMap;

public class InstancePool {


    private static volatile HashMap<String, InstanceContainer> pool;

    public synchronized static void init() {
         pool = new HashMap<>();
    }

    public synchronized static void set(String instanceID, InstanceContainer instanceContainer) {
        pool.put(instanceID, instanceContainer);
    }

    public synchronized static InstanceContainer get(String instanceID) {
        return pool.get(instanceID);
    }

    public synchronized static void remove(String instanceID) {
        pool.remove(instanceID);
    }
}
