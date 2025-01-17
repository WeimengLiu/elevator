package com.wmliu.constants;

/**
 * 电梯系统常量定义
 */
public class ElevatorConstants {
    // 电梯运行状态
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = -1;
    public static final int DIRECTION_STILL = 0;
    
    // 电梯配置
    public static final int MAX_FLOOR = 20;
    public static final int ELEVATOR_COUNT = 5;
    
    // UI相关常量
    public static final int BUTTON_DELAY = 1000;
    public static final int DOOR_DELAY = 2000;
    
    private ElevatorConstants() {
        // 防止实例化
    }
} 