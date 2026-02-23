import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class LogAll {

    public static StringBuilder  log = new StringBuilder();
    public static StringBuilder end = new StringBuilder();
    public static StringBuilder bb1 = new StringBuilder("BB1:[阻塞队列1，键盘输入:");
    public static StringBuilder bb2 = new StringBuilder("BB2:[阻塞队列2，屏幕显示:");
    public static StringBuilder running = new StringBuilder();
    public static boolean all_end = false ;
    public static StringBuilder job = new StringBuilder();
    public static StringBuilder bb = new StringBuilder();
    public static StringBuilder pp = new StringBuilder();
    public static void EnterReadyQueue(PCB pcb){
        log.append(ClockInterruptHandlerThread.simulationTime).append(":[进入就绪队列:进程ID:")
                .append(pcb.getPid()).append(",待执行的指令数:")
                .append(pcb.getInstructionCount()).append("]\n") ; }
    public static void logNewJob(Job job){
        log.append(ClockInterruptHandlerThread.simulationTime)
                .append(":[新增作业:作业ID").append(job.getJobId()).append(",请求时间").append(job.getInTime())
                .append(",指令数量").append(job.getInstructionCount()).append("]\n");
        LogAll.job.append(ClockInterruptHandlerThread.simulationTime)
                .append(":[新增作业:作业ID").append(job.getJobId()).append(",请求时间").append(job.getInTime())
                .append(",指令数量").append(job.getInstructionCount()).append("]\n");
    }
    public static void CreatePCB(PCB pcb){
        int index =  MMU.mmu(pcb) ;
        log.append(ClockInterruptHandlerThread.simulationTime)
                .append(":[创建进程:进程ID:").append(pcb.getPid()).append(",PCB内存始地址:").append(index)
                .append(",分配内存大小:").append(pcb.getMemoryInstructionCount()*100).append("]\n");
    }
    public static void CPU_Run(int state){
        StringBuilder builder = new StringBuilder().append(ClockInterruptHandlerThread.simulationTime)
                .append(":[运行进程:进程ID:").append(CPU.currentProcess.getPid())
                .append(",指令ID:").append(CPU.currentProcess.getPc()).append(",指令类型ID:").append(state)
                .append(",物理地址:").append(MMU.mmu(CPU.currentProcess)).append(",数据大小:")
                .append(CPU.currentProcess.getMemoryInstructionCount() * 100).append("]\n");
        log.append(builder.toString());
        running.append(builder.toString());


    }
    public static void CPU_Stop(){
        log.append(ClockInterruptHandlerThread.simulationTime).append(":[终止进程")
                .append(CPU.currentProcess.getPid()).append("]\n") ;
    }

    public static void CPU_Free(){
        log.append(ClockInterruptHandlerThread.simulationTime) .append(":[CPU空闲").append("]\n");

    }
    public static void InputBlock(){
        StringBuilder sb = new StringBuilder();
        Queue<PCB> pcbQueue = new LinkedList<>() ;
        while(!OSKernel.inputBlockQueue.isEmpty()){
            sb.append(OSKernel.inputBlockQueue.peek().getPid());
            pcbQueue.add(OSKernel.inputBlockQueue.poll());
        }
        OSKernel.inputBlockQueue = pcbQueue ;
        if(!OSKernel.inputBlockQueue.isEmpty()){
            log.append(ClockInterruptHandlerThread.simulationTime).append(":[阻塞进程:阻塞队列ID:").append(1)
                    .append(",").append("进程ID列表:").append(sb).append("]\n");
            bb.append(ClockInterruptHandlerThread.simulationTime).append(":[阻塞进程:阻塞队列ID:").append(1)
                    .append(",").append("进程ID列表:").append(sb).append("]\n");
        }
    }
    public static void InputBlockToReady(ArrayList<PCB> pcb){
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder() ;
        for(PCB pc : pcb){
            sb.append(pc.getPid()).append("/");
            sb1.append(pc.getInstructionCount() - pc.getPc()).append("/");
        }
        log.append(ClockInterruptHandlerThread.simulationTime).append(":[重新进入就绪队列:进程ID列表:")
                .append(sb).append(",指令数:").append(sb1).append("]\n");
    }
    public static void OutputBlock(){
        StringBuilder str_bu = new StringBuilder();
        Queue<PCB> pcbQueue = new LinkedList<>() ;
        while(!OSKernel.outputBlockQueue.isEmpty()){
            str_bu.append(OSKernel.outputBlockQueue.peek().getPid());
            pcbQueue.add(OSKernel.outputBlockQueue.poll());
        }
        OSKernel.outputBlockQueue = pcbQueue ;
        if(!OSKernel.outputBlockQueue.isEmpty()){
            log.append(ClockInterruptHandlerThread.simulationTime).append(":[阻塞进程:阻塞队列ID:").append(2)
                    .append(",").append("进程ID列表:").append(str_bu).append("]\n");
            bb.append(ClockInterruptHandlerThread.simulationTime).append(":[阻塞进程:阻塞队列ID:").append(2)
                    .append(",").append("进程ID列表:").append(str_bu).append("]\n");
        }
    }
    public static void OutputBlockToReady(ArrayList<PCB> pcb){
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder() ;
        for(PCB pc : pcb){
            sb.append(pc.getPid()).append("/");
            sb1.append(pc.getInstructionCount() - pc.getPc()).append("/");
        }
        log.append(ClockInterruptHandlerThread.simulationTime).append(":[重新进入就绪队列:进程ID列表:")
                .append(sb).append(",指令数:").append(sb1).append("]\n");
    }


    public static void Process_end_time(){
        end.append("结束时间:[进程ID").append(CPU.currentProcess.getPid()).
                append(":").append("作业请求时间").append(CPU.currentProcess.getInTime()).append("+").append("进入时间").append(CPU.currentProcess.getInTimes())
                .append("+") .append("总运行时间").append(ClockInterruptHandlerThread.simulationTime - CPU.currentProcess.getInTimes())
                .append("]\n") ;
    }
}
