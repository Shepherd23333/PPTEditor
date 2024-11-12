package me.shepherd23333;

import me.shepherd23333.file.test;
import me.shepherd23333.gui.Base;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    public static void main(String[] args) {
        try {
            Base b = new Base();
            test.create();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}