package cn.xeblog.plugin.tools.tree;

import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.util.TreeUtils;
import cn.xeblog.plugin.annotation.DoTool;
import cn.xeblog.plugin.tools.AbstractTool;
import cn.xeblog.plugin.tools.Tools;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * 结构树
 *
 * @author nn200433
 * @date 2022-07-25 025 18:26:39
 */
@DoTool(Tools.TREE)
public class Tree extends AbstractTool {

    @Override
    protected void init() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        mainPanel.setMinimumSize(new Dimension(150, 200));
        mainPanel.setLayout(new BorderLayout());

        JTextArea textArea = buildTextArea(mainPanel);
        mainPanel.add(buildPathPanel(textArea), BorderLayout.NORTH);
        mainPanel.add(getExitButton(), BorderLayout.SOUTH);

        mainPanel.updateUI();
    }


    @Override
    protected JButton getExitButton() {
        JButton exitButton = new JButton("退出");
        exitButton.addActionListener(e -> {
            over();
        });
        return exitButton;
    }

    /**
     * 构建路径面板
     *
     * @param textArea 文本区域
     * @return {@link JPanel }
     * @author nn200433
     */
    private JPanel buildPathPanel(JTextArea textArea) {
        JPanel pathPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("路径：");
        JTextField pathStr = new JTextField();
        pathStr.setEnabled(Boolean.FALSE);
        JButton chooseBtn = new JButton("选择...");
        chooseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            final String path = pathStr.getText();
            if (StrUtil.isNotBlank(path)) {
                fileChooser.setCurrentDirectory(new File(path));
            }
            int option = fileChooser.showOpenDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                pathStr.setText(file.getAbsolutePath());
                textArea.setText(TreeUtils.getTreeDir(file));
            }
        });
        JButton copyBtn = new JButton("复制");
        copyBtn.addActionListener(e -> {
            ClipboardUtil.setStr(textArea.getText());
        });
        pathPanel.add(label, BorderLayout.WEST);
        pathPanel.add(pathStr, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 5));
        buttonPanel.add(chooseBtn);
        buttonPanel.add(copyBtn);
        pathPanel.add(buttonPanel, BorderLayout.EAST);
        return pathPanel;
    }

    /**
     * 构建文本域
     *
     * @return {@link JTextArea }
     * @author nn200433
     */
    private JTextArea buildTextArea(JPanel mainPanel) {
        JTextArea textArea = new JTextArea();
        // 设置自动换行
        textArea.setLineWrap(true);
        // 激活断行不断字功能
        textArea.setWrapStyleWord(true);
        // 定义带滚动条的panel，并将JTextArea存入到panel中，使textarea具有滚动条显示功能。
        JScrollPane scrollPane = new JScrollPane(textArea);
        // 取消显示水平滚动条
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // 显示垂直滚动条
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        // 添加到中间
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return textArea;
    }

}
