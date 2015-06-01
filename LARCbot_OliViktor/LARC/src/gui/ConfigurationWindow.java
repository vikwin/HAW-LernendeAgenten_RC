package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
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

	private ArrayList<Process> robocodeProcesses = new ArrayList<Process>();
	private JTabbedPane tabbedPane;
	private JFrame frmLarcbotExperimentKonfigurator;
	private JFileChooser fc;
	private DefaultListModel<String> robot_listModel;
	private JTextField robocodeHome;
	private JPanel processPanel;
	private JPanel processSelectionPanel;
	private ButtonGroup processSelectionBtnGrp;
	
	private Console redirectConsole;

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
		redirectConsole = new Console() {
			@Override
			public void onStreamEnd(int id) {
				removeObjectsForProcess(id);
			}
		};
		
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {		
		fc = new JFileChooser(Config.getStringValue("RobocodeHome"));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		frmLarcbotExperimentKonfigurator = new JFrame();
		frmLarcbotExperimentKonfigurator.getContentPane().setBackground(Color.WHITE);
		frmLarcbotExperimentKonfigurator.setBackground(Color.WHITE);
		frmLarcbotExperimentKonfigurator
				.setTitle("LARCBot Experiment Konfigurator");
		frmLarcbotExperimentKonfigurator.setResizable(false);
		frmLarcbotExperimentKonfigurator.setBounds(100, 100, 478, 388);
		frmLarcbotExperimentKonfigurator
				.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLarcbotExperimentKonfigurator.getContentPane().setLayout(
				new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.WHITE);
		frmLarcbotExperimentKonfigurator.getContentPane().add(tabbedPane);

		JPanel bot_panel = new JPanel();
		bot_panel.setBackground(Color.WHITE);
		tabbedPane.addTab("LARCBot", null, bot_panel, null);
		tabbedPane.setEnabledAt(0, true);
		bot_panel.setLayout(null);
		
		JPanel agent_panel = new JPanel();
		agent_panel.setBackground(Color.WHITE);
		agent_panel.setBorder(new TitledBorder(null, "Agenten", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		agent_panel.setBounds(6, 106, 231, 179);
		bot_panel.add(agent_panel);
		agent_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		JPanel panel_6 = new JPanel();
		panel_6.setBackground(Color.WHITE);
		agent_panel.add(panel_6);
		FlowLayout flowLayout_12 = (FlowLayout) panel_6.getLayout();
		flowLayout_12.setHgap(3);
		flowLayout_12.setAlignment(FlowLayout.LEFT);
		
		JCheckBox loadOnStart = new JCheckBox("Letzten Stand laden");
		loadOnStart.setBackground(Color.WHITE);
		loadOnStart.setSelected(Config.getBoolValue("Agent_LoadOnStart"));
		panel_6.add(loadOnStart);
		
		JPanel panel_7 = new JPanel();
		panel_7.setBackground(Color.WHITE);
		agent_panel.add(panel_7);
		
		JLabel lblErfolgsrate = new JLabel("Erfolgsrate (%):");
		panel_7.add(lblErfolgsrate);
		
		JSpinner succesChance = new JSpinner();
		panel_7.add(succesChance);
		succesChance.setModel(new SpinnerNumberModel(Config.getIntValue("Agent_SuccesChance", 50), 50, 100, 1));
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.WHITE);
		agent_panel.add(panel_3);
		panel_3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 3));
		
		JLabel lblNewLabel = new JLabel("Speicherzyklus:");
		panel_3.add(lblNewLabel);
		
		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout_5 = (FlowLayout) panel_5.getLayout();
		flowLayout_5.setHgap(20);
		panel_5.setBackground(Color.WHITE);
		agent_panel.add(panel_5);
		
		JSpinner saveTimes = new JSpinner();
		panel_5.add(saveTimes);
		saveTimes.setModel(new SpinnerNumberModel(Config.getIntValue("Agent_SaveTimes", 10000), 10000, 10000000, 10000));
		
		JPanel robot_panel = new JPanel();
		robot_panel.setBackground(Color.WHITE);
		robot_panel.setBorder(new TitledBorder(null, "Roboter", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		robot_panel.setBounds(6, 11, 460, 92);
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
		
		JPanel panel_31 = new JPanel();
		FlowLayout flowLayout_23 = (FlowLayout) panel_31.getLayout();
		flowLayout_23.setHgap(0);
		panel_31.setBackground(Color.WHITE);
		robot_panel.add(panel_31);
		
		JPanel panel_17 = new JPanel();
		panel_31.add(panel_17);
		FlowLayout flowLayout_13 = (FlowLayout) panel_17.getLayout();
		flowLayout_13.setVgap(0);
		flowLayout_13.setHgap(3);
		panel_17.setBackground(Color.WHITE);
		
		JCheckBox extendedAttackEnv = new JCheckBox("Erweiterte Angriffsumwelt");
		extendedAttackEnv.setSelected(Config.getBoolValue("Robot_UseExtendedAttackEnv"));
		panel_17.add(extendedAttackEnv);
		
		JPanel panel_18 = new JPanel();
		panel_31.add(panel_18);
		FlowLayout flowLayout_14 = (FlowLayout) panel_18.getLayout();
		flowLayout_14.setVgap(0);
		flowLayout_14.setHgap(8);
		panel_18.setBackground(Color.WHITE);
		
		JCheckBox extendedMoveEnv = new JCheckBox("Erweiterte Bewegungsumwelt");
		extendedMoveEnv.setSelected(Config.getBoolValue("Robot_UseExtendedMoveEnv"));
		panel_18.add(extendedMoveEnv);
		
		JPanel env_panel = new JPanel();
		env_panel.setBackground(Color.WHITE);
		env_panel.setBorder(new TitledBorder(null, "Umwelt", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		env_panel.setBounds(241, 106, 225, 179);
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
		moveGridSize.setModel(new SpinnerNumberModel(Config.getIntValue("Env_GridSize", 40), null, null, new Integer(1)));
		moveGridSize.setBackground(Color.WHITE);
		moveGridSize.setEnabled(Config.getBoolValue("Env_UseExtendedMoveEnv"));
		moveGridSize_panel.add(moveGridSize);
		
		JLabel lblNewLabel_1 = new JLabel("Bewegungsgittergröße");
		moveGridSize_panel.add(lblNewLabel_1);
		
		JPanel algorithm_panel = new JPanel();
		algorithm_panel.setBackground(Color.WHITE);
		tabbedPane.addTab("Algorithmus", null, algorithm_panel, null);
		algorithm_panel.setLayout(null);
		
		ButtonGroup algorithmButtonGroup = new ButtonGroup();
		JPanel switchAlgorithm = new JPanel();
		switchAlgorithm.setBorder(new TitledBorder(null, "Auswahl", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		FlowLayout flowLayout_19 = (FlowLayout) switchAlgorithm.getLayout();
		flowLayout_19.setAlignment(FlowLayout.LEFT);
		flowLayout_19.setVgap(0);
		flowLayout_19.setHgap(0);
		switchAlgorithm.setBackground(Color.WHITE);
		switchAlgorithm.setBounds(6, 6, 460, 60);
		algorithm_panel.add(switchAlgorithm);
		
		JPanel panel_25 = new JPanel();
		FlowLayout flowLayout_18 = (FlowLayout) panel_25.getLayout();
		flowLayout_18.setHgap(3);
		panel_25.setBackground(Color.WHITE);
		switchAlgorithm.add(panel_25);
		
		JRadioButton chooseSarsaLambda = new JRadioButton("SARSA-Lambda");
		algorithmButtonGroup.add(chooseSarsaLambda);
		panel_25.add(chooseSarsaLambda);
		
		JPanel panel_28 = new JPanel();
		FlowLayout flowLayout_20 = (FlowLayout) panel_28.getLayout();
		flowLayout_20.setHgap(20);
		panel_28.setBackground(Color.WHITE);
		switchAlgorithm.add(panel_28);
		
		JRadioButton chooseQLearning = new JRadioButton("Q-Learning");
		algorithmButtonGroup.add(chooseQLearning);
		panel_28.add(chooseQLearning);
		
		switch (Config.getStringValue("Agent_Algorithm")) {
		case "SARSA-Lambda":
			chooseSarsaLambda.setSelected(true);
			break;

		case "Q-Learning":
			chooseQLearning.setSelected(true);
			break;
		}
		
		JPanel sarsaLambda = new JPanel();
		FlowLayout flowLayout_21 = (FlowLayout) sarsaLambda.getLayout();
		flowLayout_21.setVgap(0);
		flowLayout_21.setHgap(0);
		flowLayout_21.setAlignment(FlowLayout.RIGHT);
		sarsaLambda.setBorder(new TitledBorder(null, "SARSA-Lambda", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sarsaLambda.setBackground(Color.WHITE);
		sarsaLambda.setBounds(6, 66, 224, 169);
		algorithm_panel.add(sarsaLambda);
		
		JPanel panel_13 = new JPanel();
		panel_13.setBackground(Color.WHITE);
		sarsaLambda.add(panel_13);
		
		JLabel lblPropagationtiefe = new JLabel("Propagationstiefe:");
		panel_13.add(lblPropagationtiefe);
		
		JSpinner propagationDepth = new JSpinner();
		propagationDepth.setModel(new SpinnerNumberModel(Config.getIntValue("Agent_QueueSize", 1), 1, 100, 1));
		propagationDepth.setBackground(Color.WHITE);
		panel_13.add(propagationDepth);
		
		JPanel panel_16 = new JPanel();
		sarsaLambda.add(panel_16, BorderLayout.NORTH);
		FlowLayout flowLayout_11 = (FlowLayout) panel_16.getLayout();
		flowLayout_11.setVgap(2);
		flowLayout_11.setAlignment(FlowLayout.LEFT);
		panel_16.setBackground(Color.WHITE);
		
		JLabel lblLambda = new JLabel("Lambda (%):");
		lblLambda.setBackground(Color.WHITE);
		panel_16.add(lblLambda);
		
		JSpinner lambda = new JSpinner();
		lambda.setModel(new SpinnerNumberModel(Config.getIntValue("Agent_Lambda"), 0, 100, 10));
		lambda.setBackground(Color.WHITE);
		panel_16.add(lambda);
		
		JPanel panel_14 = new JPanel();
		sarsaLambda.add(panel_14, BorderLayout.CENTER);
		FlowLayout flowLayout_9 = (FlowLayout) panel_14.getLayout();
		flowLayout_9.setVgap(2);
		flowLayout_9.setAlignment(FlowLayout.LEFT);
		panel_14.setBackground(Color.WHITE);
		
		JLabel lblLernrate = new JLabel("Lernrate (%):");
		lblLernrate.setBackground(Color.WHITE);
		panel_14.add(lblLernrate);
		
		JSpinner learnRate = new JSpinner();
		learnRate.setModel(new SpinnerNumberModel(Config.getIntValue("Agent_LearnRate"), 0, 100, 10));
		learnRate.setBackground(Color.WHITE);
		panel_14.add(learnRate);
		
		JPanel panel_15 = new JPanel();
		sarsaLambda.add(panel_15, BorderLayout.SOUTH);
		FlowLayout flowLayout_10 = (FlowLayout) panel_15.getLayout();
		flowLayout_10.setVgap(2);
		flowLayout_10.setAlignment(FlowLayout.LEFT);
		panel_15.setBackground(Color.WHITE);
		
		JLabel lblDiscountrate = new JLabel("Discount-Rate (%):");
		lblDiscountrate.setBackground(Color.WHITE);
		panel_15.add(lblDiscountrate);
		
		JSpinner discountRate = new JSpinner();
		discountRate.setModel(new SpinnerNumberModel(Config.getIntValue("Agent_DiscountRate"), 0, 100, 10));
		discountRate.setBackground(Color.WHITE);
		panel_15.add(discountRate);
		
		JPanel qLearning = new JPanel();
		qLearning.setBackground(Color.WHITE);
		qLearning.setBorder(new TitledBorder(null, "Q-Learning", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		FlowLayout flowLayout_22 = (FlowLayout) qLearning.getLayout();
		flowLayout_22.setAlignment(FlowLayout.RIGHT);
		flowLayout_22.setVgap(0);
		flowLayout_22.setHgap(0);
		qLearning.setBounds(235, 66, 231, 169);
		algorithm_panel.add(qLearning);
		
		JPanel panel_29 = new JPanel();
		qLearning.add(panel_29, BorderLayout.CENTER);
		FlowLayout flowLayout_25 = (FlowLayout) panel_29.getLayout();
		flowLayout_25.setVgap(2);
		flowLayout_25.setAlignment(FlowLayout.LEFT);
		panel_29.setBackground(Color.WHITE);
		
		JLabel lblLernrateQ = new JLabel("Lernrate (%):");
		lblLernrateQ.setBackground(Color.WHITE);
		panel_29.add(lblLernrateQ);
		
		JSpinner learnRateQ = new JSpinner();
		learnRateQ.setModel(new SpinnerNumberModel(Config.getIntValue("Agent_LearnRate"), 0, 100, 10));
		learnRateQ.setBackground(Color.WHITE);
		panel_29.add(learnRateQ);
		
		JPanel panel_30 = new JPanel();
		qLearning.add(panel_30, BorderLayout.SOUTH);
		FlowLayout flowLayout_26 = (FlowLayout) panel_30.getLayout();
		flowLayout_26.setVgap(10);
		flowLayout_26.setAlignment(FlowLayout.LEFT);
		panel_30.setBackground(Color.WHITE);
		
		JLabel lblDiscountrateQ = new JLabel("Discount-Rate (%):");
		lblDiscountrateQ.setBackground(Color.WHITE);
		panel_30.add(lblDiscountrateQ);
		
		JSpinner discountRateQ = new JSpinner();
		discountRateQ.setModel(new SpinnerNumberModel(Config.getIntValue("Agent_DiscountRate"), 0, 100, 10));
		discountRateQ.setBackground(Color.WHITE);
		panel_30.add(discountRateQ);
		
		/* DRITTER TAB */
		JPanel reward_system = new JPanel();
		reward_system.setBackground(Color.WHITE);
		tabbedPane.addTab("Reward", null, reward_system, null);
		tabbedPane.setEnabledAt(2, simpleReward.isSelected());
		reward_system.setLayout(null);
		
		JPanel bullet_rewards = new JPanel();
		FlowLayout flowLayout_15 = (FlowLayout) bullet_rewards.getLayout();
		flowLayout_15.setVgap(0);
		flowLayout_15.setHgap(0);
		flowLayout_15.setAlignment(FlowLayout.RIGHT);
		bullet_rewards.setBackground(Color.WHITE);
		bullet_rewards.setBorder(new TitledBorder(null, "Belohnungen f\u00FCr Sch\u00FCsse", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		bullet_rewards.setBounds(6, 11, 230, 255);
		reward_system.add(bullet_rewards);
		
		JPanel panel_11 = new JPanel();
		FlowLayout flowLayout_28 = (FlowLayout) panel_11.getLayout();
		flowLayout_28.setVgap(8);
		panel_11.setBackground(Color.WHITE);
		bullet_rewards.add(panel_11);
		
		JLabel lblNewLabel_2 = new JLabel("Von Kugel getroffen:");
		panel_11.add(lblNewLabel_2);
		
		JTextField hitByBulletReward = new JTextField();
		hitByBulletReward.setText(Config.getStringValue("Reward_HitByBullet", "-3.0"));
		hitByBulletReward.setColumns(5);
		panel_11.add(hitByBulletReward);
		
		JPanel panel_19 = new JPanel();
		FlowLayout flowLayout_29 = (FlowLayout) panel_19.getLayout();
		flowLayout_29.setVgap(8);
		panel_19.setBackground(Color.WHITE);
		bullet_rewards.add(panel_19);
		
		JLabel lblKugelTrifftKugel = new JLabel("Kugel trifft Kugel:");
		panel_19.add(lblKugelTrifftKugel);
		
		JTextField bulletHitBulletReward = new JTextField();
		bulletHitBulletReward.setText(Config.getStringValue("Reward_BulletHitBullet", "3.0"));
		bulletHitBulletReward.setColumns(5);
		panel_19.add(bulletHitBulletReward);
		
		JPanel panel_20 = new JPanel();
		FlowLayout flowLayout_27 = (FlowLayout) panel_20.getLayout();
		flowLayout_27.setVgap(8);
		panel_20.setBackground(Color.WHITE);
		bullet_rewards.add(panel_20);
		
		JLabel lblKugelTrifftGegner = new JLabel("Kugel trifft Gegner:");
		panel_20.add(lblKugelTrifftGegner);
		
		JTextField bulletHitEnemyReward = new JTextField();
		bulletHitEnemyReward.setText(Config.getStringValue("Reward_BulletHitEnemy", "3.0"));
		bulletHitEnemyReward.setColumns(5);
		panel_20.add(bulletHitEnemyReward);
		
		JPanel panel_21 = new JPanel();
		FlowLayout flowLayout_24 = (FlowLayout) panel_21.getLayout();
		flowLayout_24.setVgap(8);
		panel_21.setBackground(Color.WHITE);
		bullet_rewards.add(panel_21);
		
		JLabel lblKugelVerfehltGegner = new JLabel("Kugel verfehlt Gegner:");
		panel_21.add(lblKugelVerfehltGegner);
		
		JTextField bulletHitWallReward = new JTextField();
		bulletHitWallReward.setText(Config.getStringValue("Reward_BulletHitWall", "-3.0"));
		bulletHitWallReward.setColumns(5);
		panel_21.add(bulletHitWallReward);
		
		JPanel panel_22 = new JPanel();
		FlowLayout flowLayout_8 = (FlowLayout) panel_22.getLayout();
		flowLayout_8.setVgap(8);
		panel_22.setBackground(Color.WHITE);
		bullet_rewards.add(panel_22);
		
		JCheckBox multBulletPower = new JCheckBox("Kugelpower verrechnen");
		multBulletPower.setSelected(Config.getBoolValue("Reward_MultBulletPower", false));
		multBulletPower.setBackground(Color.WHITE);
		panel_22.add(multBulletPower);
		
		JPanel hit_rewards = new JPanel();
		FlowLayout flowLayout_16 = (FlowLayout) hit_rewards.getLayout();
		flowLayout_16.setVgap(0);
		flowLayout_16.setHgap(0);
		flowLayout_16.setAlignment(FlowLayout.RIGHT);
		hit_rewards.setBackground(Color.WHITE);
		hit_rewards.setBorder(new TitledBorder(null, "Belohnunge f\u00FCr's Rammen", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		hit_rewards.setBounds(242, 11, 224, 145);
		reward_system.add(hit_rewards);
		
		JPanel panel_32 = new JPanel();
		panel_32.setBackground(Color.WHITE);
		hit_rewards.add(panel_32);
		
		JLabel lblVonGegnerGerammt = new JLabel("Von Gegner gerammt:");
		panel_32.add(lblVonGegnerGerammt);
		
		JTextField hitByEnemy = new JTextField();
		hitByEnemy.setText(Config.getStringValue("Reward_HitByEnemy", "1.0"));
		hitByEnemy.setColumns(5);
		panel_32.add(hitByEnemy);
		
		JPanel panel_23 = new JPanel();
		panel_23.setBackground(Color.WHITE);
		hit_rewards.add(panel_23);
		
		JLabel lblGegnerGerammt = new JLabel("Gegner gerammt:");
		panel_23.add(lblGegnerGerammt);
		
		JTextField hitRobotReward = new JTextField();
		hitRobotReward.setText(Config.getStringValue("Reward_HitRobot", "1.0"));
		hitRobotReward.setColumns(5);
		panel_23.add(hitRobotReward);
		
		JPanel panel_24 = new JPanel();
		panel_24.setBackground(Color.WHITE);
		hit_rewards.add(panel_24);
		
		JLabel lblWandGerammt = new JLabel("Wand gerammt:");
		panel_24.add(lblWandGerammt);
		
		JTextField hitWallReward = new JTextField();
		hitWallReward.setText(Config.getStringValue("Reward_HitWall", "-5.0"));
		hitWallReward.setColumns(5);
		panel_24.add(hitWallReward);
		
		JPanel victory_rewards = new JPanel();
		FlowLayout flowLayout_17 = (FlowLayout) victory_rewards.getLayout();
		flowLayout_17.setAlignment(FlowLayout.RIGHT);
		flowLayout_17.setVgap(0);
		flowLayout_17.setHgap(0);
		victory_rewards.setBorder(new TitledBorder(null, "Belohnunge bei Rundenende", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		victory_rewards.setBackground(Color.WHITE);
		victory_rewards.setBounds(242, 161, 224, 105);
		reward_system.add(victory_rewards);
		
		JPanel panel_26 = new JPanel();
		panel_26.setBackground(Color.WHITE);
		victory_rewards.add(panel_26);
		
		JLabel lblRundeGewonnen = new JLabel("Runde gewonnen:");
		panel_26.add(lblRundeGewonnen);
		
		JTextField winningReward = new JTextField();
		winningReward.setText(Config.getStringValue("Reward_Winning", "10.0"));
		winningReward.setColumns(5);
		panel_26.add(winningReward);
		
		JPanel panel_27 = new JPanel();
		panel_27.setBackground(Color.WHITE);
		victory_rewards.add(panel_27);
		
		JLabel lblRundeVerloren = new JLabel("Runde verloren:");
		panel_27.add(lblRundeVerloren);
		
		JTextField loosingReward = new JTextField();
		loosingReward.setText(Config.getStringValue("Reward_Loosing", "-10.0"));
		loosingReward.setColumns(5);
		panel_27.add(loosingReward);
		
		JPanel robocode_panel = new JPanel();
		robocode_panel.setBackground(Color.WHITE);
		tabbedPane.addTab("Robocode", null, robocode_panel, null);
		tabbedPane.setEnabledAt(3, true);
		robocode_panel.setLayout(null);

		JPanel settings_panel = new JPanel();
		settings_panel.setBackground(Color.WHITE);
		settings_panel.setBounds(7, 7, 220, 109);
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
		rounds.setModel(new SpinnerNumberModel(Config.getIntValue("Rounds", 10), 10, 100000000, 10));
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
		enemy_panel.setBounds(225, 7, 241, 278);
		FlowLayout fl_enemy_panel = (FlowLayout) enemy_panel.getLayout();
		fl_enemy_panel.setAlignment(FlowLayout.LEFT);
		fl_enemy_panel.setVgap(0);
		fl_enemy_panel.setHgap(0);
		robocode_panel.add(enemy_panel);

		JList<String> robot_list = new JList<String>();
		robot_list.setVisibleRowCount(12);
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
		panel_12.setBounds(7, 128, 220, 70);
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
		
		processPanel = new JPanel();
		processPanel.setBackground(Color.WHITE);
		tabbedPane.addTab("Prozesse", null, processPanel, null);
		tabbedPane.setEnabledAt(4, false);
		processPanel.setLayout(null);
		
		processSelectionPanel = new JPanel();
		processSelectionPanel.setBounds(6, 6, 129, 285);
		processPanel.add(processSelectionPanel);
		processSelectionPanel.setBackground(Color.WHITE);
		processSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		processSelectionBtnGrp = new ButtonGroup();

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
				String algo = "";
				for (Enumeration<AbstractButton> buttons = algorithmButtonGroup.getElements(); buttons.hasMoreElements();) {
					AbstractButton b = buttons.nextElement();
					if (b.isSelected()) {
						algo = b.getText();
						break;
					}
				}
				
				Config.setBoolValue("Agent_LoadOnStart", loadOnStart.isSelected());
				Config.setIntValue("Agent_SaveTimes", (int)saveTimes.getValue());
				Config.setStringValue("Agent_Algorithm", algo);
				Config.setIntValue("Agent_SuccesChance", (int)succesChance.getValue());
				switch (algo) {
					case "Q-Learning":
						Config.setIntValue("Agent_DiscountRate", (int)discountRateQ.getValue());
						Config.setIntValue("Agent_LearnRate", (int)learnRateQ.getValue());
						break;
					case "SARSA-Lambda":
						Config.setIntValue("Agent_QueueSize", (int)propagationDepth.getValue());
						Config.setIntValue("Agent_Lambda", (int)lambda.getValue());
						Config.setIntValue("Agent_DiscountRate", (int)discountRate.getValue());
						Config.setIntValue("Agent_LearnRate", (int)learnRate.getValue());
						break;
				}
				
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
				
				Config.setDoubleValue("Reward_HitByBullet", Double.parseDouble(hitByBulletReward.getText()));
				Config.setDoubleValue("Reward_BulletHitBullet", Double.parseDouble(bulletHitBulletReward.getText()));
				Config.setDoubleValue("Reward_BulletHitEnemy", Double.parseDouble(bulletHitEnemyReward.getText()));
				Config.setDoubleValue("Reward_BulletHitWall", Double.parseDouble(bulletHitWallReward.getText()));
				Config.setBoolValue("Reward_MultBulletPower", multBulletPower.isSelected());
				
				Config.setDoubleValue("Reward_HitByEnemy", Double.parseDouble(hitByEnemy.getText()));
				Config.setDoubleValue("Reward_HitRobot", Double.parseDouble(hitRobotReward.getText()));
				Config.setDoubleValue("Reward_HitWall", Double.parseDouble(hitWallReward.getText()));
				
				Config.setDoubleValue("Reward_Winning", Double.parseDouble(winningReward.getText()));
				Config.setDoubleValue("Reward_Loosing", Double.parseDouble(loosingReward.getText()));

				start();
			}
		});
		button_panel.add(btnStart);
		
		extendedMoveEnv.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				moveGridSize.setEnabled(extendedMoveEnv.isSelected());
			}
		});
		
		simpleReward.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				tabbedPane.setEnabledAt(2, simpleReward.isSelected());
			}
		});
		
		simpleReward.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				tabbedPane.setEnabledAt(2, simpleReward.isSelected());
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
		classpath.add(new File(".").getAbsolutePath());
		File libs = new File(".\\libs");
		if (libs.exists() && libs.isDirectory()) {
			for (File f : libs.listFiles()) {
				classpath.add(f.getAbsolutePath());
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
		
		ArrayList<String> commands = new ArrayList<>();
		commands.add("java");
		commands.addAll(Arrays.asList(vmArgs.split(" ")));
		commands.add("-cp");
		commands.add(String.join(";", classpath));
		commands.add("robocode.Robocode");
		commands.addAll(Arrays.asList(robocodeArgs.split(" ")));
		
		ProcessBuilder pBuilder = new ProcessBuilder(commands);
		pBuilder.directory(new File(rHome));
		pBuilder.redirectOutput(Redirect.PIPE);
		pBuilder.redirectError(Redirect.PIPE);
		
		try {
			Process rcp = pBuilder.start();
			
			robocodeProcesses.add(rcp);
			tabbedPane.setEnabledAt(4, true);
			
			createObjectsForNewProcess(Config.getStringValue("EnemyRobot"), rcp);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void createObjectsForNewProcess(String radioBtnName, Process rcp) {
		int index = processSelectionBtnGrp.getButtonCount();
		
		// TextContainer für die Ausgabe erstellen	
		JPanel rightPanel = new JPanel();
		rightPanel.setBackground(Color.WHITE);
		rightPanel.setBounds(147, 6, 319, 279);
		rightPanel.setName("panel_" + index);
		processPanel.add(rightPanel);
		rightPanel.setLayout(new BorderLayout(0, 0));
		
		JTextArea loggingPanel = new JTextArea();
		loggingPanel.setWrapStyleWord(true);
		loggingPanel.setLineWrap(true);
		loggingPanel.setEditable(false);
		loggingPanel.setBackground(Color.WHITE);
		
		JScrollPane scrollPane = new JScrollPane(loggingPanel);
		rightPanel.add(scrollPane, BorderLayout.CENTER);
		
		JPanel roundsPanel = new JPanel();
		FlowLayout flowLayout_30 = (FlowLayout) roundsPanel.getLayout();
		flowLayout_30.setVgap(2);
		flowLayout_30.setAlignment(FlowLayout.LEFT);
		roundsPanel.setBackground(Color.WHITE);
		rightPanel.add(roundsPanel, BorderLayout.NORTH);
		
		roundsPanel.add(new JLabel("Runde"));
		
		JLabel roundLabel = new JLabel("0");
		roundsPanel.add(roundLabel);
		
		// RadioButton erstellen
		JRadioButton rdbtn = new JRadioButton(radioBtnName);
		rdbtn.setName("btn_" + index);
		processSelectionBtnGrp.add(rdbtn);
		processSelectionPanel.add(rdbtn);
		
		rdbtn.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JRadioButton btn = (JRadioButton)e.getSource();
				
				if (!btn.isSelected())
					return;
				
				String name = "panel" + btn.getName().substring(btn.getName().indexOf('_'));				
				
				for (Component c : processPanel.getComponents()) {
					if (c instanceof JPanel && c.getName() != null && c.getName().startsWith("panel")) {
						if (c.getName().equals(name)) {
							c.setVisible(true);
						} else {
							c.setVisible(false);
						}
					}
				}
			}
		});
		
		rdbtn.doClick();
		
		// Output redirekten
		redirectConsole.redirectOutput(loggingPanel, roundLabel, rcp.getInputStream(), rcp.getErrorStream(), index);
	}
	
	private void removeObjectsForProcess(int index) {
		boolean wasSelected = false;
		
		for (Component c : processPanel.getComponents()) {
			if (c instanceof JPanel && c.getName() != null && c.getName().equals("panel_" + index)) {
				processPanel.remove(c);
				break;
			}
		}
		
		Enumeration<AbstractButton> btns = processSelectionBtnGrp.getElements();
		AbstractButton btn;
		while (btns.hasMoreElements()) {
			btn = btns.nextElement();
			if (btn.getName() != null && btn.getName().equals("btn_" + index)) {
				processSelectionBtnGrp.remove(btn);
				if (btn.isSelected()) {
					wasSelected = true;
				}
				
				break;
			}
		}
		
		for (Component c : processSelectionPanel.getComponents()) {
			if (c instanceof JRadioButton && c.getName() != null && c.getName().equals("btn_" + index)) {				
				processSelectionPanel.remove(c);
				break;
			}
		}
		
		if (wasSelected) {
			btns = processSelectionBtnGrp.getElements();
			if (btns.hasMoreElements())
				btns.nextElement().doClick();
		}
	}
}
