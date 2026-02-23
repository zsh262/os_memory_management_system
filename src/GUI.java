import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 操作系统模拟器主界面
 */
public class GUI extends JFrame {

    // 单例模式实例

    // 系统线程

    // 界面组件
    private JTextArea jobQueueTextArea;
    private JTextArea readyQueueTextArea;
    public  JTextArea runningProcessTextArea;
    private JTextArea blockedQueueTextArea;
    private MemoryVisualizer memoryVisualizer;
    private JTextArea memoryTextArea;
    public  JTextArea clockTextArea; // 时钟显示区域

    // 控制按钮
    private JButton executeButton; // 执行按钮
    private JButton pauseButton;   // 暂停按钮
    private JButton saveButton;    // 保存按钮
    private JButton realTimeButton; // 实时按钮

    // 随机数生成器
    private Random random = new Random();

    // 作业ID计数器
    private int jobIdCounter = 1;
    private DefaultTableModel model;
    private JTable table;
    /**
     * 构造函数
     */
    public GUI() {
        // 设置窗口标题和大小
        super("作业管理及内存管理系统");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 在这里添加自定义的关闭逻辑
                try {
                    saveRecords();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(0);                    }
        });


        // 设置全局字体，解决中文显示问题
        Font globalFont = new Font("Microsoft YaHei", Font.PLAIN, 12);
        UIManager.put("Button.font", globalFont);
        UIManager.put("Label.font", globalFont);
        UIManager.put("TextField.font", globalFont);
        UIManager.put("TextArea.font", globalFont);
        UIManager.put("ComboBox.font", globalFont);
        UIManager.put("TabbedPane.font", globalFont);
        UIManager.put("List.font", globalFont);
        UIManager.put("Table.font", globalFont);
        UIManager.put("TableHeader.font", globalFont);
        UIManager.put("Menu.font", globalFont);
        UIManager.put("MenuItem.font", globalFont);

        // 初始化界面组件
        initComponents();

        // 初始化系统线程
        // initThreads();

        // 显示窗口
        setVisible(true);

    }

    /**
     * 初始化界面组件
     */
    private void initComponents() {
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 创建控制面板
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // 创建信息显示面板
        JPanel infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // 设置主面板
        setContentPane(mainPanel);
    }

    /**
     * 创建控制面板
     * @return 控制面板
     */
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();

        // 创建按钮
        executeButton = new JButton("执行");
        pauseButton = new JButton("暂停");
        saveButton = new JButton("保存");
        realTimeButton = new JButton("实时");

        // 设置按钮状态

        // 添加按钮到面板，只添加4个按钮
        controlPanel.add(executeButton);
        controlPanel.add(pauseButton);
        controlPanel.add(saveButton);
        controlPanel.add(realTimeButton);

        // 添加按钮事件监听器
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Main.running = true ;
                    SyncManager.semaphore.release();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(!Main.running) {
                        return ;
                    }
                    Main.running = false ;
                    SyncManager.semaphore.acquire();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    saveRecords();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        realTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JobSchedulingHandlerThread.Real_time();
                JOptionPane.showMessageDialog(null,"实时作业创建成功","提示",JOptionPane.INFORMATION_MESSAGE);

            }
        });

        return controlPanel;
    }

    /**
     * 创建信息显示面板
     * @return 信息显示面板
     */
    private JPanel createInfoPanel() {
        // 创建统一字体
        Font textFont = new Font("Microsoft YaHei", Font.PLAIN, 12);

        // 创建主面板，使用BorderLayout
        JPanel infoPanel = new JPanel(new BorderLayout());

        // 创建中央内容面板，使用GridBagLayout进行精确定位
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 1. 时钟显示区：位于左上角
        JPanel clockPanel = new JPanel(new BorderLayout());
        clockPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"时钟显示区"));
        clockTextArea = new JTextArea();
        clockTextArea.setEditable(false);
        clockTextArea.setFont(textFont);
        clockTextArea.setText("系统第 0s");
        JScrollPane clockScrollPane = new JScrollPane(clockTextArea);
        clockPanel.add(clockScrollPane, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.35;
        gbc.weighty = 0.2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        contentPanel.add(clockPanel, gbc);

        // 2. 作业请求区：位于左中
        JPanel jobRequestPanel = new JPanel(new BorderLayout());
        jobRequestPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"作业请求区"));
        jobQueueTextArea = new JTextArea();
        jobQueueTextArea.setEditable(false);
        jobQueueTextArea.setFont(textFont);
        JScrollPane jobQueueScrollPane = new JScrollPane(jobQueueTextArea);
        jobRequestPanel.add(jobQueueScrollPane, BorderLayout.CENTER);
        DefaultCaret caret1 = (DefaultCaret)jobQueueTextArea.getCaret();
        caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.35;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(jobRequestPanel, gbc);

        // 3. 内存区：位于中部
        JPanel memoryPanel = new JPanel(new BorderLayout());
        memoryPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"内存区"));

        // 创建内存可视化面板
        JPanel memoryVisualizerPanel = MemoryVisualizer.createMemoryVisualizerPanel();
        memoryPanel.add(memoryVisualizerPanel, BorderLayout.CENTER);

        // 保留文本显示区域，但放在底部
        memoryTextArea = new JTextArea(4, 20);
        memoryTextArea.setEditable(false);
        memoryTextArea.setFont(textFont);
        String [] header = new String[]{"作业ID","起始地址","终止地址","ResA","ResB"};
        model = new DefaultTableModel(new String[][]{},header);
        table = new JTable(model);

        table.getTableHeader().setFont(new Font("宋体", Font.PLAIN, 10));
        table.getTableHeader().setFont(new Font("宋体", Font.PLAIN, 10));
        table.setFont(new Font("宋体", Font.PLAIN, 10));
        table.setPreferredScrollableViewportSize(new Dimension(200,80));
        JScrollPane memoryScrollPane = new JScrollPane(table);
        memoryPanel.add(memoryScrollPane, BorderLayout.SOUTH);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 3;
        gbc.weightx = 0.15;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(memoryPanel, gbc);

        // 4. 进程就绪区：位于右中上
        JPanel readyProcessPanel = new JPanel(new BorderLayout());
        readyProcessPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"进程就绪区"));
        readyQueueTextArea = new JTextArea();
        readyQueueTextArea.setEditable(false);
        readyQueueTextArea.setFont(textFont);
        JScrollPane readyQueueScrollPane = new JScrollPane(readyQueueTextArea);
        readyProcessPanel.add(readyQueueScrollPane, BorderLayout.CENTER);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.weightx = 0.5;
        gbc.weighty = 0.6;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(readyProcessPanel, gbc);

        // 5. 进程运行区：位于右下角
        JPanel runningProcessPanel = new JPanel(new BorderLayout());
        runningProcessPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"进程运行区"));
        runningProcessTextArea = new JTextArea();
        runningProcessTextArea.setEditable(false);
        runningProcessTextArea.setFont(textFont);
        JScrollPane runningProcessScrollPane = new JScrollPane(runningProcessTextArea);
        runningProcessPanel.add(runningProcessScrollPane, BorderLayout.CENTER);
        DefaultCaret caret = (DefaultCaret) runningProcessTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.4;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(runningProcessPanel, gbc);

        // 6. 进程阻塞区：位于左下
        JPanel blockProcessPanel = new JPanel(new BorderLayout());
        blockProcessPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"进程阻塞区"));
        blockedQueueTextArea = new JTextArea();
        blockedQueueTextArea.setEditable(false);
        blockedQueueTextArea.setFont(textFont);
        JScrollPane blockedQueueScrollPane = new JScrollPane(blockedQueueTextArea);
        DefaultCaret caret3 = (DefaultCaret) blockedQueueTextArea.getCaret();
        caret3.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        blockProcessPanel.add(blockedQueueScrollPane, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.35;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(blockProcessPanel, gbc);

        // 将内容面板添加到主面板
        infoPanel.add(contentPanel, BorderLayout.CENTER);

        // 创建底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(executeButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(realTimeButton);

        // 添加底部按钮面板到主面板
        infoPanel.add(buttonPanel, BorderLayout.SOUTH);

        return infoPanel;
    }

    /**
     * 初始化系统线程
     */

    /**
     * 暂停系统
     */
    private void pauseSystem() {

    }

    /**
     * 停止系统
     */
    private void stopSystem() {

    }


    /**
     * 保存记录
     */
    public static  void saveRecords() throws IOException {
        String builder = "./output/ProcessResults-" +
               ( ClockInterruptHandlerThread.simulationTime-1) + "-DJFK.txt";

        File file = new File(builder);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file))  ;

        writer.write(LogAll.pp.toString());
        JOptionPane.showMessageDialog(null,"文件保存在" + builder,"提示",JOptionPane.INFORMATION_MESSAGE);

        writer.close();
    }

    /**
     * 更新后备队列显示
     */
    public void updateJobQueueDisplay() {


        jobQueueTextArea.setText(LogAll.job.toString());
    }

    /**
     * 更新就绪队列显示
     */
    private void updateReadyQueueDisplay() {
        StringBuilder sb = new StringBuilder();

        sb.append("第一级队列：\n");
        for (PCB process : OSKernel.readyQueue1) {
            sb.append(ClockInterruptHandlerThread.simulationTime).append(":").append("进程ID：").append(process.getPid())
                    .append("，作业ID：").append(process.getJobId())
                    .append("，指令ID：").append(process.getInstructions().get(process.getPc() >=
                            process.getInstructions().size() ? process.getPc()-1:process.getPc()).getId())
                    .append("，时间片：").append(1)
                    .append("\n");

        }

        sb.append("\n第二级队列：\n");
        for (PCB process : OSKernel.readyQueue2) {
            sb.append(ClockInterruptHandlerThread.simulationTime).append(":").append("进程ID：").append(process.getPid())
                    .append("，作业ID：").append(process.getJobId())
                    .append("，指令ID：").append(process.getInstructions().get(process.getPc() >=
                            process.getInstructions().size() ? process.getPc()-1:process.getPc()).getId())
                    .append("，时间片：").append(2)
                    .append("\n");
        }

        sb.append("\n第三级队列：\n");
        for (PCB process : OSKernel.readyQueue3) {
            sb.append(ClockInterruptHandlerThread.simulationTime).append(":").append("进程ID：").append(process.getPid())
                    .append("，作业ID：").append(process.getJobId())
                    .append("，指令ID：").append(process.getInstructions().get(process.getPc() >=
                            process.getInstructions().size() ? process.getPc()-1:process.getPc()).getId())
                    .append("，时间片：").append(4)
                    .append("\n");
        }

        readyQueueTextArea.setText(sb.toString());

    }

    /**
     * 更新运行进程显示
     */
    private void updateRunningProcessDisplay() {
        runningProcessTextArea.setText(LogAll.running.toString());

    }

    /**
     * 更新阻塞队列显示
     */
    private void updateBlockedQueueDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("阻塞队列1中的进程数量：").append(OSKernel.inputBlockQueue.size()).append("\n\n");

        for (PCB process : OSKernel.inputBlockQueue) {
            sb.append("进程ID：").append(process.getPid())
                    .append("，作业ID：").append(process.getJobId())
                    //.append("，优先级：").append(process.getPriority())
                    .append("\n");
        }
        sb.append("阻塞队列2中的进程数量：").append(OSKernel.outputBlockQueue.size()).append("\n\n");

        for (PCB process : OSKernel.outputBlockQueue) {
            sb.append("进程ID：").append(process.getPid())
                    .append("，作业ID：").append(process.getJobId())
                    //.append("，优先级：").append(process.getPriority())
                    .append("\n");
        }

        blockedQueueTextArea.setText(LogAll.bb.toString());
    }

    /**
     * 更新内存使用情况显示
     */
    public void updateMemoryDisplay() {
        // 更新文本区域
        //.setText("资源分配"+"  A:" +OSKernel.ResA + "  B:" + OSKernel.ResB);

        while(model.getRowCount()>0){
            model.removeRow(0);
        }
        for(PCB process:OSKernel.allQueue){
            model.addRow(new String[]{process.getPid()+"",MMU.mmu(process)+"",
                    MMU.mmu(process) + process.getMemoryInstructionCount()*100+"",
                    process.getResA() +"" , process.getResB() +""
            });
        }
        // 获取当前可视化组件并更新
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                searchAndUpdateMemoryVisualizer((JPanel) comp);
            }
        }
    }

    /**
     * 递归搜索并更新内存可视化组件
     * @param panel 要搜索的面板
     */
    private void searchAndUpdateMemoryVisualizer(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof MemoryVisualizer) {
                // 更新为新的方法名
                ((MemoryVisualizer) comp).updateMemoryInfo();
                return;
            } else if (comp instanceof JPanel) {
                searchAndUpdateMemoryVisualizer((JPanel) comp);
            } else if (comp instanceof JScrollPane) {
                Component viewComp = ((JScrollPane) comp).getViewport().getView();
                if (viewComp instanceof MemoryVisualizer) {
                    // 更新为新的方法名
                    ((MemoryVisualizer) viewComp).updateMemoryInfo();
                    return;
                } else if (viewComp instanceof JPanel) {
                    searchAndUpdateMemoryVisualizer((JPanel) viewComp);
                }
            }
        }
    }

    /**
     * 更新进程显示
     * 该方法会更新所有与进程相关的显示区域
     */
    public void updateProcessDisplay() {

       // updateClockDisplay(ClockInterruptHandlerThread.getCurrentTime());
        updateJobQueueDisplay();
        // 更新就绪队列显示
        updateReadyQueueDisplay();

        // 更新运行进程显示
        updateRunningProcessDisplay();

        // 更新阻塞队列显示
        updateBlockedQueueDisplay();

        // 更新内存显示
        updateMemoryDisplay();
    }

    /**
     * 更新时钟显示
     * @param time 当前模拟时间
     */

}

