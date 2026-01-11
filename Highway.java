package assignment;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Highway {

    private static int distance = 0;  
    public static volatile boolean useLock = false;

    private static final Lock lock = new ReentrantLock();

    public static void increment(int delta) {
        if (!useLock) {
            int tmp = distance;
            tmp += delta;
            Thread.yield();
            distance = tmp;
        } else {
            lock.lock();
            try {
                distance += delta;
            } finally {
                lock.unlock();
            }
        }
    }

    public static int getDistance() {
        return distance;
    }

    public static void reset() {
        distance = 0;
    }
}
