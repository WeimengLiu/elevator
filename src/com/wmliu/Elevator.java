package com.wmliu;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import com.wmliu.audio.SoundPlayer;
import com.wmliu.constants.ElevatorConstants;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.border.EmptyBorder;

public class Elevator extends JPanel implements Runnable{

	private static final long serialVersionUID = 1L;
	private JButton[] button=new JButton[20];
	private JButton[] Button=new JButton[20];
	private int CurPosition=0;
	private int ToFloor=0;
	private int Up=1,Down=-1,Still=0;
	private int Direction;
	private Thread thread;
	private boolean[] state=new boolean[20];
	private JLabel jlabel=new JLabel();
	private JLabel jlabel1=new JLabel();
	private Color color=Color.GRAY;  //  @jve:decl-index=0:
	private JButton NoUse=new JButton();
	private final SoundPlayer soundPlayer = new SoundPlayer();  // 替换 Ring 实例
	private final JButton alarm = new JButton("SOS");  // 添加这行
	private int Next_Direction;
	private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
	private static final Color BUTTON_ACTIVE_COLOR = new Color(255, 69, 0);    // 亮橙红色
	private static final Color BUTTON_INACTIVE_COLOR = new Color(40, 44, 52);  // 深灰色
	private static final Color BUTTON_HIGHLIGHT_COLOR = new Color(255, 255, 255);
	private static final Font DISPLAY_FONT = new Font("Arial", Font.BOLD, 24);
	private static final int BUTTON_SIZE = 40;
	/**
	 * This is the default constructor
	 */
	public Elevator() {
		super();
		initialize();
	}
	public Thread getthread()
	{
		return thread;
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(200, 600);
		this.setLayout(new GridLayout(22, 2, 5, 5));  // 增加间距
		this.setBackground(BACKGROUND_COLOR);
		this.setBorder(new EmptyBorder(10, 10, 10, 10));  // 添加边距
		
		Direction = Still;
		Next_Direction = Still;
		thread = new Thread(this);
		
		// 设置状态显示标签
		setupDisplayLabels();
		
		// 设置电梯按钮
		setupElevatorButtons();
		
		// 设置控制按钮
		setupControlButtons();
		
		updateElevatorPosition(0);
	}
	private void setupDisplayLabels() {
		jlabel.setText("STILL");
		jlabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
		jlabel.setForeground(new Color(0, 102, 204));
		jlabel.setBackground(new Color(240, 240, 240));
		jlabel.setOpaque(true);
		jlabel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
			BorderFactory.createEmptyBorder(4, 8, 4, 8)
		));
		
		jlabel1.setText("1");
		jlabel1.setFont(new Font("微软雅黑", Font.BOLD, 14));
		jlabel1.setForeground(new Color(0, 102, 204));
		jlabel1.setBackground(new Color(240, 240, 240));
		jlabel1.setOpaque(true);
		jlabel1.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
			BorderFactory.createEmptyBorder(4, 8, 4, 8)
		));
		
		this.add(jlabel);
		this.add(jlabel1);
	}
	private void setupElevatorButtons() {
		for(int i=19; i>=0; i--) {
			state[i] = false;
			
			// 楼层按钮
			button[i] = createStyledButton(String.valueOf(i+1), false);
			Button[i] = createStyledButton(String.valueOf(i+1), true);
			
			Button[i].addActionListener(new Action());
			this.add(button[i]);
			this.add(Button[i]);
		}
	}
	private JButton createStyledButton(String text, boolean isClickable) {
		JButton btn = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				if (isClickable) {
					GradientPaint gp = new GradientPaint(
						0, 0, getBackground().brighter(),
						0, getHeight(), getBackground()
					);
					g2.setPaint(gp);
				} else {
					g2.setColor(getBackground());
				}
				
				g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
				g2.setColor(getForeground());
				g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
				
				super.paintComponent(g);
				g2.dispose();
			}
		};
		
		btn.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
		btn.setFont(new Font("Arial", Font.BOLD, 14));
		
		if (isClickable) {
			btn.setBackground(new Color(240, 240, 240));
			btn.setForeground(new Color(50, 50, 50));
		} else {
			btn.setEnabled(false);
			btn.setBackground(BUTTON_INACTIVE_COLOR);
			btn.setForeground(Color.WHITE);
		}
		
		btn.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		btn.setFocusPainted(false);
		btn.setContentAreaFilled(false);
		btn.setOpaque(true);
		
		return btn;
	}
	private void setupControlButtons() {
		alarm.setText("⚠ SOS");
		alarm.setFont(new Font("微软雅黑", Font.BOLD, 14));
		alarm.setBackground(new Color(255, 69, 0));
		alarm.setForeground(Color.WHITE);
		alarm.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		alarm.setFocusPainted(false);
		alarm.setOpaque(true);
		
		NoUse.setEnabled(false);
		NoUse.setBackground(BACKGROUND_COLOR);
		NoUse.setBorder(null);
		
		this.add(NoUse);
		this.add(alarm);
	}
	private void updateElevatorPosition(int position) {
		for(int i=0; i<20; i++) {
			button[i].setBackground(Color.black);
			button[i].setOpaque(true);
			button[i].repaint();
		}
		button[position].setBackground(Color.red);
		button[position].setOpaque(true);
		button[position].repaint();
	}
	class Action implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			for(int i=0;i<20;i++){
				if(e.getSource()==Button[i])
				{
					state[i]=true;
					Button[i].setBackground(color);
					if(Direction==Still)
					{
						ToFloor=i;
					}
					else if(Direction==Up){
						ToFloor=MaxToFloor();
					}
					else if(Direction==Down)
					{
						ToFloor=MinToFloor();
					}
				}
			}
		}
	}
	private int MaxToFloor() {
		// TODO Auto-generated method stub
		int Max = -1;
		for(int i=19;i>=0;i--){
			if(state[i])
			{
				Max=i;
				break;
			}
		}
		return Max;
	}
	private int MinToFloor() {
		// TODO Auto-generated method stub
		int Min=-1;
		for(int i=0;i<20;i++){
			if(state[i])
			{
				Min=i;
				break;
			}
		}
		return Min;
	}
	
	
	
	
	public void run()
	{
		while(true){
			try {
				thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(ToFloor>CurPosition){
				Direction=Up;
				MoveUp();
				Direction=Still;
				jlabel.setText("STILL");
				}
			else if(ToFloor<CurPosition)
			{
				Direction=Down;
				MoveDown();
				Direction=Still;
				jlabel.setText("STILL");
			}
			for(int i=0;i<20;i++)
			{
				if(state[i])
				{
					if(Direction==Still)
					{
						ToFloor=i;
					}
					else if(Direction==Up){
						ToFloor=MaxToFloor();
					}
					else if(Direction==Down)
					{
						ToFloor=MinToFloor();
					}
				}
			}
		}
	}

	private void MoveUp() {
		if(Next_Direction==Up || Next_Direction==Still) {
			jlabel.setText("UP");
			int OldCurPosition = CurPosition;
			try {
				thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for(int i=OldCurPosition+1; i<=ToFloor; i++) {
				updateElevatorPosition(i);
				jlabel1.setText(String.valueOf(i+1));
				try {
					thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				CurPosition = i;
				
				if(state[i]) {
					state[i] = false;
					Button[i].setBackground(null);
					button[i].setBackground(Color.white);
					button[i].repaint();
					playElevatorSound();
					try {
						thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					updateElevatorPosition(i);
				}
			}
			
			// 到达目标楼层
			button[ToFloor].setBackground(Color.white);
			button[ToFloor].repaint();
			Button[ToFloor].setBackground(null);
			playElevatorSound();
			try {
				thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			updateElevatorPosition(ToFloor);
			CurPosition = ToFloor;
			state[ToFloor] = false;
		}
		else if(Next_Direction==Down)
		{
			jlabel.setText("UP");
			int OldCurPosition=CurPosition;
			try {
				thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			button[OldCurPosition].setBackground(Color.black);
			button[OldCurPosition].setOpaque(true);
			for(int i=OldCurPosition+1;i<ToFloor;i++)
			{
				button[i].setBackground(Color.red);
				button[i].setOpaque(true);
				button[i].repaint();
				jlabel1.setText(String.valueOf(i+1));
				try {
					thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				button[i].setBackground(Color.black);
				button[i].repaint();
				CurPosition=i;
			}
			button[ToFloor].setBackground(Color.red);
			jlabel1.setText(String.valueOf(ToFloor+1));
			Button[ToFloor].setBackground(null);
			try {
				thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			button[ToFloor].setBackground(Color.white);
			playElevatorSound();
			try {
				thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			button[ToFloor].setBackground(Color.red);
			CurPosition=ToFloor;
			state[ToFloor]=false;
			if(MinToFloor()!=-1)
			{
				ToFloor=MinToFloor();
			}
			Next_Direction=Still;
			MoveDown();

		}
	}

	private void MoveDown() {
		if(Next_Direction==Down || Next_Direction==Still) {
			jlabel.setText("DOWN");
			int OldCurPosition = CurPosition;
			try {
				thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for(int i=OldCurPosition-1; i>=ToFloor; i--) {
				updateElevatorPosition(i);
				jlabel1.setText(String.valueOf(i+1));
				try {
					thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				CurPosition = i;
				
				if(state[i]) {
					state[i] = false;
					Button[i].setBackground(null);
					button[i].setBackground(Color.white);
					button[i].repaint();
					playElevatorSound();
					try {
						thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					updateElevatorPosition(i);
				}
			}
			
			// 到达目标楼层
			button[ToFloor].setBackground(Color.white);
			button[ToFloor].repaint();
			Button[ToFloor].setBackground(null);
			playElevatorSound();
			try {
				thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			updateElevatorPosition(ToFloor);
			CurPosition = ToFloor;
			state[ToFloor] = false;
		}
		else if(Next_Direction==Up)
		{
			jlabel.setText("DOWN");
			int OldCurPosition=CurPosition;
			try {
				thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			button[OldCurPosition].setBackground(Color.black);
			button[OldCurPosition].setOpaque(true);
			for(int i=OldCurPosition-1;i>ToFloor;i--)
			{
				
				button[i].setBackground(Color.red);
				button[i].setOpaque(true);
				button[i].repaint();
				jlabel1.setText(String.valueOf(i+1));
				try {
					thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				button[i].setBackground(Color.black);
				button[i].repaint();
				CurPosition=i;
			}
			button[ToFloor].setBackground(Color.red);
			jlabel1.setText(String.valueOf(ToFloor+1));
			Button[ToFloor].setBackground(null);
			try {
				thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			button[ToFloor].setBackground(Color.white);
			playElevatorSound();
			try {
				thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			button[ToFloor].setBackground(Color.red);
			CurPosition=ToFloor;
			state[ToFloor]=false;
			if(MaxToFloor()!=-1)
			{
				ToFloor=MaxToFloor();
			}
			Next_Direction=Still;
			MoveUp();
		}
	}
	public void setToFloor(int i)
	{
		state[i]=true;
		if(Direction==Still)
			ToFloor=i;
		else if(Direction==Up)
			ToFloor=MaxToFloor();
		else if(Direction==Down)
		{
			ToFloor=MinToFloor();
		}
	}
	public int getCurPosition()
	{
		return CurPosition;
	}
	public int getDirection()
	{
		return Direction;
	}
	public int getToFloor()
	{
		
		return ToFloor;
	}
	public void Set_NextDirectionUp()
	{
		Next_Direction=Up;
	}
	public void Set_NextDirectionDown()
	{
		Next_Direction=Down;
	}
	public int get_NextDirection()
	{
		return Next_Direction;
	}
	private void playElevatorSound() {
		soundPlayer.playSound("lift.wav");
	}
}//  @jve:decl-index=0:visual-constraint="10,10"



















































