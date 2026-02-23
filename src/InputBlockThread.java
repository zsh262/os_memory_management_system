import java.util.ArrayList;
import java.util.Objects;
/**
 * 输入阻塞线程类，负责处理输入阻塞队列中的进程，检查其是否满足唤醒条件，并将符合条件的进程转移到就绪队列
 */
public class InputBlockThread extends Thread{

    /**
     * 处理并记录从输入阻塞队列转移到就绪队列的进程列表
     * @param processList 转移的进程列表
     */
    private void ALL_IO(ArrayList<PCB> processList) {
        LogAll.InputBlockToReady(processList);
    }    // 调用日志记录，记录进程从输入阻塞队列转移到就绪队列
    /**
     * 记录输入阻塞队列的状态信息
     */
    private void ALL_IO(){
        LogAll.InputBlock();
    }
    /**
     * 处理输入阻塞队列中的进程，检查是否可以唤醒
     */
    private void inputBlockProcess(){
        ArrayList<PCB> pcbList = new ArrayList<>() ;
        // 遍历输入阻塞队列，直到队列为空
        while(!OSKernel.inputBlockQueue.isEmpty()){

            PCB peek = OSKernel.inputBlockQueue.peek();
            if(peek.Bq1Info[1] + 2 <= ClockInterruptHandlerThread.getCurrentTime()){
                // 弹出队列头部的进程，并添加到多级就绪队列
                OSKernel.AddtoReadyQueue(Objects.requireNonNull(OSKernel.inputBlockQueue.poll()));
                LogAll.bb1.append("进程ID").append(peek.getPid()).append("/").append("进入时间").append(peek.Bq1Info[1]).append("/")
                        .append("唤醒时间").append(ClockInterruptHandlerThread.getCurrentTime()).append(",");
            }else{
                break ;
            }
        }
        // 如果有进程被唤醒，记录转移信息
        if(!pcbList.isEmpty()){
            ALL_IO(pcbList);
        }

    }

    @Override
    public void run() {

        while (true) {

            SyncManager.lock.lock();
            try {
                // 等待输入阻塞条件被唤醒
                SyncManager.inputBlockCondition.await();
                // 处理输入阻塞队列
                inputBlockProcess();
                // 记录输入阻塞队列的状态
                ALL_IO();

                // 唤醒输出阻塞线程，通知其可以处理输出队列
                SyncManager.outputBlockCondition.signal();



            } catch (Exception e) {
                // 捕获异常，如果线程被中断，则打印异常信息
                e.printStackTrace();
            } finally {
                // 释放锁，允许其他线程访问共享资源
                // 请在此处释放锁
                SyncManager.lock.unlock();
            }
        }
    }
}
