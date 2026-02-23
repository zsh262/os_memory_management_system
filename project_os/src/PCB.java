public class PCB extends Job {
    private int pid;      // 进程ID
    private int pc;       // 程序计数器
    private int ir;       // 指令寄存器
    private int state;     // 进程状态
    private int inTimes;   // 进程进入时间
    private int priority;  // 进程优先级
    private int endTimes;   // 进程结束时间
    private int RunTimes;    // 累计运行时间
    private int turnTimes;    // 周转时间
    private int memoryInstructionCount;    // 需要的内存指令数量

    // 就绪队列信息
    public int []RqInfo ;
    // 输入阻塞队列信息
    public int []Bq1Info;
    // 输出阻塞队列信息
    public int []Bq2Info;
    /**
     * 构造方法，根据作业信息创建进程控制块
     * @param job 作业对象，包含作业基本信息
     * @param pid 进程ID
     */
    public PCB(Job job,int pid ){
        super(job.getJobId(), job.getInTime(), job.getInstructionCount(),job.getResA(),job.getResB());
        setInstructions(job.getInstructions());
        this.pid = pid;
        int count = 0;
        for (int i = 0; i < job.getInstructions().size(); i++) {
            if(job.getInstructions().get(i).getState() == 0)
                count++;
        }
        this.memoryInstructionCount = count;
        this.pc = 0 ;
        this.ir = -1;
        this.state = 0 ;
        this.inTimes = ClockInterruptHandlerThread.simulationTime ;
        this.priority = 0;
        this.endTimes = -1 ;
        this.RunTimes = 0 ;
        this.turnTimes = 0 ;
        this.RqInfo = new int[2] ;
        this.Bq1Info = new int[2] ;
        this.Bq2Info = new int[2] ;

    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getState() {
        return this.getInstructions().get(this.pc).getState();
    }

    public int getIr() {
        return ir;
    }

    public void setIr(int ir) {
        this.ir = ir;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getInTimes() {
        return inTimes;
    }

    public void setInTimes(int inTimes) {
        this.inTimes = inTimes;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getEndTimes() {
        return endTimes;
    }

    public void setEndTimes(int endTimes) {
        this.endTimes = endTimes;
    }

    public int getTurnTimes() {
        return turnTimes;
    }

    public void setTurnTimes(int turnTimes) {
        this.turnTimes = turnTimes;
    }

    public int getRunTimes() {
        return RunTimes;
    }

    public void setRunTimes(int runTimes) {
        RunTimes = runTimes;
    }

    @Override
    public int getMemoryInstructionCount() {
        return memoryInstructionCount;
    }

    @Override
    public void setMemoryInstructionCount(int memoryInstructionCount) {
        this.memoryInstructionCount = memoryInstructionCount;
    }

    @Override
    public String toString() {
        return "PCB{" +
                "pid=" + pid +
                '}';
    }
}

