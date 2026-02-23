import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 同步管理类，包含锁、条件变量
 */
public class SyncManager {
    // 共享锁
    public static final Lock lock = new ReentrantLock();

    // 条件变量：用于时钟线程和进程调度线程之间的同步
    public static final Condition clkCondition = lock.newCondition();  // 时钟线程的条件变量
    public static final Condition jrtCondition = lock.newCondition();
    public static final Condition pstCondition = lock.newCondition();  // 进程调度线程的条件变量
    public static final Condition inputBlockCondition = lock.newCondition();
    public static final Condition outputBlockCondition = lock.newCondition();
    public static final Semaphore semaphore = new Semaphore(1);


}
