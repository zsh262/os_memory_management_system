public class MMU {
    /**
     * 内存管理单元（Memory Management Unit）
     * 负责将进程ID映射到物理内存地址
     */
    /**
     * 根据进程ID查找其在内存中的物理起始地址
     * @param pcb 进程控制块，包含进程ID
     * @return 进程在内存中的物理起始地址（单位：字节）
     */
    public static int mmu(PCB pcb){
        for(int i = 0 ;i < Memory.BLOCK_NUM;i++)
        {
            for(int j = 0 ;j < Memory.BLOCK_LENGTH;j++)
            {
                if(Memory.bitMap[i][j] == pcb.getPid())
                {

                    return  i * Memory.BLOCK_LENGTH * 100 + j * 100 ;

                }
            }
        }
        return 0;
    }
}
