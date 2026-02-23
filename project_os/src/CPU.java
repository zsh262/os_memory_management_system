import java.util.HashMap;
import java.util.Map;

public class CPU  {
    private int pc; // 程序计数器
    private int ir; // 指令寄存器
    private int psw; // 程序状态字
    private Map<String, Integer> registerBackup; // 寄存器备份，用于进程切换时保存寄存器状态
    private static CPU instance = null; // 单例模式实例
    public static PCB currentProcess; // 当前正在执行的进程
    public static int instructionPointer; // 当前进程的指令指针

    /**
     * 记录 IO 操作的状态（带参数版本）。
     * @param state IO 状态码
     */
private void ALL_IO(int state){
    LogAll.CPU_Run(state);
}
    /**
     * 停止 IO 操作并记录结束时间。
     */
private void ALL_IO(){
    LogAll.CPU_Stop();
    LogAll.Process_end_time();
}

    // 私有构造函数，防止外部实例化
    private CPU() {
        this.pc = 0;
        this.ir = 0;
        this.psw = 0;
        this.registerBackup = new HashMap<>();
        currentProcess = null;
        instructionPointer = 0;
    }
    // 获取 CPU 单例实例
    public static CPU getInstance() {
        return instance;
    }
    static {
        instance = new CPU();
    }
    //运行当前进程的下一条指令。
    public void runProcess() {
        if(currentProcess != null) {
            if(this.pc == currentProcess.getInstructionCount()){    // 如果程序计数器等于指令总数
                Memory.clearMemory(currentProcess);               // 清除该进程占用的内存
                OSKernel.ResA += currentProcess.getResA();        // 释放资源 A
                OSKernel.ResB += currentProcess.getResB();       // 释放资源 B
                ALL_IO(); // 停止 IO 操作并记录结束时间
                currentProcess = null ;
                ProcessSchedulingHandlerThread.MFQ3() ;
                return ;

            }

            int state = currentProcess.getInstructions().get(this.pc).getState();     // 获取当前指令的状态
            this.pc ++ ;
            currentProcess.setPc(this.pc);
          //  System.out.println( currentProcess);
            System.out.println(currentProcess.getPid()+"-------------");
            ALL_IO(state);
            // 根据指令状态进行不同处理
            switch (state){
                case 0: {
                        break;
                }
                case 1: {
                    OSKernel.inputBlockQueue.add(currentProcess);          // 将当前进程加入输入阻塞队列
                    currentProcess.Bq1Info[0] = OSKernel.inputBlockQueue.size() ;
                    currentProcess.Bq1Info[1] = ClockInterruptHandlerThread.getCurrentTime();
                    currentProcess = null ;
                    ProcessSchedulingHandlerThread.MFQ3() ;
                    break ;
                }
                case 2 :{
                    OSKernel.outputBlockQueue.add(currentProcess);       // 将当前进程加入输出阻塞队列
                    currentProcess.Bq2Info[0] = OSKernel.outputBlockQueue.size() ;
                    currentProcess.Bq2Info[1] = ClockInterruptHandlerThread.getCurrentTime();
                    currentProcess = null ;
                    ProcessSchedulingHandlerThread.MFQ3() ;
                    break ;
                }
            }

        }else{
            System.out.println("空闲");
            LogAll.CPU_Free();

        }

    }
    //CPU现场保护，用于进程切换时保存当前进程的状态
    public PCB CPU_PRO() {
        currentProcess.setPc(this.pc);        // 保存当前进程的程序计数器
        this.psw = 0 ;
        this.ir = -1 ;
        this.pc = 0 ;
        PCB pcb = currentProcess;
        currentProcess = null ;
        return pcb ;
    }

    //CPU现场恢复,用于进程切换时恢复进程的状态
    public void CPU_REC(PCB process) {
        currentProcess = process;
        // 恢复程序计数器
        this.pc = currentProcess.getPc();
        // 恢复指令寄存器
        this.ir = currentProcess.getPc()-1 ;
        this.psw = 1 ;
    }

}
