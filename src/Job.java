import java.util.ArrayList;
import java.util.List;

public class Job {
    private int jobId;               // 作业的唯一标识符
    private int inTime;              // 作业进入系统的时间
    private int instructionCount;    // 作业包含的指令数量
    private int memoryInstructionCount;
    private int ResA;
    private int ResB;
    private List<Instruction> instructions; // 与作业关联的指令列表
    public static int MaxJobNumber = 0 ;
    public static int MaxTime =0 ;
    public Job(int jobId, int inTime, int instructionCount,int ResA, int ResB) {
        this.jobId = jobId;
        this.inTime = inTime;
        this.instructionCount = instructionCount;
        instructions = new ArrayList<Instruction>();
            this.ResA = ResA;
            this.ResB = ResB;
    }

    public int getJobId() {
        return jobId;
    }

    public int getInTime() {
        return inTime;
    }

    public int getInstructionCount() {
        return instructionCount;
    }

    public void decInstructionCount() {
        this.instructionCount--;
    }
    public void setInstructionCount(int instructionCount) {
        this.instructionCount = instructionCount;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        return jobId + "," + inTime + "," + instructionCount;
    }


    public int getResA() {
        return ResA;
    }

    public void setResA(int resA) {
        ResA = resA;
    }

    public int getResB() {
        return ResB;
    }

    public void setResB(int resB) {
        ResB = resB;
    }

    public int getMemoryInstructionCount() {
        return memoryInstructionCount;
    }

    public void setMemoryInstructionCount(int memoryInstructionCount) {
        this.memoryInstructionCount = memoryInstructionCount;
    }
}