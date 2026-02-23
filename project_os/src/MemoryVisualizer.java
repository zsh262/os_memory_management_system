import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 内存可视化组件
 * 使用位示图显示内存分配情况
 */
public class MemoryVisualizer extends JPanel {

    // 物理块数量
    private static final int PHYSICAL_BLOCK_COUNT =  Memory.BLOCK_NUM;

    private static final int LEAVE =  80;
    // 基础存储单元大小
    private static final int BASIC_UNIT_SIZE = 100;

    // 每个物理块包含的基础存储单元数量
    private static final int UNITS_PER_BLOCK = Memory.BLOCK_LENGTH ;

    // 总基础存储单元数量
    private static final int TOTAL_UNITS = Memory.BLOCK_LENGTH * Memory.BLOCK_NUM ;
    private static JLabel resLabel;

    // 位示图
    private Integer[] bitMap;

    // 进程内存分配记录
    private List<Object> processAllocations; // 使用Object类型，因为ProcessMemoryAllocation是MemoryManager的内部类

    // 单元格的宽度和高度（像素）
    private static final int CELL_WIDTH = 40;
    private static final int CELL_HEIGHT = 40;

    // 物理块显示的行数
    private static final int ROWS = 10;

    // 每行显示的物理块数量
    private static final int BLOCKS_PER_ROW = PHYSICAL_BLOCK_COUNT / ROWS;

    /**
     * 构造函数
     */
    public MemoryVisualizer() {
        setPreferredSize(new Dimension(
                BLOCKS_PER_ROW * UNITS_PER_BLOCK * CELL_WIDTH,
                ROWS * (CELL_HEIGHT * 2 + 10)
        ));
        setBackground(Color.WHITE);
        updateMemoryInfo();
    }

    /**
     * 更新内存信息
     */
    @SuppressWarnings("unchecked")
    public void updateMemoryInfo() {
        this.bitMap = Memory.getGUIBitMap();


        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (bitMap == null) {
            g2d.setColor(Color.RED);
            g2d.drawString("位示图数据不可用", 10, 20);
            return;
        }

        // 绘制位示图
        drawBitMap(g2d);

        // 绘制物理块边界
        drawPhysicalBlockBoundaries(g2d);

        // 绘制进程分配信息
        //   drawProcessAllocationInfo(g2d);
    }

    /**
     * 绘制位示图
     * @param g2d Graphics2D对象
     */
    private void drawBitMap(Graphics2D g2d) {
        for (int i = 0; i < PHYSICAL_BLOCK_COUNT; i++) {//10
            int rowIndex = i / BLOCKS_PER_ROW;
            int colIndex = i % BLOCKS_PER_ROW;//10

            int blockStartX = colIndex * UNITS_PER_BLOCK * CELL_WIDTH + LEAVE;
            int blockStartY = rowIndex * (CELL_HEIGHT  ) ;

            // 绘制物理块标题
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
            // g2d.drawString("物理块 " + i, blockStartX, blockStartY + 15);

            // 绘制每个基础存储单元
            for (int j = 0; j < UNITS_PER_BLOCK; j++) {
                int unitIndex = i * UNITS_PER_BLOCK + j;

                if (unitIndex < TOTAL_UNITS) {
                    int cellX = blockStartX + j * CELL_WIDTH;
                    int cellY = blockStartY + 20;

                    // 绘制单元格
                    if (bitMap[unitIndex] > 0 ) {
                        g2d.setColor(new Color(255, 100, 100)); // 已分配用红色
                    } else {
                        g2d.setColor(new Color(200, 255, 200)); // 空闲用绿色
                    }

                    g2d.fillRect(cellX, cellY, CELL_WIDTH - 1, CELL_HEIGHT - 1);

                    // 绘制单元格边框
                    g2d.setColor(Color.GRAY);
                    g2d.drawRect(cellX, cellY, CELL_WIDTH - 1, CELL_HEIGHT - 1);

                    // 绘制位示图值
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
                    g2d.drawString(bitMap[unitIndex] + "" ,
                            cellX + CELL_WIDTH / 2 - 4,
                            cellY + CELL_HEIGHT / 2 + 5);

                    // 绘制单元索引
                    g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 9));
                    g2d.drawString(String.valueOf(unitIndex),
                            cellX + 2,
                            cellY + CELL_HEIGHT - 2);
                }
            }
        }
    }

    /**
     * 绘制物理块边界
     * @param g2d Graphics2D对象
     */
    private void drawPhysicalBlockBoundaries(Graphics2D g2d) {
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(2.0f));

        for (int i = 0; i < PHYSICAL_BLOCK_COUNT; i++) {
            int rowIndex = i / BLOCKS_PER_ROW;
            int colIndex = i % BLOCKS_PER_ROW;

            int blockStartX = colIndex * UNITS_PER_BLOCK * CELL_WIDTH+LEAVE;
            int blockStartY = rowIndex * (CELL_HEIGHT )+20;

            g2d.drawRect(blockStartX, blockStartY,
                    UNITS_PER_BLOCK * CELL_WIDTH - 1, CELL_HEIGHT - 1);
        }

        // 重置线条样式
        g2d.setStroke(new BasicStroke(1.0f));
    }

    public static JPanel createMemoryVisualizerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建内存可视化组件
        MemoryVisualizer memoryVisualizer = new MemoryVisualizer();

        // 创建图例面板
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 已分配内存图例
        JPanel allocatedLegend = new JPanel();
        allocatedLegend.setBackground(new Color(255, 100, 100));
        allocatedLegend.setPreferredSize(new Dimension(15, 15));
        legendPanel.add(allocatedLegend);
        legendPanel.add(new JLabel("已分配"));

        // 空闲内存图例
        JPanel freeLegend = new JPanel();
        freeLegend.setBackground(new Color(200, 255, 200));
        freeLegend.setPreferredSize(new Dimension(15, 15));
        legendPanel.add(freeLegend);
        legendPanel.add(new JLabel("空闲"));

        // 添加说明
        legendPanel.add(new JLabel("  |  "));
        resLabel = new JLabel("resA:2,resB:1");

        legendPanel.add(resLabel);
        legendPanel.add(new JLabel("|                                                                    " +
                "                     "));
        // 添加组件到面板
        panel.add(new JScrollPane(memoryVisualizer), BorderLayout.CENTER);
        panel.add(legendPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 获取内存可视化组件实例
     * @return 内存可视化组件实例
     */
    public static MemoryVisualizer getInstance() {
        return new MemoryVisualizer();
    }
}


