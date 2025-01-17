package com.wmliu;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import com.wmliu.constants.ElevatorConstants;

public class Manager extends JFrame implements Runnable{

	private static final long serialVersionUID = 1L;
	private JPanel jContainPanel;
	private JPanel jPanel;
	private final JButton[] upButton = new JButton[ElevatorConstants.MAX_FLOOR];
	private final JButton[] downButton = new JButton[ElevatorConstants.MAX_FLOOR];
	private final Elevator[] elevators = new Elevator[ElevatorConstants.ELEVATOR_COUNT];
	private final boolean[] upState = new boolean[ElevatorConstants.MAX_FLOOR];
	private final boolean[] downState = new boolean[ElevatorConstants.MAX_FLOOR];
	private final Thread thread;
	private JLabel statusLabel;
	private JLabel floorLabel;
	/**
	 * This is the default constructor
	 */
	public Manager() {
		super();
		thread = new Thread(this);
		initialize();
		thread.start();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(1200, 700);
		this.setMinimumSize(new Dimension(1000, 600));
		this.setContentPane(getJContainPanel());
		this.setTitle("电梯调度系统");
		this.setBackground(new Color(240, 240, 240));
		
		// 设置窗口居中显示
		this.setLocationRelativeTo(null);
		
		// 修改标题面板样式
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBackground(new Color(0, 102, 204));
		titlePanel.setBorder(new EmptyBorder(10, 0, 10, 0));
		JLabel titleLabel = new JLabel("电梯调度控制系统", SwingConstants.CENTER);
		titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
		titleLabel.setForeground(Color.WHITE);
		titlePanel.add(titleLabel, BorderLayout.CENTER);
		
		this.add(titlePanel, BorderLayout.NORTH);
		
		for(int i=0;i<ElevatorConstants.MAX_FLOOR;i++)
		{
			upState[i]=false;
			downState[i]=false;
		}
		
	}

	/**
	 * This method initializes jContainPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContainPanel() {
		if (jContainPanel == null) {
			jContainPanel = new JPanel();
			// 修改布局为BorderLayout
			jContainPanel.setLayout(new BorderLayout());
			
			// 创建一个面板来容纳电梯
			JPanel elevatorsPanel = new JPanel();
			elevatorsPanel.setLayout(new GridLayout(1, 6, 10, 0));
			elevatorsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
			elevatorsPanel.setBackground(new Color(240, 240, 240));
			
			// 添加控制面板
			elevatorsPanel.add(getJPanel());
			
			// 添加电梯
			for(int i=0; i<ElevatorConstants.ELEVATOR_COUNT; i++) {
				Elevator ele = new Elevator();
				ele.setBorder(BorderFactory.createTitledBorder(null, 
					"电梯 " + (i+1), 
					TitledBorder.DEFAULT_JUSTIFICATION, 
					TitledBorder.DEFAULT_POSITION, 
					new Font("微软雅黑", Font.BOLD, 14), 
					new Color(0, 102, 204)));
				ele.getthread().start();
				elevatorsPanel.add(ele);
				elevators[i] = ele;
			}
			
			jContainPanel.add(elevatorsPanel, BorderLayout.CENTER);
		}
		return jContainPanel;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setBackground(new Color(245, 245, 245));
			jPanel.setLayout(new GridLayout(22, 2, 5, 5));
			
			// 修改UP/DOWN标签
			statusLabel = new JLabel("上行", SwingConstants.CENTER);
			floorLabel = new JLabel("下行", SwingConstants.CENTER);
			statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
			floorLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
			
			jPanel.add(statusLabel);
			jPanel.add(floorLabel);
			jPanel.setBorder(BorderFactory.createTitledBorder(null, 
				"控制面板", 
				TitledBorder.DEFAULT_JUSTIFICATION, 
				TitledBorder.DEFAULT_POSITION, 
				new Font("微软雅黑", Font.BOLD, 14), 
				new Color(0, 102, 204)));
			
			// 修改按钮样式，移除按钮禁用限制
			for(int i=ElevatorConstants.MAX_FLOOR-1; i>=0; i--) {
				upButton[i] = createStyledButton("上行", Color.DARK_GRAY);
				downButton[i] = createStyledButton("下行", Color.DARK_GRAY);
				upButton[i].addActionListener(new Action());
				downButton[i].addActionListener(new Action());
				jPanel.add(upButton[i]);
				jPanel.add(downButton[i]);
			}
		}
		return jPanel;
	}

	private JButton createStyledButton(String text, Color bgColor) {
		JButton button = new JButton(text);
		button.setBackground(bgColor);
		button.setForeground(Color.WHITE);
		button.setFont(new Font("微软雅黑", Font.BOLD, 12));  // 减小字体
		button.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
			BorderFactory.createEmptyBorder(2, 4, 2, 4)  // 减小内边距
		));
		button.setFocusPainted(false);
		button.setOpaque(true);
		return button;
	}

	class Action implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			for(int i=0;i<ElevatorConstants.MAX_FLOOR;i++){
				if(e.getSource()==upButton[i])
				{
					upState[i]=true;
				}
				else if(e.getSource()==downButton[i])
				{
					downState[i]=true;
				}
			}
		}
	}

	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(100);  // 添加短暂延时，减少CPU占用
				
				for(int i=0; i<ElevatorConstants.MAX_FLOOR; i++) {
					if(upState[i]) {
						ManageUpElevator(i);
					}
					if(downState[i]) {
						ManageDownElevator(i);
					}
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}

	private void ManageDownElevator(int i) {
		// TODO Auto-generated method stub
		int Elevator1=0,Elevator2=0,Elevator=-1;
		int Distance1=20;
		int Distance2=20;
		int Distance=20;
		int Temp;
		for(int j=0;j<ElevatorConstants.ELEVATOR_COUNT;j++)
		{
			if((elevators[j].get_NextDirection()==-1)&&(elevators[j].getDirection()==1))
			{
				if(i<elevators[j].getToFloor())
				{
					Temp=Math.abs(i-elevators[j].getToFloor());
					if(Temp<Distance)
					{
						Elevator=j;
						Distance=Temp;
					}
				}
			}
		}
		if(Distance!=20)
		{
			elevators[Elevator].Set_NextDirectionDown();
			elevators[Elevator].setToFloor(i);
			downState[i]=false;
			return;
		}
		else
		{
		for(int j=0;j<ElevatorConstants.ELEVATOR_COUNT;j++){
			if((elevators[j].getDirection()==-1)&&(i<elevators[j].getCurPosition())&&(elevators[j].get_NextDirection()==-1))
			{
				Temp=Math.abs(i-elevators[j].getCurPosition());
				if(Temp<Distance1)
				{
					Elevator1=j;
					Distance1=Temp;
				}
			}
		}
		for(int j=0;j<ElevatorConstants.ELEVATOR_COUNT;j++){
			if(elevators[j].getDirection()==0)
			{
				Temp=Math.abs(i-elevators[j].getCurPosition());
				if(Temp<Distance2)
				{
					Elevator2=j;
					Distance2=Temp;
				}
			}
		}
		if((Distance1!=20)||(Distance2!=20))
		{
			if(Distance1<=Distance2)
			{
				elevators[Elevator1].Set_NextDirectionDown();
				elevators[Elevator1].setToFloor(i);
				downState[i]=false;
			}	
			else if(Distance2<Distance1)
			{
				elevators[Elevator2].Set_NextDirectionDown();
				elevators[Elevator2].setToFloor(i);
				downState[i]=false;
			}
		}
		}
	}

	private void ManageUpElevator(int i) {
		// TODO Auto-generated method stub
		int Elevator1=0,Elevator2=0,Elevator=0;
		int Distance1=20;
		int Distance2=20;
		int Distance=20;
		int Temp;
		for(int j=0;j<ElevatorConstants.ELEVATOR_COUNT;j++)
		{
			if((elevators[j].get_NextDirection()==1)&&(elevators[j].getDirection()==-1))
			{
				if(i>elevators[j].getToFloor())
				{
					Temp=Math.abs(i-elevators[j].getToFloor());
					if(Temp<Distance)
					{
						Elevator=j;
						Distance=Temp;
					}
				}
			}
		}
		if(Distance!=20)
		{
			elevators[Elevator].Set_NextDirectionUp();
			elevators[Elevator].setToFloor(i);
			upState[i]=false;
			return;
		}
		else
		{
		for(int j=0;j<ElevatorConstants.ELEVATOR_COUNT;j++){
			if((elevators[j].getDirection()==1)&&(i>elevators[j].getCurPosition())&&(elevators[j].get_NextDirection()==1))
			{
				Temp=Math.abs(i-elevators[j].getCurPosition());
				if(Temp<Distance1)
				{
					Elevator1=j;
					Distance1=Temp;
				}
			}
		}
		for(int j=0;j<ElevatorConstants.ELEVATOR_COUNT;j++){
			if(elevators[j].getDirection()==0)
			{
				Temp=Math.abs(i-elevators[j].getCurPosition());
				if(Temp<Distance2)
				{
					Elevator2=j;
					Distance2=Temp;
				}
			}
		}
		if((Distance1!=20)||(Distance2!=20))
		{
			if(Distance1<=Distance2)
			{
				elevators[Elevator1].Set_NextDirectionUp();
				elevators[Elevator1].setToFloor(i);
				upState[i]=false;
			}	
			else if(Distance2<Distance1)
			{
				elevators[Elevator2].Set_NextDirectionUp();
				elevators[Elevator2].setToFloor(i);
				upState[i]=false;
			}
		}
		}
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"






















