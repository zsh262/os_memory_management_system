public class ProcessSchedulingHandlerThread extends Thread {
    private  static  Integer timeslice = 0  ;

    public static  void MFQ3(){

        if(OSKernel.readyQueue1.isEmpty() && OSKernel.readyQueue2.isEmpty() && OSKernel.readyQueue3.isEmpty()){return;}
        if( CPU.currentProcess== null || timeslice == 0 ){
            PCB readyFirst = OSKernel.OutfromReadyQueue();

            PCB pcb = CPU.currentProcess ;
            if(pcb != null ){
                pcb = CPU.getInstance().CPU_PRO();
                OSKernel.AddtoReadyQueue(pcb);
            }
            int priority = readyFirst.getPriority();
            CPU.getInstance().CPU_REC(readyFirst);
            switch (priority){
                case 1 : timeslice =1 ;
                break;
                case 2 : timeslice =2 ;
                break ;
                case 3 : timeslice =4 ;
                break ;

            }
        }
        timeslice--;

    }

    @Override
    public void run() {
        // 持续运行线程，模拟进程调度
        while (true) {
            // 获取锁，确保线程同步
            // 获取锁，确保其他线程不能同时访问共享资源
            SyncManager.lock.lock();
            try {
                // 等待时钟中断信号，直到时钟线程发出信号
                // 实现等待时钟中断的逻辑，让此线程在没有时钟信号时阻塞
                SyncManager.pstCondition.await();
                // *** 提示：在此处模拟进程调度 ***

                //三级调度
                MFQ3();
                // 模拟进程调度完成后的输出
                // 在此处输出 "完成进程调度" 或者其他调度完成后的提示信息
                if(CPU.getInstance()!= null){
                    CPU.getInstance().runProcess();

                }
                // 发出下一个时钟开始的信号，通知时钟线程继续执行
                // 请在此处发出时钟开始的信号，唤醒时钟线程继续工作
                SyncManager.inputBlockCondition.signal();
            } catch (InterruptedException e) {
                // 捕获异常，如果线程被中断，则打印异常信息
                // 请在此处处理线程中断异常
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } finally {
                // 释放锁，允许其他线程访问共享资源
                SyncManager.lock.unlock();
            }
        }
    }
}
