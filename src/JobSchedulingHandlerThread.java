import java.util.ArrayList;
import java.util.Random;

public class JobSchedulingHandlerThread extends Thread {
    public void run() {

        while (true) {

            SyncManager.lock.lock();
            try {
                // 等待时钟中断线程发出的信号，若未收到则线程阻塞
                SyncManager.jrtCondition.await();
                if(ClockInterruptHandlerThread.simulationTime%2==0){
                    // 若为偶数，调用检查临时队列的方法
                    checkTmpQueue();

                }
                // 激活进程调度线程，通知其可以开始工作
                SyncManager.pstCondition.signal();

            } catch (Exception e) {
                // 捕获异常，如果线程被中断，则打印异常信息
                e.printStackTrace();
            } finally {
                // 释放锁，允许其他线程访问共享资源
                // 在此处释放锁
                SyncManager.lock.unlock();
            }
        }
    }
    // 处理 PCB（进程控制块）的 I/O 相关操作
    // 调用 LogAll 类的方法记录 PCB 创建和进入就绪队列的信息
    public static void ALL_IO(PCB pcb){
        LogAll.CreatePCB(pcb);
        LogAll.EnterReadyQueue(pcb);
    }
    // 处理作业的 I/O 相关操作
    // 调用 LogAll 类的方法记录新作业的信息
    public static void ALL_IO(Job job){
        LogAll.logNewJob(job);
    }
    public static void checkTmpQueue(){
        for (int i = 0; i < OSKernel.tmpQueue.size(); i++) {
            if(OSKernel.tmpQueue.get(i).getInTime() <= ClockInterruptHandlerThread.simulationTime){
                ALL_IO(OSKernel.tmpQueue.get(i));
                // 将该作业添加到后备队列
                OSKernel.backupQueue.add(OSKernel.tmpQueue.get(i));
                OSKernel.tmpQueue.remove(i) ;
                i--;
            }
        }

    }
    // 检查后备队列，将满足资源和内存条件的作业转为 PCB 并添加到就绪队列
    public static  void  checkBackQueue(){
        ArrayList<Job> jobs = OSKernel.QueueToArrayList(OSKernel.backupQueue);
        for (int i = 0; i < jobs.size(); i++) {
            if(jobs.get(i).getInTime() <= ClockInterruptHandlerThread. simulationTime){
                Job job = jobs.get(i);
                if(!(job.getResA() <= OSKernel.ResA && job.getResB() <= OSKernel.ResB)){
                    continue;
                }
                // 根据作业信息创建一个新的 PCB
                PCB pcb = new PCB(job,job.getJobId());

                if(Memory.useMemory(pcb)>=0){
                    OSKernel.AddtoReadyQueue(new PCB(job,job.getJobId()));

                    OSKernel.ResA -= job.getResA();
                    OSKernel.ResB -= job.getResB();

                    jobs.remove(i);
                    i--;
                    System.out.println("作业："+job.getJobId() +"到达"+"时间："+ClockInterruptHandlerThread. simulationTime);
                    ALL_IO(pcb);
                }else{
                    System.out.println("内存不足");
                    break ;
                }


            }else{
                break ;
            }
        }
        OSKernel.backupQueue = OSKernel.ArrayListToQueue(jobs);
    }
    //生成实时作业
    public static void Real_time()
    {// 生成新作业任务的方法
        // 生成一个新的作业

        Job job = new Job(++Job.MaxJobNumber,ClockInterruptHandlerThread.simulationTime
                ,20,0,0);
        Random rand = new Random();
        // 创建存储指令的列表
        int IR = 0 ;
        // 为 PCB 生成随机数量的指令
        ArrayList<Instruction> list = new ArrayList<>() ;
        for(int i = 0 ; i < 15 ;i++)
        {
            list.add(new Instruction(IR++,0));
        }
        for(int i = 0; i < 3 ; i++)
            list.add(new Instruction(IR++,1));
        for(int i = 0; i < 2 ; i++)
            list.add(new Instruction(IR++,2));
        for(int i = 0; i < 20 ; i++){
                int index1 = rand.nextInt(list.size());
                int index2 = rand.nextInt(list.size());
                Instruction ins = list.get(index1);
                Instruction ins2 = list.get(index2);
                list.set(index1, ins);
                list.set(index2, ins2);
        }
        OSKernel.tmpQueue.add(job) ;
        job.setInstructions(list);
        LogAll.all_end = false;

    }
    private void handleJobRequest() {
    }
}
