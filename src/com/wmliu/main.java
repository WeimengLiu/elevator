package com.wmliu;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 电梯系统的主入口类
 */
public class Main {
	// 窗口默认尺寸
	private static final int DEFAULT_WIDTH = 944;
	private static final int DEFAULT_HEIGHT = 573;
	
	public static void main(String[] args) {
		Manager elevatorManager = new Manager();
		
		// 添加窗口关闭事件监听
		elevatorManager.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		// 设置窗口尺寸和可见性
		elevatorManager.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		elevatorManager.setVisible(true);
	}
}
