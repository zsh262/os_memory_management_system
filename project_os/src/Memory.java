public class Memory {
    public static final Integer BLOCK_NUM =10;
    public static final Integer BLOCK_LENGTH =10 ;
    public static int [][] bitMap =new int [BLOCK_NUM][BLOCK_LENGTH] ;
    public static final Integer BLOCK_SUM = BLOCK_NUM * BLOCK_LENGTH ;
    /**
     * 获取用于图形界面显示的位示图一维数组
     * @return 一维数组，每个元素表示一个内存单元的状态（进程ID或0）
     */
    public static Integer []getGUIBitMap(){
        Integer []guiBitMap = new Integer[BLOCK_SUM];
        int index =0 ;
        for (int i = 0; i < BLOCK_NUM; i++) {
            for (int j = 0; j < BLOCK_LENGTH; j++) {
                guiBitMap[index++] = bitMap[i][j];
            }
        }
        return guiBitMap;
    }
    /**
     * 使用最佳适应算法为进程分配内存
     * @param pcb 进程控制块，包含所需的内存指令数量
     * @return 分配的内存起始位置（单元索引），失败返回-1
     */
    public static int useMemory(PCB pcb){

        int length = pcb.getMemoryInstructionCount() ;
        System.out.println("+++++++++++++++++++++++++++");//从传入的进程中获取指令数量length
        System.out.println(length);
        System.out.println("+++++++++++++++++++++++++++");//从传入的进程中获取指令数量length

        int index = -1 ; //找到的最佳匹配内存块的起始位置（初始化为-1）
        int minContainer = Integer.MAX_VALUE ;  //找到的最佳匹配内存块的大小
        for(int i = 0; i < Memory.BLOCK_SUM - length ; i++){
            int num = 0 ;  //连续空闲内存块的大小
            for(int j = 0; (i+j < Memory.BLOCK_SUM) && Memory.bitMap[(i+j)/BLOCK_LENGTH][(i+j)%BLOCK_LENGTH]== 0  ;j++){
                num++ ;
            }
            if(num>=length){
                if(num < minContainer){
                    index = i  ;
                    minContainer = num ;
                }
            }
            i = i+num ;
        }

        if(index !=-1){
        // 标记分配的内存块
            for(int j = 0; j <length ; j++)
            {
                Memory.bitMap[(index+j)/BLOCK_LENGTH][(index+j)%BLOCK_LENGTH] = pcb.getPid();

            }
            // 将进程添加到全局队列
            OSKernel.allQueue.add(pcb);

            return index ;
        }
        return -1 ;
    }
    /**
     * 回收进程占用的内存，并从全局队列中移除该进程
     * @param pcb 要回收内存的进程控制块
     */
    public static void clearMemory(PCB pcb){
        for(int i = 0; i < OSKernel.allQueue.size(); i++){
            if(OSKernel.allQueue.get(i).getPid() == pcb.getPid()){
                OSKernel.allQueue.remove(i);
                break;
            }
        }

        for(int i = 0 ;i < Memory.BLOCK_NUM;i++)
        {
            for(int j = 0 ;j < Memory.BLOCK_LENGTH;j++)
            {
                if(Memory.bitMap[i][j] == pcb.getPid())
                {
                    Memory.bitMap[i][j] = 0;//回收内存
                }
            }
        }
    }

}
