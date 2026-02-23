import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class OSKernel {
    public static ArrayList<Job> tmpQueue = new ArrayList<>();
    // 后备队列，用于存放备份的作业
    public static Queue<Job> backupQueue = new LinkedList<>();
    // 就绪队列，用于存放已准备好执行的进程
   // public static Queue<PCB> readyQueue = new LinkedList<>();
    public static Integer ResA = 2 ;
    public static Integer ResB = 1 ;
    // 就绪队列，用于存放已准备好执行的进程
    public static Queue<PCB> readyQueue1 = new LinkedList<>();
    public static Queue<PCB> readyQueue2 = new LinkedList<>();
    public static Queue<PCB> readyQueue3 = new LinkedList<>();
    // 输入阻塞队列，用于存放因输入操作而阻塞的进程
    public static Queue<PCB> inputBlockQueue = new LinkedList<>();
    // 输出阻塞队列，用于存放因输出操作而阻塞的进程
    public static Queue<PCB> outputBlockQueue = new LinkedList<>();
    // 所有进程控制块的集合，用于存储所有进程信息
    public static ArrayList<PCB> allQueue = new ArrayList<>();
    /**
     * 将进程控制块添加到多级就绪队列中
     * 会根据进程的优先级调整其优先级，并将其放入相应的就绪队列
     * @param pcb 要添加到就绪队列的进程控制块
     */
    public static void AddtoReadyQueue(PCB pcb) {
        // 获取进程的优先级
        int priority = pcb.getPriority();
        priority = priority >=3 ? 3 : priority+1   ;
        pcb.setPriority(priority);
        switch (priority) {
            case 1 : readyQueue1.add(pcb) ;
            break;
            case 2 : readyQueue2.add(pcb) ;
            break;
            case 3 : readyQueue3.add(pcb) ;
            break;
        }
        pcb.setPriority(priority);
    }
    /**
     * 从多级就绪队列中取出一个进程控制块
     * 按照优先级从高到低的顺序尝试从各级就绪队列中取出进程
     * @return 取出的进程控制块，如果所有就绪队列为空则返回 null
     */
    public static PCB OutfromReadyQueue() {

        if(!readyQueue1.isEmpty()){
            return readyQueue1.poll();
            // 若第一级队列为空，尝试从第二级就绪队列中取出进程
        }else if(!readyQueue2.isEmpty()){
            return readyQueue2.poll();
        }else if(!readyQueue3.isEmpty()){
            return readyQueue3.poll();
        }else{
            return null;
        }
    }
    /**
     * 将队列中的作业转换为 ArrayList
     * 会清空原队列
     * @param queue 要转换的作业队列
     * @return 转换后的作业 ArrayList
     */
    public static ArrayList<Job> QueueToArrayList(Queue<Job> queue) {
        ArrayList<Job> list = new ArrayList<>();
        // 循环将队列中的作业取出并添加到 ArrayList 中，直到队列为空
        while(!queue.isEmpty()){
            list.add(queue.poll());
        }
        return list;
    }
    /**
     * 将 ArrayList 中的作业转换为队列
     * @param queue 要转换的作业 ArrayList
     * @return 转换后的作业队列
     */
    public static Queue<Job> ArrayListToQueue(ArrayList<Job> queue) {
        return new LinkedList<>(queue);
    }
}
