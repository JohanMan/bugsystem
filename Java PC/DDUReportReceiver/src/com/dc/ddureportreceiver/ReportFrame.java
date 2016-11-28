package com.dc.ddureportreceiver;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dc.ddureportreceiver.db.LogDao;

public class ReportFrame extends JFrame {

	private static final long serialVersionUID = -3482568771987287984L;
	
	private static final int FRAME_WIDTH = 800;
	private static final int FRAME_HEIGHT = 527;
	
	private SystemTray tray = SystemTray.getSystemTray();
	private TrayIcon trayIcon;
	
	private ReportPanel reportPanel;
	
	public ReportFrame() {
		setTitle("Bug日志");
		setIcon();
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		showCenter();
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
			@Override
			public void windowIconified(WindowEvent event) {
				miniTray();
			}
		});
		reportPanel = new ReportPanel();
		add(reportPanel);
		setVisible(true);
	}
	
	private void setIcon() {
		Image icon = java.awt.Toolkit.getDefaultToolkit().getImage("image/ic_launcher.jpg");
		setIconImage(icon);
	}
	
	private void showCenter() {
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (screenSize.width - FRAME_WIDTH) / 2;
		int centerY = (screenSize.height - FRAME_HEIGHT) / 2;
		setLocation(centerX, centerY);
	}
	
	private void miniTray() {
		setVisible(false);
		Image icon = java.awt.Toolkit.getDefaultToolkit().getImage("image/ic_launcher.jpg");
		ImageIcon trayImage = new ImageIcon(icon);
		PopupMenu pop = new PopupMenu();
		MenuItem show = new MenuItem("还原");
		MenuItem exit = new MenuItem("退出");
		trayIcon = new TrayIcon(trayImage.getImage(), "Bug日志", pop);
		trayIcon.setImageAutoSize(true);
		show.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // 按下还原键
				restored();
			}
		});
		exit.addActionListener(new ActionListener() { // 按下退出键
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		pop.add(show);
		pop.add(exit);
		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) { // 鼠标器双击事件
				if (e.getClickCount() == 2) {
					restored();
				}
			}
		});
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	private void exit() {
		if (trayIcon != null) {
			tray.remove(trayIcon); // 移去托盘图标
		}
		System.exit(0);
	}
	
	private void restored() {
		if (trayIcon != null) {
			tray.remove(trayIcon); // 移去托盘图标
		}
		setVisible(true);
		setExtendedState(JFrame.NORMAL); // 还原窗口
		toFront();
	}
	
	public void receiveReport(String logPath) {
		reportPanel.receiveReport(logPath);
		restored();
	}
	
	/**
	 * ReportPanel
	 * @author Administrator
	 */
	public class ReportPanel extends JPanel {

		private static final long serialVersionUID = -292202195169545049L;
		
		private static final int LOG_STATE_SOLVED = 1;
		private static final int LOG_STATE_UNSOLVE = 0;
		
		private static final int LOG_LAYOUT_SPACE = 5;
		private static final int LOG_PANEL_SPACE = 5;
		private static final int LOG_PANEL_HEIGHT = 35;
		
		private JPanel logPanel;
		
		private boolean readyListener = false;
		
		private LogDao logDao;
		private List<Log> logList;
		
		private static final int LOG_PANEL_TOP = 1;
		private static final int CAN_SEE_SIZE = 12;
		private int offSetPosition = 0;
		
		private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		
		public ReportPanel() {
			setSize(FRAME_WIDTH, FRAME_HEIGHT);
			setLayout(null);
			initView();
		}
		
		public void initView() {
			JPanel logLayoutPanel = new JPanel();
			logLayoutPanel.setSize(FRAME_WIDTH - 16, FRAME_HEIGHT - 40);
			logLayoutPanel.setLocation(LOG_LAYOUT_SPACE, LOG_LAYOUT_SPACE);
			logLayoutPanel.setBackground(Color.decode("#DDDDDD"));
			logLayoutPanel.setLayout(null);
			JPanel logLayout = new JPanel();
			logLayout.setSize(FRAME_WIDTH - 18, FRAME_HEIGHT - 42);
			logLayout.setLocation(1, 1);
			logLayout.setBackground(Color.decode("#FFFFFF"));
			logLayout.setLayout(null);
			logPanel = new JPanel();
			logPanel.setSize(FRAME_WIDTH - 20, 10000);
			logPanel.setLocation(1, LOG_PANEL_TOP);
			logPanel.setBackground(Color.decode("#FFFFFF"));
			logPanel.setLayout(null);
			logPanel.addMouseWheelListener(new MouseWheelListener() {
				public void mouseWheelMoved(MouseWheelEvent event) {
					int rotation = event.getWheelRotation();
					if (rotation == 1) {
						// 下滚
						if (logList.size() <= CAN_SEE_SIZE || CAN_SEE_SIZE - offSetPosition == logList.size()) {
							return;
						}
						offSetPosition -= 1;
						int logPanelTop = LOG_PANEL_TOP + offSetPosition * (LOG_PANEL_HEIGHT + LOG_PANEL_SPACE);
						logPanel.setLocation(1, logPanelTop);
						logPanel.repaint();
						logPanel.invalidate();
					} else if (rotation == -1) {
						// 上滚
						if (offSetPosition == 0) {
							return;
						}
						offSetPosition += 1;
						int logPanelTop = LOG_PANEL_TOP + offSetPosition * (LOG_PANEL_HEIGHT + LOG_PANEL_SPACE);
						logPanel.setLocation(1, logPanelTop);
						logPanel.repaint();
						logPanel.invalidate();
					}
				}
			});
			logLayout.add(logPanel);
			logLayoutPanel.add(logLayout);
			add(logLayoutPanel);
			initLogList();
			readyListener = true;
		}
		
		private void initLogList() {
			logPanel.removeAll();
			logDao = new LogDao();
			logList = logDao.findLogList();
			refreshLogPanel();
		}
		
		private void addNewLog(String logPath) {
			Log log = new Log();
			log.setTime(dateFormat.format(new Date()));
			log.setLogPath(logPath);
			log.setState(0);
			boolean insertResult = logDao.insertLog(log);
			if (insertResult) {
				logList.add(0, log);
				refreshLogPanel();
			}
		}
		
		private void refreshLogPanel() {
			logPanel.removeAll();
			readyListener = false;
			for (int i = 0; i < logList.size(); i++) {
				showLogPanel(logList.get(i), i);
			}
			readyListener = true;
		}
		
		public void showLogPanel(final Log log, int position) {
			final JPanel panel = new JPanel();
			panel.setSize(FRAME_WIDTH - 20 - LOG_PANEL_SPACE * 2, LOG_PANEL_HEIGHT);
			panel.setLocation(LOG_PANEL_SPACE, LOG_PANEL_SPACE + position * (LOG_PANEL_HEIGHT + LOG_PANEL_SPACE));
			if (log.getState() == LOG_STATE_UNSOLVE) {
				panel.setBackground(Color.decode("#E0E0E0"));
			} else {
				panel.setBackground(Color.decode("#FCFCFC"));
			}
			panel.setLayout(null);
			String logPathInfo = log.getTime() + " : " + log.getLogPath();
			JLabel label = new JLabel();
			label.setSize(FRAME_WIDTH - 20 - LOG_PANEL_SPACE * 2 - 100, LOG_PANEL_HEIGHT);
			label.setLocation(LOG_PANEL_SPACE * 2, 0);
			label.setFont(new Font("宋体", Font.PLAIN, 16));
			label.setText(logPathInfo);
			label.addMouseListener(new MouseClickListener() {
				public void mouseClicked(MouseEvent arg0) {
					FileUtil.openLogFile(log.getLogPath());
				}
			});
			panel.add(label);
			final JComboBox<String> comboBox = new JComboBox<String>();
			comboBox.setSize(80, 21);
			comboBox.setLocation(FRAME_WIDTH - 20 - LOG_PANEL_SPACE * 2 - 100 + LOG_PANEL_SPACE * 2, 7);
			comboBox.addItem(" 未 解");
			comboBox.addItem(" 已 解");
			comboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					if (event.getStateChange() == ItemEvent.SELECTED && readyListener) {
						if (log.getState() == LOG_STATE_UNSOLVE) {
							log.setState(LOG_STATE_SOLVED);
						} else {
							log.setState(LOG_STATE_UNSOLVE);
						}
						boolean updateResult = logDao.updateLog(log);
						if (updateResult) {
							if (log.getState() == LOG_STATE_UNSOLVE) {
								panel.setBackground(Color.decode("#E0E0E0"));
							} else {
								panel.setBackground(Color.decode("#F0F0F0"));
							}
							panel.repaint();
							panel.invalidate();
						}
					}
				}
			});
			if (log.getState() == 0) {
				comboBox.setSelectedIndex(LOG_STATE_UNSOLVE);
			} else {
				comboBox.setSelectedIndex(LOG_STATE_SOLVED);
			}
			comboBox.setFont(new Font("宋体", Font.PLAIN, 14));
			comboBox.setOpaque(true);
			comboBox.setBackground(Color.decode("#FFFFFF"));
			panel.add(comboBox);
			logPanel.add(panel);
			logPanel.repaint();
			logPanel.invalidate();
		}
		
		public void receiveReport(String logPath) {
			addNewLog(logPath);
		}
		
		public abstract class MouseClickListener implements MouseListener {
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
		}
		
	}
	
}
