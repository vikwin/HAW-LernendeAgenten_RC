package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import utils.Config;

public class ConfigurationWindow {

	private Process robocodeProcess;
	private JFrame frmLarcbotExperimentKonfigurator;
	private JFileChooser fc;
	private DefaultListModel<String> robot_listModel;
	private JTextField robocodeHome;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new NimbusLookAndFeel());

					ConfigurationWindow window = new ConfigurationWindow();
					window.frmLarcbotExperimentKonfigurator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ConfigurationWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		int val;
		
		fc = new JFileChooser(Config.getStringValue("RobocodeHome"));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		frmLarcbotExperimentKonfigurator = new JFrame();
		frmLarcbotExperimentKonfigurator.getContentPane().setBackground(Color.WHITE);
		frmLarcbotExperimentKonfigurator.setBackground(Color.WHITE);
		frmLarcbotExperimentKonfigurator
				.setTitle("LARCBot Experiment Konfigurator");
		frmLarcbotExperimentKonfigurator.setResizable(false);
		frmLarcbotExperimentKonfigurator.setBounds(100, 100, 478, 430);
		frmLarcbotExperimentKonfigurator
				.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLarcbotExperimentKonfigurator.getContentPane().setLayout(
				new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.WHITE);
		frmLarcbotExperimentKonfigurator.getContentPane().add(tabbedPane);

		JPanel bot_panel = new JPanel();
		bot_panel.setBackground(Color.WHITE);
		tabbedPane.addTab("LARCBot", null, bot_panel, null);
		bot_panel.setLayout(null);
		
		JPanel agent_panel = new JPanel();
		agent_panel.setBackground(Color.WHITE);
		agent_panel.setBorder(new TitledBorder(null, "Agenten", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		agent_panel.setBounds(6, 11, 231, 316);
		bot_panel.add(agent_panel);
		agent_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		JPanel panel_6 = new JPanel();
		panel_6.setBackground(Color.WHITE);
		agent_panel.add(panel_6);
		FlowLayout flowLayout_12 = (FlowLayout) panel_6.getLayout();
		flowLayout_12.setVgap(3);
		flowLayout_12.setHgap(3);
		flowLayout_12.setAlignment(FlowLayout.LEFT);
		
		JCheckBox loadOnStart = new JCheckBox("Letzten Stand laden");
		loadOnStart.setBackground(Color.WHITE);
		loadOnStart.setSelected(Config.getBoolValue("Agent_LoadOnStart"));
		panel_6.add(loadOnStart);
		
		JPanel panel_7 = new JPanel();
		FlowLayout flowLayout_8 = (FlowLayout) panel_7.getLayout();
		flowLayout_8.setVgap(3);
		panel_7.setBackground(Color.WHITE);
		agent_panel.add(panel_7);
		
		JLabel lblErfolgsrate = new JLabel("Erfolgsrate (%):");
		panel_7.add(lblErfolgsrate);
		
		JSpinner succesChance = new JSpinner();
		panel_7.add(succesChance);
		val = Config.getIntValue("Agent_SuccesChance");
		succesChance.setModel(new SpinnerNumberModel(val < 50 ? 50 : val, 50, 100, 1));
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.WHITE);
		agent_panel.add(panel_3);
		panel_3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 3));
		
		JLabel lblNewLabel = new JLabel("Speicherzyklus:");
		panel_3.add(lblNewLabel);
		
		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout_5 = (FlowLayout) panel_5.getLayout();
		flowLayout_5.setVgap(3);
		flowLayout_5.setHgap(20);
		panel_5.setBackground(Color.WHITE);
		agent_panel.add(panel_5);
		
		JSpinner saveTimes = new JSpinner();
		panel_5.add(saveTimes);
		val = Config.getIntValue("Agent_SaveTimes");
		saveTimes.setModel(new SpinnerNumberModel(val < 10000 ? 10000 : val, 10000, 10000000, 10000));
		
		JPanel panel_13 = new JPanel();
		panel_13.setBackground(Color.WHITE);
		agent_panel.add(panel_13);
		
		JLabel lblPropagationtiefe = new JLabel("Propagationstiefe:");
		panel_13.add(lblPropagationtiefe);
		
		JSpinner propagationDepth = new JSpinner();
		val = Config.getIntValue("Agent_QueueSize");
		propagationDepth.setModel(new SpinnerNumberModel(val == 0 ? 1 : val, 1, 100, 1));
		propagationDepth.setBackground(Color.WHITE);
		panel_13.add(propagationDepth);
		
		JPanel learnAlgorithm = new JPanel();
		agent_panel.add(learnAlgorithm);
		learnAlgorithm.setBorder(new TitledBorder(null, "SARASA-Lambda", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(59, 59, 59)));
		learnAlgorithm.setBackground(Color.WHITE);
		learnAlgorithm.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_16 = new JPanel();
		learnAlgorithm.add(panel_16, BorderLayout.NORTH);
		FlowLayout flowLayout_11 = (FlowLayout) panel_16.getLayout();
		flowLayout_11.setVgap(2);
		flowLayout_11.setAlignment(FlowLayout.LEFT);
		panel_16.setBackground(Color.WHITE);
		
		JLabel lblLambda = new JLabel("Lambda (%):");
		lblLambda.setBackground(Color.WHITE);
		panel_16.add(lblLambda);
		
		JSpinner lambda = new JSpinner();
		val = Config.getIntValue("Agent_Lambda");
		lambda.setModel(new SpinnerNumberModel(val, 0, 100, 10));
		lambda.setBackground(Color.WHITE);
		panel_16.add(lambda);
		
		JPanel panel_14 = new JPanel();
		learnAlgorithm.add(panel_14, BorderLayout.CENTER);
		FlowLayout flowLayout_9 = (FlowLayout) panel_14.getLayout();
		flowLayout_9.setVgap(2);
		flowLayout_9.setAlignment(FlowLayout.LEFT);
		panel_14.setBackground(Color.WHITE);
		
		JLabel lblLernrate = new JLabel("Lernrate (%):");
		lblLernrate.setBackground(Color.WHITE);
		panel_14.add(lblLernrate);
		
		JSpinner learnRate = new JSpinner();
		val = Config.getIntValue("Agent_LearnRate");
		learnRate.setModel(new SpinnerNumberModel(val, 0, 100, 10));
		learnRate.setBackground(Color.WHITE);
		panel_14.add(learnRate);
		
		JPanel panel_15 = new JPanel();
		learnAlgorithm.add(panel_15, BorderLayout.SOUTH);
		FlowLayout flowLayout_10 = (FlowLayout) panel_15.getLayout();
		flowLayout_10.setVgap(2);
		flowLayout_10.setAlignment(FlowLayout.LEFT);
		panel_15.setBackground(Color.WHITE);
		
		JLabel lblDiscountrate = new JLabel("Discount-Rate (%):");
		lblDiscountrate.setBackground(Color.WHITE);
		panel_15.add(lblDiscountrate);
		
		JSpinner discountRate = new JSpinner();
		val = Config.getIntValue("Agent_DiscountRate");
		discountRate.setModel(new SpinnerNumberModel(val, 0, 100, 10));
		discountRate.setBackground(Color.WHITE);
		panel_15.add(discountRate);
		
		JPanel robot_panel = new JPanel();
		robot_panel.setBackground(Color.WHITE);
		robot_panel.setBorder(new TitledBorder(null, "Roboter", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		robot_panel.setBounds(241, 11, 225, 135);
		bot_panel.add(robot_panel);
		robot_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.WHITE);
		robot_panel.add(panel_4);
		FlowLayout flowLayout = (FlowLayout) panel_4.getLayout();
		flowLayout.setVgap(8);
		flowLayout.setHgap(3);
		
		JCheckBox simpleReward = new JCheckBox("Erweitertes Belohnungssystem");
		simpleReward.setBackground(Color.WHITE);
		simpleReward.setSelected(!Config.getBoolValue("Robot_SimpleReward"));
		panel_4.add(simpleReward);
		
		JPanel panel_17 = new JPanel();
		robot_panel.add(panel_17);
		FlowLayout flowLayout_13 = (FlowLayout) panel_17.getLayout();
		flowLayout_13.setVgap(8);
		flowLayout_13.setHgap(3);
		panel_17.setBackground(Color.WHITE);
		
		JCheckBox extendedAttackEnv = new JCheckBox("Erweiterte Angriffsumwelt");
		extendedAttackEnv.setSelected(Config.getBoolValue("Robot_UseExtendedAttackEnv"));
		panel_17.add(extendedAttackEnv);
		
		JPanel panel_18 = new JPanel();
		robot_panel.add(panel_18);
		FlowLayout flowLayout_14 = (FlowLayout) panel_18.getLayout();
		flowLayout_14.setVgap(8);
		flowLayout_14.setHgap(3);
		panel_18.setBackground(Color.WHITE);
		
		JCheckBox extendedMoveEnv = new JCheckBox("Erweiterte Bewegungsumwelt");
		extendedMoveEnv.setSelected(Config.getBoolValue("Robot_UseExtendedMoveEnv"));
		panel_18.add(extendedMoveEnv);
		
		JPanel env_panel = new JPanel();
		env_panel.setBackground(Color.WHITE);
		env_panel.setBorder(new TitledBorder(null, "Umwelt", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		env_panel.setBounds(241, 148, 225, 179);
		bot_panel.add(env_panel);
		env_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		JPanel panel_8 = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) panel_8.getLayout();
		flowLayout_4.setVgap(8);
		flowLayout_4.setHgap(3);
		panel_8.setBackground(Color.WHITE);
		env_panel.add(panel_8);
		
		JCheckBox envDebug = new JCheckBox("Debugausgaben");
		envDebug.setBackground(Color.WHITE);
		envDebug.setSelected(Config.getBoolValue("Env_Debug"));
		panel_8.add(envDebug);
		
		JPanel panel_9 = new JPanel();
		FlowLayout flowLayout_6 = (FlowLayout) panel_9.getLayout();
		flowLayout_6.setVgap(8);
		flowLayout_6.setAlignment(FlowLayout.LEFT);
		flowLayout_6.setHgap(3);
		panel_9.setBackground(Color.WHITE);
		env_panel.add(panel_9);
		
		JCheckBox paintMoveEnv = new JCheckBox("Move-Umwelt zeichnen");
		paintMoveEnv.setBackground(Color.WHITE);
		paintMoveEnv.setSelected(Config.getBoolValue("Env_PaintMoveEnv"));
		panel_9.add(paintMoveEnv);
		
		JPanel panel_10 = new JPanel();
		FlowLayout flowLayout_7 = (FlowLayout) panel_10.getLayout();
		flowLayout_7.setVgap(8);
		flowLayout_7.setHgap(3);
		flowLayout_7.setAlignment(FlowLayout.LEFT);
		panel_10.setBackground(Color.WHITE);
		env_panel.add(panel_10);
		
		JCheckBox paintAttackEnv = new JCheckBox("Attack-Umwelt zeichnen");
		paintAttackEnv.setBackground(Color.WHITE);
		paintAttackEnv.setSelected(Config.getBoolValue("Env_PaintAttackEnv"));
		panel_10.add(paintAttackEnv);
		
		JPanel moveGridSize_panel = new JPanel();
		moveGridSize_panel.setBackground(Color.WHITE);
		env_panel.add(moveGridSize_panel);

		JSpinner moveGridSize = new JSpinner();
		val = Config.getIntValue("Env_GridSize");
		moveGridSize.setModel(new SpinnerNumberModel(val == 0 ? new Integer(40) : val, null, null, new Integer(1)));
		moveGridSize.setBackground(Color.WHITE);
		moveGridSize.setEnabled(Config.getBoolValue("Env_UseExtendedMoveEnv"));
		moveGridSize_panel.add(moveGridSize);
		
		JLabel lblNewLabel_1 = new JLabel("Bewegungsgittergröße");
		moveGridSize_panel.add(lblNewLabel_1);
		
		JPanel robocode_panel = new JPanel();
		robocode_panel.setBackground(Color.WHITE);
		tabbedPane.addTab("Robocode", null, robocode_panel, null);
		robocode_panel.setLayout(null);

		JPanel settings_panel = new JPanel();
		settings_panel.setBackground(Color.WHITE);
		settings_panel.setBounds(7, 7, 206, 172);
		settings_panel.setBorder(new EmptyBorder(0, 0, 0, 10));
		robocode_panel.add(settings_panel);
		settings_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EmptyBorder(0, 0, 0, 20));
		settings_panel.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 2));

		JCheckBox showGUI = new JCheckBox("GUI starten");
		showGUI.setBackground(Color.WHITE);
		showGUI.setSelected(Config.getBoolValue("ShowRobocodeGUI"));
		panel.add(showGUI);
		showGUI.setHorizontalAlignment(SwingConstants.LEFT);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel_1.setBorder(new EmptyBorder(0, 0, 0, 20));
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setVgap(2);
		flowLayout_1.setHgap(0);
		settings_panel.add(panel_1);

		JCheckBox startBattle = new JCheckBox("Battle starten");
		startBattle.setBackground(Color.WHITE);
		startBattle.setSelected(Config.getBoolValue("StartBattle"));
		panel_1.add(startBattle);

		JPanel rounds_panel = new JPanel();
		rounds_panel.setBackground(Color.WHITE);
		FlowLayout flowLayout_2 = (FlowLayout) rounds_panel.getLayout();
		flowLayout_2.setHgap(0);
		flowLayout_2.setVgap(2);
		settings_panel.add(rounds_panel);

		JSpinner rounds = new JSpinner();
		val = Config.getIntValue("Rounds");
		rounds.setModel(new SpinnerNumberModel(val < 10 ? 10 : val, 10, 100000000, 10));
		rounds_panel.add(rounds);

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		FlowLayout flowLayout_3 = (FlowLayout) panel_2.getLayout();
		flowLayout_3.setHgap(10);
		rounds_panel.add(panel_2);

		JLabel lblRunden = new JLabel("Runden");
		panel_2.add(lblRunden);
		lblRunden.setHorizontalAlignment(SwingConstants.LEFT);
		
		JPanel enemy_panel = new JPanel();
		enemy_panel.setBorder(new TitledBorder(null, "Gegner", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		enemy_panel.setBackground(Color.WHITE);
		enemy_panel.setBounds(225, 76, 241, 251);
		FlowLayout fl_enemy_panel = (FlowLayout) enemy_panel.getLayout();
		fl_enemy_panel.setAlignment(FlowLayout.LEFT);
		fl_enemy_panel.setVgap(0);
		fl_enemy_panel.setHgap(0);
		robocode_panel.add(enemy_panel);

		JList<String> robot_list = new JList<String>();
		robot_list.setVisibleRowCount(11);
		robot_list.setBorder(null);
		robot_listModel = new DefaultListModel<String>();
		robot_listModel.addElement("###########################");
		loadRobots();
		robot_list.setModel(robot_listModel);
		robot_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		robot_list.setSelectedValue(Config.getStringValue("EnemyRobot"), true);

		JScrollPane scrollPane = new JScrollPane(robot_list);
		scrollPane.setViewportBorder(null);
		enemy_panel.add(scrollPane);
		
		JPanel panel_12 = new JPanel();
		panel_12.setBounds(225, 7, 241, 70);
		robocode_panel.add(panel_12);
		panel_12.setBorder(new TitledBorder(null, "Robocode Home Directory:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_12.setBackground(Color.WHITE);
		FlowLayout fl_panel_12 = new FlowLayout(FlowLayout.LEFT, 0, 5);
		panel_12.setLayout(fl_panel_12);
		
		robocodeHome = new JTextField();
		robocodeHome.setText(Config.getStringValue("RobocodeHome"));
		panel_12.add(robocodeHome);
		robocodeHome.setColumns(13);
		
		JButton button = new JButton("...");
		button.setSize(10, button.getHeight());
		button.setIcon(null);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int res = fc.showOpenDialog(frmLarcbotExperimentKonfigurator);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					robocodeHome.setText(fc.getSelectedFile().getPath());
					Config.setStringValue("RobocodeHome", robocodeHome.getText());
					
					loadRobots();
				}
			}
		});
		button.setBackground(Color.WHITE);
		panel_12.add(button);

		JPanel button_panel = new JPanel();
		button_panel.setBackground(Color.WHITE);
		FlowLayout fl_button_panel = (FlowLayout) button_panel.getLayout();
		fl_button_panel.setHgap(15);
		fl_button_panel.setAlignment(FlowLayout.RIGHT);
		frmLarcbotExperimentKonfigurator.getContentPane().add(button_panel,
				BorderLayout.SOUTH);

		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Config.setBoolValue("Agent_LoadOnStart", loadOnStart.isSelected());
				Config.setIntValue("Agent_SaveTimes", (int)saveTimes.getValue());
				Config.setIntValue("Agent_SuccesChance", (int)succesChance.getValue());
				Config.setIntValue("Agent_LearnRate", (int)learnRate.getValue());
				Config.setIntValue("Agent_DiscountRate", (int)discountRate.getValue());
				Config.setIntValue("Agent_Lambda", (int)lambda.getValue());
				Config.setIntValue("Agent_QueueSize", (int)propagationDepth.getValue());
				
				Config.setBoolValue("Robot_SimpleReward", !simpleReward.isSelected());
				Config.setBoolValue("Robot_UseExtendedMoveEnv", extendedMoveEnv.isSelected());
				Config.setBoolValue("Robot_UseExtendedAttackEnv", extendedAttackEnv.isSelected());
				
				Config.setBoolValue("Env_Debug", envDebug.isSelected());
				Config.setBoolValue("Env_PaintMoveEnv", paintMoveEnv.isSelected());
				Config.setBoolValue("Env_PaintAttackEnv", paintAttackEnv.isSelected());
				Config.setIntValue("Env_GridSize", (int)moveGridSize.getValue());
				
				Config.setBoolValue("ShowRobocodeGUI", showGUI.isSelected());
				Config.setBoolValue("StartBattle", startBattle.isSelected());
				Config.setIntValue("Rounds", (int)rounds.getValue());
				Config.setStringValue("EnemyRobot", robot_list.getSelectedValue());
				Config.setIntValue("FieldWidth", 800);
				Config.setIntValue("FieldHeight", 600);

				start();
			}
		});
		button_panel.add(btnStart);
		
		extendedMoveEnv.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				moveGridSize.setEnabled(extendedMoveEnv.isSelected());
			}
		});
	}

	private void loadRobots() {
		String robocode = Config.getStringValue("RobocodeHome");
		File robots = new File(robocode, "robots");

		if (robots.isDirectory()) {
			robot_listModel.removeAllElements();
			for (File f : robots.listFiles()) {
				String name = "";
				if (f.isDirectory()) {
					for (String s : f.list()) {
						name = f.getName() + ".";
						if (s.indexOf('$') < 0 && s.endsWith(".class")) {
							name += s.substring(0, s.lastIndexOf('.'));
							robot_listModel.addElement(name);
						}
					}
				} else if (f.getName().endsWith(".jar")) {
					name = f.getName();
					robot_listModel.addElement(name.substring(0, name.lastIndexOf('.')));
				}
			}
		}
	}

	private void start() {
		String rHome = Config.getStringValue("RobocodeHome"), robocodeArgs = "", battle = "", vmArgs = "-Xmx512M -Dsun.io.useCanonCaches=false -Ddebug=true -DNOSECURITY=true -DPARALLEL=true";
		ArrayList<String> classpath = new ArrayList<String>();

		classpath.add(rHome + "\\libs\\robocode.jar");
		classpath.add("\"" + new File(".").getAbsolutePath() + "\"");
		File libs = new File(".\\libs");
		if (libs.exists() && libs.isDirectory()) {
			for (File f : libs.listFiles()) {
				classpath.add("\"" + f.getAbsolutePath() + "\"");
			}
		}

		Config.save();

		if (!Config.getBoolValue("ShowRobocodeGUI")) {
			robocodeArgs += "-nodisplay ";
		}

		if (Config.getBoolValue("StartBattle")) {
			battle = Config.createAndSaveBattle();
			robocodeArgs += "-battle " + battle + " ";
		}

		try {
			robocodeProcess = Runtime.getRuntime().exec(
					"java " + vmArgs + " -cp " + String.join(";", classpath)
							+ " robocode.Robocode " + robocodeArgs, null,
					new File(rHome));
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					String s = "";
					BufferedReader reader = new BufferedReader(new InputStreamReader(robocodeProcess.getInputStream()));
					try {
						while ((s = reader.readLine()) != null) {
							System.out.println(s);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
//			System.out.println(robocodeProcess.waitFor());
		} catch (IOException e) {
			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
	}
}
