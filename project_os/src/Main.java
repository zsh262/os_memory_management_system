import java.io.IOException;
import java.util.List;

public class Main{
    public static GUI gui = new GUI();
    public static Boolean running =false ;


    public static void main(String[] args) throws InterruptedException {

        String jobsFilePath = "input2/jobs-input.txt";
        String instructionsFolderPath = "input2/";

        try {
            // 调用加载作业和指令的封装方法，并获取返回的作业列表
            List<Job> jobs = JobandInstructionLoader.loadAllJobsAndInstructions(jobsFilePath, instructionsFolderPath);
            OSKernel.tmpQueue.addAll(jobs);
            System.out.println("\n所有作业加载完成：");
            for (Job job : jobs) {
                System.out.println("作业ID: " + job.getJobId() + ", 到达时间: " + job.getInTime() +
                        ", 指令数量: " + job.getInstructionCount() +"------" + job.getMemoryInstructionCount());

            }

        } catch (IOException e) {
            System.err.println("加载作业和指令时出错: " + e.getMessage());
            e.printStackTrace();
        }

        SyncManager.semaphore.acquire();
        running =false;
        JobSchedulingHandlerThread jobSchedulingHandlerThread = new JobSchedulingHandlerThread();
        jobSchedulingHandlerThread.start();

        Thread processSchedulingHandlerThread = new ProcessSchedulingHandlerThread();
        processSchedulingHandlerThread.start();

        // 确保时钟中断线程和作业请求线程启动后再启动进程调度线程
        try {
            Thread.sleep(100); // 确保前两个线程有时间启动
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 启动时钟中断线程
        Thread clockInterruptHandlerThread = new ClockInterruptHandlerThread();
        clockInterruptHandlerThread.start();


        InputBlockThread inputBlockThread = new InputBlockThread();
        inputBlockThread.start();

        OutputBlockThread outputBlockThread = new OutputBlockThread();
        outputBlockThread.start();

    }
}
