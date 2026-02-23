import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 作业和指令加载工具类
 * 负责从文件中读取作业信息和指令，并进行解析和初始化
 */
public class JobandInstructionLoader {

    /**
     * 从文件中读取指令
     *
     * @param filePath 指令文件路径
     * @return 指令列表
     * @throws IOException 读取文件时发生的异常
     */
    public static List<Instruction> loadInstructions(String filePath) throws IOException {
        List<Instruction> instructions = new ArrayList<>();

        File file = new File(filePath);

        String s = "" ;
        BufferedReader br = new BufferedReader(new FileReader(file));   // 使用缓冲流读取文件
        while((s = br.readLine())!= null){
            if(! s.trim().isEmpty()){
                Instruction instruction = new Instruction(Integer.parseInt(s.split(",")[0].trim()),
                        Integer.parseInt(s.split(",")[1].trim())) ;
                instructions.add(instruction);            // 创建指令对象并添加到列表
            }else{
                break ;
            }
        }
        // 完成文件读取并填充 instructions 列表

        return instructions;
    }

    /**
     * 从作业请求文件中读取作业信息
     *
     * @param filePath 作业请求文件路径
     * @return 作业信息列表
     * @throws IOException 读取文件时发生的异常
     */
    public static List<Job> loadJobs(String filePath) throws IOException {
        // 存储作业的列表
        List<Job> jobs = new ArrayList<>();
        // 完成文件读取并填充 jobs 列表
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s = "" ;
        while((s = br.readLine())!= null){
            if(! s.trim().isEmpty()){
                // 创建作业对象并添加到列表
                Job job = new Job(Integer.parseInt(s.split(",")[0].trim()),
                        Integer.parseInt(s.split(",")[1].trim()),
                        0,Integer.parseInt(s.split(",")[3].trim()),
                        Integer.parseInt(s.split(",")[4].trim())
                        );
                // 更新全局最大作业ID和到达时间
                Job.MaxTime = Math.max(job.getInTime(),Job.MaxTime);
                System.out.println(job.toString());
                jobs.add(job);
            }else{
                break ;
            }
        }
        return jobs ;


    }

    /**
     * 封装的加载作业和指令的方法
     *
     * @param jobsFilePath           作业文件路径
     * @param instructionsFolderPath 指令文件夹路径
     * @return 包含所有作业的列表
     * @throws IOException 读取文件时发生的异常
     */
    public static List<Job> loadAllJobsAndInstructions(String jobsFilePath, String instructionsFolderPath) throws IOException {
        // 加载作业基本信息
        List<Job> jobs = loadJobs(jobsFilePath);
        jobs.forEach((one)->{
            String instructionPath =instructionsFolderPath + one.getJobId() + ".txt" ;    // 加载指令
            try {
                List<Instruction> instructions = loadInstructions(instructionPath);
                // 设置作业的指令列表和相关属性
                one.setInstructions(instructions);
                one.setInstructionCount(instructions.size());
                // 统计内存指令数量
                int count = 0 ;
                for (Instruction instruction : instructions) {
                    if(instruction.getState() == 0){
                        count++;
                    }
                }
                one.setMemoryInstructionCount(count);
                System.out.println("*****************"+one.getMemoryInstructionCount());
                Job.MaxJobNumber++ ;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        return jobs;
        // 返回加载的所有作业列表
    }
}
