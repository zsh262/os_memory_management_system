import javax.swing.*;
import java.util.ArrayList;
import java.util.Objects;
/**
 * 输出阻塞线程类，负责处理输出阻塞队列中的进程，检查其是否满足唤醒条件，并将符合条件的进程转移到就绪队列
 */
public class OutputBlockThread extends Thread{

    public void updateClockDisplay(int time) {
        // 在EDT线程中更新UI
        SwingUtilities.invokeLater(() -> {
            // 更新时钟显示区域
           Main.gui. clockTextArea.setText("当前系统时间: " + time + "s" );
        });
    }

    /**
     * 处理并记录从输出阻塞队列转移到就绪队列的进程列表
     * @param processList 转移的进程列表
     */

    private void ALL_IO(ArrayList<PCB> processList) {
        LogAll.OutputBlockToReady(processList);
    }
    /**
     * 记录输出阻塞队列的状态信息
     */
    // 调用日志记录，记录输出阻塞队列的当前状态
    private void ALL_IO(){
        LogAll.OutputBlock();
    }
    private void outputBlockProcess(){

        ArrayList<PCB> pcbList = new ArrayList<>() ;
        // 遍历输出阻塞队列，直到队列为空
        while(!OSKernel.outputBlockQueue.isEmpty()){

            PCB peek = OSKernel.outputBlockQueue.peek();
            pcbList.add(peek);
            // 弹出队列头部的进程，并添加到多级就绪队列
            if(peek.Bq2Info[1] + 3 <= ClockInterruptHandlerThread.getCurrentTime()){
                OSKernel.AddtoReadyQueue(Objects.requireNonNull(OSKernel.outputBlockQueue.poll()));
                LogAll.bb2.append("进程ID").append(peek.getPid()).append("/").append("进入时间").append(peek.Bq2Info[1]).append("/")
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
    /**
     * 线程主逻辑，持续处理输出阻塞队列
     */

    @Override
    public void run() {

        while (true) {

            SyncManager.lock.lock();
            try {

                SyncManager.outputBlockCondition.await();

                SyncManager.semaphore.acquire();      // 等待开始信号
                if(!Main.running){
                    continue;
                }
                updateClockDisplay(ClockInterruptHandlerThread.getCurrentTime());
                Main.gui.updateProcessDisplay();
                LogAll.pp =  new StringBuilder( "作业/进程调度事件:\n"+LogAll.log.toString() + "进程状态统计信息:\n"
                        + LogAll.end +LogAll.bb1 + "]\n" + LogAll.bb2 + "]\n");
                outputBlockProcess();     // 处理输出阻塞队列
                ALL_IO();              // 记录输出阻塞队列的状态
                // 唤醒时钟线程，通知其可以继续推进时间
                 // 更新GUI显示

                SyncManager.clkCondition.signal();

            } catch (Exception e) {
                // 捕获异常，如果线程被中断，则打印异常信息
                e.printStackTrace();
            } finally {
                // 释放锁，允许其他线程访问共享资源
                // 在此处释放锁
                SyncManager.lock.unlock();
                SyncManager.semaphore.release();
            }
        }
    }
}
