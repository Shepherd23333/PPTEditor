package me.shepherd23333;

import me.shepherd23333.gui.Base;

import javax.swing.*;

/**
 * 程序主入口 <p>
 * 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> <p>
 * 或点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
 */
public class Main {
    public static void main(String[] args) {
        //线程安全
        SwingUtilities.invokeLater(Base::new);
    }
}