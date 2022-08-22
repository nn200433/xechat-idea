package cn.xeblog.plugin.tools.coder;

import cn.xeblog.plugin.annotation.DoTool;
import cn.xeblog.plugin.tools.AbstractTool;
import cn.xeblog.plugin.tools.Tools;
import cn.xeblog.plugin.tools.coder.tree.Tree;

import javax.swing.*;
import java.awt.*;

/**
 * @author song_jx
 * @date 2022-08-10 010 08:26:19
 */
@DoTool(Tools.TREE)
public class Coder extends AbstractTool {

    @Override
    protected void init() {
        mainPanel.removeAll();
        mainPanel.setMinimumSize(new Dimension(150, 200));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(new Tree(), BorderLayout.CENTER);
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

}
