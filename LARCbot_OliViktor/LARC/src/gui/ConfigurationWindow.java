package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import utils.Config;
import agents.AgentMode;
import environment.EnvironmentBuilder.AttackEnvironments;
import environment.EnvironmentBuilder.MoveEnvironments;

public class ConfigurationWindow {

	private ArrayList<Process> robocodeProcesses = new ArrayList<Process>();
	private JTabbedPane tabbedPane, tabPanelOliViktor;
	private JFrame frmLarcbotExperimentKonfigurator;
	private JFileChooser folder_fc, file_fc;
	private DefaultListModel<String> robot_listModel;
	private JTextField robocodeHome, agentSaveFile, agentLoadFile;
	private JPanel processPanel;
	private JPanel processSelectionPanel;
	private ButtonGroup processSelectionBtnGrp;

	private JComboBox<AgentMode> agentModeChoose;
	private JPanel sarsaLambda, qLearning;
	private JSpinner successChance;

	private Console redirectConsole;

	private static class ConfiguratedFileFilter extends FileFilter {
		private static String acceptedType;

		@Override
		public String getDescription() {
			return "*." + acceptedType;
		}

		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			return f.getName().endsWith("." + acceptedType);
		}

		public static void setAcceptedFileType(String fileType) {
			acceptedType = fileType;
		}
	};

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
		folder_fc = new JFileChooser(Config.getStringValue("RobocodeHome"));
		folder_fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		file_fc = new JFileChooser();
		file_fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		file_fc.setFileFilter(new ConfiguratedFileFilter());

		frmLarcbotExperimentKonfigurator = new JFrame();
		frmLarcbotExperimentKonfigurator.getContentPane().setBackground(
				Color.WHITE);
		frmLarcbotExperimentKonfigurator.setBackground(Color.WHITE);
		frmLarcbotExperimentKonfigurator
				.setTitle("LARCBot Experiment Konfigurator");
		frmLarcbotExperimentKonfigurator.setResizable(false);
		frmLarcbotExperimentKonfigurator.setBounds(100, 100, 484, 406);
		frmLarcbotExperimentKonfigurator
				.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLarcbotExperimentKonfigurator.getContentPane().setLayout(
				new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.WHITE);
		frmLarcbotExperimentKonfigurator.getContentPane().add(tabbedPane);

		JPanel panel_oli_viktor = new JPanel();
		panel_oli_viktor.setBackground(Color.WHITE);
		tabbedPane.addTab("Oli & Viktor", null, panel_oli_viktor, null);
		tabbedPane.setEnabledAt(0, true);
		panel_oli_viktor.setLayout(new BorderLayout(0, 0));

		tabPanelOliViktor = new JTabbedPane(JTabbedPane.TOP);
		tabPanelOliViktor.setBackground(Color.WHITE);
		panel_oli_viktor.add(tabPanelOliViktor);

		JPanel bot_panel = new JPanel();
		bot_panel.setBackground(Color.WHITE);
		tabPanelOliViktor.addTab("LARCBot", null, bot_panel, null);
		bot_panel.setLayout(null);

		JPanel agent_panel = new JPanel();
		agent_panel.setBounds(6, 98, 231, 180);
		bot_panel.add(agent_panel);
		agent_panel.setBackground(Color.WHITE);
		agent_panel.setBorder(new TitledBorder(null, "Agenten",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
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

		JPanel agentLoadFilePanel = new JPanel();
		FlowLayout flowLayout_30 = (FlowLayout) agentLoadFilePanel.getLayout();
		flowLayout_30.setHgap(15);
		flowLayout_30.setVgap(2);
		flowLayout_30.setAlignment(FlowLayout.LEFT);
		agentLoadFilePanel.setBackground(Color.WHITE);
		agent_panel.add(agentLoadFilePanel);

		agentLoadFile = new JTextField();
		agentLoadFile.setText(Config.getStringValue("Agent_LoadFile"));
		agentLoadFilePanel.add(agentLoadFile);
		agentLoadFile.setColumns(10);

		JButton btnNewButton = new JButton("...");
		btnNewButton.setBackground(Color.WHITE);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfiguratedFileFilter.setAcceptedFileType("zip");
				file_fc.setCurrentDirectory(new File(Config
						.getStringValue("Agent_LoadFile")).getParentFile());
				int res = file_fc
						.showOpenDialog(frmLarcbotExperimentKonfigurator);

				if (res == JFileChooser.APPROVE_OPTION) {
					String filename = file_fc.getSelectedFile().getPath();
					if (!filename.endsWith(".zip"))
						filename += ".zip";
					agentLoadFile.setText(filename);
				}
			}
		});
		agentLoadFilePanel.add(btnNewButton);

		loadOnStart.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				enableComponents(agentLoadFilePanel, loadOnStart.isSelected());
			}
		});
		enableComponents(agentLoadFilePanel, loadOnStart.isSelected());

		JPanel panel_34 = new JPanel();
		panel_34.setBackground(Color.WHITE);
		agent_panel.add(panel_34);
		FlowLayout flowLayout_34 = (FlowLayout) panel_34.getLayout();
		flowLayout_34.setHgap(3);
		flowLayout_34.setAlignment(FlowLayout.LEFT);

		JCheckBox saveAgents = new JCheckBox("Letzten Stand speichern");
		saveAgents.setBackground(Color.WHITE);
		saveAgents.setSelected(Config.getBoolValue("Agent_SaveAgents"));
		panel_34.add(saveAgents);

		JPanel agengtSaveFilePanel = new JPanel();
		FlowLayout flowLayout_35 = (FlowLayout) agengtSaveFilePanel.getLayout();
		flowLayout_35.setHgap(15);
		flowLayout_35.setVgap(2);
		flowLayout_35.setAlignment(FlowLayout.LEFT);
		agengtSaveFilePanel.setBackground(Color.WHITE);
		agent_panel.add(agengtSaveFilePanel);

		agentSaveFile = new JTextField();
		agentSaveFile.setText(Config.getStringValue("Agent_SaveFile"));
		agengtSaveFilePanel.add(agentSaveFile);
		agentSaveFile.setColumns(10);

		JButton btnNewButton2 = new JButton("...");
		btnNewButton2.setBackground(Color.WHITE);
		btnNewButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfiguratedFileFilter.setAcceptedFileType("zip");
				file_fc.setCurrentDirectory(new File(Config
						.getStringValue("Agent_SaveFile")).getParentFile());
				int res = file_fc
						.showOpenDialog(frmLarcbotExperimentKonfigurator);

				if (res == JFileChooser.APPROVE_OPTION) {
					String filename = file_fc.getSelectedFile().getPath();
					if (!filename.endsWith(".zip"))
						filename += ".zip";
					agentSaveFile.setText(filename);
				}
			}
		});
		agengtSaveFilePanel.add(btnNewButton2);

		saveAgents.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				enableComponents(agengtSaveFilePanel, saveAgents.isSelected());
			}
		});
		enableComponents(agengtSaveFilePanel, saveAgents.isSelected());

		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.WHITE);
		agent_panel.add(panel_3);
		panel_3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 3));

		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout_5 = (FlowLayout) panel_5.getLayout();
		flowLayout_5.setHgap(20);
		panel_5.setBackground(Color.WHITE);
		agent_panel.add(panel_5);

		JPanel robot_panel = new JPanel();
		robot_panel.setBounds(6, 6, 460, 92);
		bot_panel.add(robot_panel);
		robot_panel.setBackground(Color.WHITE);
		robot_panel.setBorder(new TitledBorder(null, "Roboter",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		robot_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		JPanel panel_17 = new JPanel();
		robot_panel.add(panel_17);
		FlowLayout flowLayout_13 = (FlowLayout) panel_17.getLayout();
		flowLayout_13.setVgap(3);
		panel_17.setBackground(Color.WHITE);

		JLabel lblAngriffumwelt = new JLabel("Angriffumwelt-Typ:");
		panel_17.add(lblAngriffumwelt);

		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.WHITE);
		FlowLayout flowLayout = (FlowLayout) panel_4.getLayout();
		flowLayout.setHgap(32);
		flowLayout.setVgap(0);
		panel_17.add(panel_4);

		JComboBox<AttackEnvironments> attackEnvChoose = new JComboBox<AttackEnvironments>();
		attackEnvChoose.setModel(new DefaultComboBoxModel<AttackEnvironments>(
				AttackEnvironments.values()));
		attackEnvChoose.setSelectedItem(AttackEnvironments.values()[Config
				.getIntValue("Robot_AttackEnv")]);
		panel_4.add(attackEnvChoose);

		JPanel panel_18 = new JPanel();
		robot_panel.add(panel_18);
		FlowLayout flowLayout_14 = (FlowLayout) panel_18.getLayout();
		flowLayout_14.setVgap(3);
		panel_18.setBackground(Color.WHITE);

		JLabel lblBewegungsumwelt = new JLabel("Bewegungsumwelt-Typ:");
		panel_18.add(lblBewegungsumwelt);

		JComboBox<MoveEnvironments> moveEnvChoose = new JComboBox<MoveEnvironments>();
		moveEnvChoose.setModel(new DefaultComboBoxModel<MoveEnvironments>(
				MoveEnvironments.values()));
		moveEnvChoose.setSelectedItem(MoveEnvironments.values()[Config
				.getIntValue("Robot_MoveEnv")]);

		panel_18.add(moveEnvChoose);

		JPanel env_panel = new JPanel();
		env_panel.setBounds(241, 98, 225, 180);
		bot_panel.add(env_panel);
		env_panel.setBackground(Color.WHITE);
		env_panel.setBorder(new TitledBorder(null, "Umwelt",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
		moveGridSize.setModel(new SpinnerNumberModel(Config.getIntValue(
				"Env_GridSize", 40), null, null, new Integer(1)));
		moveGridSize.setBackground(Color.WHITE);
		moveGridSize
				.setEnabled(Config.getIntValue("Robot_MoveEnv") == MoveEnvironments.COMPLEX_MOVE
						.ordinal());
		moveGridSize_panel.add(moveGridSize);

		JLabel lblNewLabel_1 = new JLabel("Bewegungsgittergröße");
		moveGridSize_panel.add(lblNewLabel_1);

		JPanel algorithm_panel = new JPanel();
		tabPanelOliViktor.addTab("Algorithmus", null, algorithm_panel, null);
		algorithm_panel.setBackground(Color.WHITE);
		algorithm_panel.setLayout(null);

		JPanel algorithmGeneralPanel = new JPanel();
		FlowLayout flowLayout_32 = (FlowLayout) algorithmGeneralPanel
				.getLayout();
		flowLayout_32.setVgap(0);
		flowLayout_32.setHgap(0);
		flowLayout_32.setAlignment(FlowLayout.LEFT);
		algorithmGeneralPanel.setBorder(new TitledBorder(null, "Allgemein",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		algorithmGeneralPanel.setBackground(Color.WHITE);
		algorithmGeneralPanel.setBounds(6, 6, 460, 98);
		algorithm_panel.add(algorithmGeneralPanel);

		JPanel panel_31 = new JPanel();
		FlowLayout flowLayout_18 = (FlowLayout) panel_31.getLayout();
		flowLayout_18.setVgap(3);
		panel_31.setBackground(Color.WHITE);
		algorithmGeneralPanel.add(panel_31);

		JLabel lblAgentenModus = new JLabel("Agenten Modus:");
		panel_31.add(lblAgentenModus);

		agentModeChoose = new JComboBox<AgentMode>();
		agentModeChoose.setModel(new DefaultComboBoxModel<AgentMode>(AgentMode
				.values()));
		agentModeChoose.setSelectedItem(AgentMode.values()[Config
				.getIntValue("Agent_Mode")]);
		agentModeChoose.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				agentModeChooseChanged();
			}
		});
		panel_31.add(agentModeChoose);

		JPanel panel_7 = new JPanel();
		FlowLayout flowLayout_31 = (FlowLayout) panel_7.getLayout();
		flowLayout_31.setVgap(3);
		algorithmGeneralPanel.add(panel_7);
		panel_7.setBackground(Color.WHITE);

		JLabel lblErfolgsrate = new JLabel("Erfolgsrate (%):");
		panel_7.add(lblErfolgsrate);

		successChance = new JSpinner();
		panel_7.add(successChance);
		successChance.setModel(new SpinnerNumberModel(Config.getIntValue(
				"Agent_SuccesChance", 50), 50, 100, 1));

		sarsaLambda = new JPanel();
		FlowLayout flowLayout_21 = (FlowLayout) sarsaLambda.getLayout();
		flowLayout_21.setVgap(0);
		flowLayout_21.setHgap(0);
		flowLayout_21.setAlignment(FlowLayout.RIGHT);
		sarsaLambda.setBorder(new TitledBorder(null, "SARSA-Lambda",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sarsaLambda.setBackground(Color.WHITE);
		sarsaLambda.setBounds(6, 105, 224, 180);
		algorithm_panel.add(sarsaLambda);

		JPanel panel_13 = new JPanel();
		FlowLayout flowLayout_19 = (FlowLayout) panel_13.getLayout();
		flowLayout_19.setVgap(4);
		panel_13.setBackground(Color.WHITE);
		sarsaLambda.add(panel_13);

		JLabel lblPropagationtiefe = new JLabel("Propagationstiefe:");
		panel_13.add(lblPropagationtiefe);

		JSpinner propagationDepth = new JSpinner();
		propagationDepth.setModel(new SpinnerNumberModel(Config.getIntValue(
				"Agent_QueueSize", 1), 1, 100, 1));
		propagationDepth.setBackground(Color.WHITE);
		panel_13.add(propagationDepth);

		JPanel panel_16 = new JPanel();
		sarsaLambda.add(panel_16, BorderLayout.NORTH);
		FlowLayout flowLayout_11 = (FlowLayout) panel_16.getLayout();
		flowLayout_11.setVgap(4);
		flowLayout_11.setAlignment(FlowLayout.LEFT);
		panel_16.setBackground(Color.WHITE);

		JLabel lblLambda = new JLabel("Lambda (%):");
		lblLambda.setBackground(Color.WHITE);
		panel_16.add(lblLambda);

		JSpinner lambda = new JSpinner();
		lambda.setModel(new SpinnerNumberModel(Config
				.getIntValue("Agent_Lambda"), 0, 100, 10));
		lambda.setBackground(Color.WHITE);
		panel_16.add(lambda);

		JPanel panel_14 = new JPanel();
		sarsaLambda.add(panel_14, BorderLayout.CENTER);
		FlowLayout flowLayout_9 = (FlowLayout) panel_14.getLayout();
		flowLayout_9.setVgap(4);
		flowLayout_9.setAlignment(FlowLayout.LEFT);
		panel_14.setBackground(Color.WHITE);

		JLabel lblLernrate = new JLabel("Lernrate (%):");
		lblLernrate.setBackground(Color.WHITE);
		panel_14.add(lblLernrate);

		JSpinner learnRate = new JSpinner();
		learnRate.setModel(new SpinnerNumberModel(Config
				.getIntValue("Agent_LearnRate"), 0, 100, 10));
		learnRate.setBackground(Color.WHITE);
		panel_14.add(learnRate);

		JPanel panel_15 = new JPanel();
		sarsaLambda.add(panel_15, BorderLayout.SOUTH);
		FlowLayout flowLayout_10 = (FlowLayout) panel_15.getLayout();
		flowLayout_10.setVgap(4);
		flowLayout_10.setAlignment(FlowLayout.LEFT);
		panel_15.setBackground(Color.WHITE);

		JLabel lblDiscountrate = new JLabel("Discount-Rate (%):");
		lblDiscountrate.setBackground(Color.WHITE);
		panel_15.add(lblDiscountrate);

		JSpinner discountRate = new JSpinner();
		discountRate.setModel(new SpinnerNumberModel(Config
				.getIntValue("Agent_DiscountRate"), 0, 100, 10));
		discountRate.setBackground(Color.WHITE);
		panel_15.add(discountRate);

		qLearning = new JPanel();
		qLearning.setBackground(Color.WHITE);
		qLearning.setBorder(new TitledBorder(null, "Q-Learning",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		FlowLayout flowLayout_22 = (FlowLayout) qLearning.getLayout();
		flowLayout_22.setAlignment(FlowLayout.RIGHT);
		flowLayout_22.setVgap(0);
		flowLayout_22.setHgap(0);
		qLearning.setBounds(235, 110, 231, 175);
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
		learnRateQ.setModel(new SpinnerNumberModel(Config
				.getIntValue("Agent_LearnRate"), 0, 100, 10));
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
		discountRateQ.setModel(new SpinnerNumberModel(Config
				.getIntValue("Agent_DiscountRate"), 0, 100, 10));
		discountRateQ.setBackground(Color.WHITE);
		panel_30.add(discountRateQ);

		JPanel reward_system = new JPanel();
		tabPanelOliViktor.addTab("Reward", null, reward_system, null);
		reward_system.setBackground(Color.WHITE);
		reward_system.setLayout(null);

		JPanel bullet_rewards = new JPanel();
		FlowLayout flowLayout_15 = (FlowLayout) bullet_rewards.getLayout();
		flowLayout_15.setVgap(0);
		flowLayout_15.setHgap(0);
		flowLayout_15.setAlignment(FlowLayout.RIGHT);
		bullet_rewards.setBackground(Color.WHITE);
		bullet_rewards.setBorder(new TitledBorder(null,
				"Belohnungen f\u00FCr Sch\u00FCsse", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		bullet_rewards.setBounds(6, 11, 230, 255);
		reward_system.add(bullet_rewards);

		JPanel panel_11 = new JPanel();
		FlowLayout flowLayout_28 = (FlowLayout) panel_11.getLayout();
		flowLayout_28.setVgap(8);
		panel_11.setBackground(Color.WHITE);
		bullet_rewards.add(panel_11);

		JLabel lblNewLabel_2 = new JLabel("Von Kugel getroffen:");
		panel_11.add(lblNewLabel_2);

		JTextField ov_hitByBulletReward = new JTextField();
		ov_hitByBulletReward.setText(Config.getStringValue("Reward_HitByBullet",
				"-3.0"));
		ov_hitByBulletReward.setColumns(5);
		panel_11.add(ov_hitByBulletReward);

		JPanel panel_19 = new JPanel();
		FlowLayout flowLayout_29 = (FlowLayout) panel_19.getLayout();
		flowLayout_29.setVgap(8);
		panel_19.setBackground(Color.WHITE);
		bullet_rewards.add(panel_19);

		JLabel lblKugelTrifftKugel = new JLabel("Kugel trifft Kugel:");
		panel_19.add(lblKugelTrifftKugel);

		JTextField ov_bulletHitBulletReward = new JTextField();
		ov_bulletHitBulletReward.setText(Config.getStringValue(
				"Reward_BulletHitBullet", "3.0"));
		ov_bulletHitBulletReward.setColumns(5);
		panel_19.add(ov_bulletHitBulletReward);

		JPanel panel_20 = new JPanel();
		FlowLayout flowLayout_27 = (FlowLayout) panel_20.getLayout();
		flowLayout_27.setVgap(8);
		panel_20.setBackground(Color.WHITE);
		bullet_rewards.add(panel_20);

		JLabel lblKugelTrifftGegner = new JLabel("Kugel trifft Gegner:");
		panel_20.add(lblKugelTrifftGegner);

		JTextField ov_bulletHitEnemyReward = new JTextField();
		ov_bulletHitEnemyReward.setText(Config.getStringValue(
				"Reward_BulletHitEnemy", "3.0"));
		ov_bulletHitEnemyReward.setColumns(5);
		panel_20.add(ov_bulletHitEnemyReward);

		JPanel panel_21 = new JPanel();
		FlowLayout flowLayout_24 = (FlowLayout) panel_21.getLayout();
		flowLayout_24.setVgap(8);
		panel_21.setBackground(Color.WHITE);
		bullet_rewards.add(panel_21);

		JLabel lblKugelVerfehltGegner = new JLabel("Kugel verfehlt Gegner:");
		panel_21.add(lblKugelVerfehltGegner);

		JTextField ov_bulletHitWallReward = new JTextField();
		ov_bulletHitWallReward.setText(Config.getStringValue(
				"Reward_BulletHitWall", "-3.0"));
		ov_bulletHitWallReward.setColumns(5);
		panel_21.add(ov_bulletHitWallReward);

		JPanel panel_22 = new JPanel();
		FlowLayout flowLayout_8 = (FlowLayout) panel_22.getLayout();
		flowLayout_8.setVgap(8);
		panel_22.setBackground(Color.WHITE);
		bullet_rewards.add(panel_22);

		JCheckBox ov_multBulletPower = new JCheckBox("Kugelpower verrechnen");
		ov_multBulletPower.setSelected(Config.getBoolValue(
				"Reward_MultBulletPower", false));
		ov_multBulletPower.setBackground(Color.WHITE);
		panel_22.add(ov_multBulletPower);

		JPanel hit_rewards = new JPanel();
		FlowLayout flowLayout_16 = (FlowLayout) hit_rewards.getLayout();
		flowLayout_16.setVgap(0);
		flowLayout_16.setHgap(0);
		flowLayout_16.setAlignment(FlowLayout.RIGHT);
		hit_rewards.setBackground(Color.WHITE);
		hit_rewards.setBorder(new TitledBorder(null,
				"Belohnungen f\u00FCr's Rammen", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		hit_rewards.setBounds(242, 11, 224, 145);
		reward_system.add(hit_rewards);

		JPanel panel_32 = new JPanel();
		panel_32.setBackground(Color.WHITE);
		hit_rewards.add(panel_32);

		JLabel lblVonGegnerGerammt = new JLabel("Von Gegner gerammt:");
		panel_32.add(lblVonGegnerGerammt);

		JTextField ov_hitByEnemy = new JTextField();
		ov_hitByEnemy.setText(Config.getStringValue("Reward_HitByEnemy", "1.0"));
		ov_hitByEnemy.setColumns(5);
		panel_32.add(ov_hitByEnemy);

		JPanel panel_23 = new JPanel();
		panel_23.setBackground(Color.WHITE);
		hit_rewards.add(panel_23);

		JLabel lblGegnerGerammt = new JLabel("Gegner gerammt:");
		panel_23.add(lblGegnerGerammt);

		JTextField ov_hitRobotReward = new JTextField();
		ov_hitRobotReward.setText(Config.getStringValue("Reward_HitRobot", "1.0"));
		ov_hitRobotReward.setColumns(5);
		panel_23.add(ov_hitRobotReward);

		JPanel panel_24 = new JPanel();
		panel_24.setBackground(Color.WHITE);
		hit_rewards.add(panel_24);

		JLabel lblWandGerammt = new JLabel("Wand gerammt:");
		panel_24.add(lblWandGerammt);

		JTextField ov_hitWallReward = new JTextField();
		ov_hitWallReward.setText(Config.getStringValue("Reward_HitWall", "-5.0"));
		ov_hitWallReward.setColumns(5);
		panel_24.add(ov_hitWallReward);

		JPanel victory_rewards = new JPanel();
		FlowLayout flowLayout_17 = (FlowLayout) victory_rewards.getLayout();
		flowLayout_17.setAlignment(FlowLayout.RIGHT);
		flowLayout_17.setVgap(0);
		flowLayout_17.setHgap(0);
		victory_rewards.setBorder(new TitledBorder(null,
				"Belohnungen bei Rundenende", TitledBorder.LEADING,
				TitledBorder.TOP, null, Color.BLACK));
		victory_rewards.setBackground(Color.WHITE);
		victory_rewards.setBounds(242, 161, 224, 105);
		reward_system.add(victory_rewards);

		JPanel panel_26 = new JPanel();
		panel_26.setBackground(Color.WHITE);
		victory_rewards.add(panel_26);

		JLabel lblRundeGewonnen = new JLabel("Runde gewonnen:");
		panel_26.add(lblRundeGewonnen);

		JTextField ov_winningReward = new JTextField();
		ov_winningReward.setText(Config.getStringValue("Reward_Winning", "10.0"));
		ov_winningReward.setColumns(5);
		panel_26.add(ov_winningReward);

		JPanel panel_27 = new JPanel();
		panel_27.setBackground(Color.WHITE);
		victory_rewards.add(panel_27);

		JLabel lblRundeVerloren = new JLabel("Runde verloren:");
		panel_27.add(lblRundeVerloren);

		JTextField ov_loosingReward = new JTextField();
		ov_loosingReward.setText(Config.getStringValue("Reward_Loosing", "-10.0"));
		ov_loosingReward.setColumns(5);
		panel_27.add(ov_loosingReward);

		moveEnvChoose.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if ((MoveEnvironments) moveEnvChoose.getSelectedItem() == MoveEnvironments.COMPLEX_MOVE) {
					moveGridSize.setEnabled(true);
				} else {
					moveGridSize.setEnabled(false);
				}
			}
		});

		/* DRITTER TAB */

		JPanel panel_alex_daniel = new JPanel();
		panel_alex_daniel.setBackground(Color.WHITE);
		tabbedPane.addTab("Alex & Daniel", null, panel_alex_daniel, null);
		panel_alex_daniel.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabPanelAlexDaniel = new JTabbedPane(JTabbedPane.TOP);
		panel_alex_daniel.add(tabPanelAlexDaniel);

		JPanel ad_agent_panel = new JPanel();
		ad_agent_panel.setBackground(Color.WHITE);
		tabPanelAlexDaniel.addTab("LARCAgent", null, ad_agent_panel, null);
		ad_agent_panel.setLayout(null);

		JPanel ad_sarsaLambda = new JPanel();
		FlowLayout ad_flowLayout_21 = (FlowLayout) ad_sarsaLambda.getLayout();
		ad_flowLayout_21.setVgap(0);
		ad_flowLayout_21.setHgap(0);
		ad_flowLayout_21.setAlignment(FlowLayout.RIGHT);
		ad_sarsaLambda.setBorder(new TitledBorder(null, "SARSA-Lambda",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		ad_sarsaLambda.setBackground(Color.WHITE);
		ad_sarsaLambda.setBounds(248, 6, 224, 207);
		ad_agent_panel.add(ad_sarsaLambda);
		
		JPanel panel_38 = new JPanel();
		ad_sarsaLambda.add(panel_38);
		panel_38.setBackground(Color.WHITE);
		
		JLabel lblExplorationsrate = new JLabel("Explorationsrate (%):");
		lblExplorationsrate.setBackground(Color.WHITE);
		panel_38.add(lblExplorationsrate);
		
		JSpinner ad_explorationRate = new JSpinner();
		ad_explorationRate.setModel(new SpinnerNumberModel(Config.getIntValue(
				"AD_Agent_ExplorationRate", 0), 0, 100, 1));
		ad_explorationRate.setBackground(Color.WHITE);
		panel_38.add(ad_explorationRate);

		JPanel ad_panel_13 = new JPanel();
		FlowLayout ad_flowLayout_19 = (FlowLayout) ad_panel_13.getLayout();
		ad_flowLayout_19.setVgap(4);
		ad_panel_13.setBackground(Color.WHITE);
		ad_sarsaLambda.add(ad_panel_13);

		JLabel ad_lblPropagationtiefe = new JLabel("Propagationstiefe:");
		ad_panel_13.add(ad_lblPropagationtiefe);

		JSpinner ad_propagationDepth = new JSpinner();
		ad_propagationDepth.setModel(new SpinnerNumberModel(Config.getIntValue(
				"AD_Agent_ListCapacity", 1), 1, 100, 1));
		ad_propagationDepth.setBackground(Color.WHITE);
		ad_panel_13.add(ad_propagationDepth);

		JPanel ad_panel_16 = new JPanel();
		ad_sarsaLambda.add(ad_panel_16, BorderLayout.NORTH);
		FlowLayout ad_flowLayout_11 = (FlowLayout) ad_panel_16.getLayout();
		ad_flowLayout_11.setVgap(4);
		ad_flowLayout_11.setAlignment(FlowLayout.LEFT);
		ad_panel_16.setBackground(Color.WHITE);

		JLabel ad_lblLambda = new JLabel("Lambda (%):");
		ad_lblLambda.setBackground(Color.WHITE);
		ad_panel_16.add(ad_lblLambda);

		JSpinner ad_lambda = new JSpinner();
		ad_lambda.setModel(new SpinnerNumberModel(Config
				.getIntValue("AD_Agent_Lambda"), 0, 100, 10));
		ad_lambda.setBackground(Color.WHITE);
		ad_panel_16.add(ad_lambda);

		JPanel ad_panel_14 = new JPanel();
		ad_sarsaLambda.add(ad_panel_14, BorderLayout.CENTER);
		FlowLayout ad_flowLayout_9 = (FlowLayout) ad_panel_14.getLayout();
		ad_flowLayout_9.setVgap(4);
		ad_flowLayout_9.setAlignment(FlowLayout.LEFT);
		ad_panel_14.setBackground(Color.WHITE);

		JLabel ad_lblLernrate = new JLabel("Lernrate (%):");
		ad_lblLernrate.setBackground(Color.WHITE);
		ad_panel_14.add(ad_lblLernrate);

		JSpinner ad_learnRate = new JSpinner();
		ad_learnRate.setModel(new SpinnerNumberModel(Config
				.getIntValue("AD_Agent_LearnRate"), 0, 100, 10));
		ad_learnRate.setBackground(Color.WHITE);
		ad_panel_14.add(ad_learnRate);

		JPanel ad_panel_15 = new JPanel();
		ad_sarsaLambda.add(ad_panel_15, BorderLayout.SOUTH);
		FlowLayout ad_flowLayout_10 = (FlowLayout) ad_panel_15.getLayout();
		ad_flowLayout_10.setVgap(4);
		ad_flowLayout_10.setAlignment(FlowLayout.LEFT);
		ad_panel_15.setBackground(Color.WHITE);

		JLabel ad_lblDiscountRate = new JLabel("Discount-Rate (%):");
		ad_lblDiscountRate.setBackground(Color.WHITE);
		ad_panel_15.add(ad_lblDiscountRate);

		JSpinner ad_discountRate = new JSpinner();
		ad_discountRate.setModel(new SpinnerNumberModel(Config
				.getIntValue("AD_Agent_DiscountRate"), 0, 100, 10));
		ad_discountRate.setBackground(Color.WHITE);
		ad_panel_15.add(ad_discountRate);

		JPanel panel_25 = new JPanel();
		FlowLayout flowLayout_20 = (FlowLayout) panel_25.getLayout();
		flowLayout_20.setAlignment(FlowLayout.LEFT);
		panel_25.setBorder(new TitledBorder(null, "Settings",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_25.setBackground(Color.WHITE);
		panel_25.setBounds(6, 6, 237, 154);
		ad_agent_panel.add(panel_25);

		JPanel panel_28 = new JPanel();
		panel_28.setBackground(Color.WHITE);
		panel_25.add(panel_28);

		JCheckBox chckbxPolicyFrozen = new JCheckBox("Policy Frozen");
		chckbxPolicyFrozen.setSelected(Config.getBoolValue("AD_PolicyFrozen"));
		panel_28.add(chckbxPolicyFrozen);

		JPanel panel_33 = new JPanel();
		panel_33.setBackground(Color.WHITE);
		panel_25.add(panel_33);

		JCheckBox chckbxExplorationFrozen = new JCheckBox("Exploration Frozen");
		chckbxPolicyFrozen.setSelected(Config.getBoolValue("AD_ExploingFrozen"));
		chckbxExplorationFrozen.setBackground(Color.WHITE);
		panel_33.add(chckbxExplorationFrozen);
		
		JPanel panel_35 = new JPanel();
		FlowLayout flowLayout_23 = (FlowLayout) panel_35.getLayout();
		flowLayout_23.setVgap(0);
		panel_35.setBackground(Color.WHITE);
		panel_25.add(panel_35);
		
		JLabel lblDateiFrQwerte = new JLabel("Datei für Q-Werte:");
		panel_35.add(lblDateiFrQwerte);

		JPanel ad_agentLoadFilePanel = new JPanel();
		FlowLayout ad_flowLayout_30 = (FlowLayout) ad_agentLoadFilePanel
				.getLayout();
		ad_flowLayout_30.setHgap(15);
		ad_flowLayout_30.setVgap(2);
		ad_flowLayout_30.setAlignment(FlowLayout.LEFT);
		ad_agentLoadFilePanel.setBackground(Color.WHITE);
		panel_25.add(ad_agentLoadFilePanel);

		JTextField qValueFile = new JTextField();
		qValueFile.setText(Config.getStringValue("AD_QValueFile"));
		ad_agentLoadFilePanel.add(qValueFile);
		qValueFile.setColumns(10);

		JButton ad_savePathSearchBtn = new JButton("...");
		ad_savePathSearchBtn.setBackground(Color.WHITE);
		ad_savePathSearchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfiguratedFileFilter.setAcceptedFileType("csv");
				file_fc.setCurrentDirectory(new File(Config
						.getStringValue("AD_QValueFile")).getParentFile());
				int res = file_fc
						.showOpenDialog(frmLarcbotExperimentKonfigurator);

				if (res == JFileChooser.APPROVE_OPTION) {
					String filename = file_fc.getSelectedFile().getPath();
					if (!filename.endsWith(".csv"))
						filename += ".csv";
					qValueFile.setText(filename);
				}
			}
		});
		ad_agentLoadFilePanel.add(ad_savePathSearchBtn);

		// Reward Alex & Daniel
		JPanel ad_reward_system = new JPanel();
		tabPanelAlexDaniel.addTab("Reward", null, ad_reward_system, null);
		ad_reward_system.setBackground(Color.WHITE);
		ad_reward_system.setLayout(null);

		JPanel ad_bullet_rewards = new JPanel();
		FlowLayout ad_flowLayout_15 = (FlowLayout) ad_bullet_rewards
				.getLayout();
		ad_flowLayout_15.setVgap(0);
		ad_flowLayout_15.setHgap(0);
		ad_flowLayout_15.setAlignment(FlowLayout.RIGHT);
		ad_bullet_rewards.setBackground(Color.WHITE);
		ad_bullet_rewards.setBorder(new TitledBorder(null,
				"Belohnungen f\u00FCr Sch\u00FCsse", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		ad_bullet_rewards.setBounds(6, 11, 230, 255);
		ad_reward_system.add(ad_bullet_rewards);

		JPanel ad_panel_11 = new JPanel();
		FlowLayout ad_flowLayout_28 = (FlowLayout) ad_panel_11.getLayout();
		ad_flowLayout_28.setVgap(8);
		ad_panel_11.setBackground(Color.WHITE);
		ad_bullet_rewards.add(ad_panel_11);

		JLabel ad_lblVonKugelGetroffen = new JLabel("Von Kugel getroffen:");
		ad_panel_11.add(ad_lblVonKugelGetroffen);

		JTextField ad_hitByBulletReward = new JTextField();
		ad_hitByBulletReward.setText(Config.getStringValue(
				"AD_Reward_HitByBullet", "-3.0"));
		ad_hitByBulletReward.setColumns(5);
		ad_panel_11.add(ad_hitByBulletReward);

		JPanel ad_panel_19 = new JPanel();
		FlowLayout ad_flowLayout_29 = (FlowLayout) ad_panel_19.getLayout();
		ad_flowLayout_29.setVgap(8);
		ad_panel_19.setBackground(Color.WHITE);
		ad_bullet_rewards.add(ad_panel_19);

		JLabel ad_lblKugelTrifftKugel = new JLabel("Kugel trifft Kugel:");
		ad_panel_19.add(ad_lblKugelTrifftKugel);

		JTextField ad_bulletHitBulletReward = new JTextField();
		ad_bulletHitBulletReward.setText(Config.getStringValue(
				"AD_Reward_BulletHitBullet", "3.0"));
		ad_bulletHitBulletReward.setColumns(5);
		ad_panel_19.add(ad_bulletHitBulletReward);

		JPanel ad_panel_20 = new JPanel();
		FlowLayout ad_flowLayout_27 = (FlowLayout) ad_panel_20.getLayout();
		ad_flowLayout_27.setVgap(8);
		ad_panel_20.setBackground(Color.WHITE);
		ad_bullet_rewards.add(ad_panel_20);

		JLabel ad_lblKugelTrifftGegner = new JLabel("Kugel trifft Gegner:");
		ad_panel_20.add(ad_lblKugelTrifftGegner);

		JTextField ad_bulletHitEnemyReward = new JTextField();
		ad_bulletHitEnemyReward.setText(Config.getStringValue(
				"AD_Reward_BulletHitEnemy", "3.0"));
		ad_bulletHitEnemyReward.setColumns(5);
		ad_panel_20.add(ad_bulletHitEnemyReward);

		JPanel ad_panel_21 = new JPanel();
		FlowLayout ad_flowLayout_24 = (FlowLayout) ad_panel_21.getLayout();
		ad_flowLayout_24.setVgap(8);
		ad_panel_21.setBackground(Color.WHITE);
		ad_bullet_rewards.add(ad_panel_21);

		JLabel ad_lblKugelVerfehltGegner = new JLabel("Kugel verfehlt Gegner:");
		ad_panel_21.add(ad_lblKugelVerfehltGegner);

		JTextField ad_bulletHitWallReward = new JTextField();
		ad_bulletHitWallReward.setText(Config.getStringValue(
				"AD_Reward_BulletHitWall", "-3.0"));
		ad_bulletHitWallReward.setColumns(5);
		ad_panel_21.add(ad_bulletHitWallReward);

		JPanel ad_panel_22 = new JPanel();
		FlowLayout ad_flowLayout_8 = (FlowLayout) ad_panel_22.getLayout();
		ad_flowLayout_8.setVgap(8);
		ad_panel_22.setBackground(Color.WHITE);
		ad_bullet_rewards.add(ad_panel_22);

		JCheckBox ad_multBulletPower = new JCheckBox("Kugelpower verrechnen");
		ad_multBulletPower.setSelected(Config.getBoolValue(
				"AD_Reward_MultBulletPower", false));
		ad_multBulletPower.setBackground(Color.WHITE);
		ad_panel_22.add(ad_multBulletPower);

		JPanel ad_hit_rewards = new JPanel();
		FlowLayout ad_flowLayout_16 = (FlowLayout) ad_hit_rewards.getLayout();
		ad_flowLayout_16.setVgap(0);
		ad_flowLayout_16.setHgap(0);
		ad_flowLayout_16.setAlignment(FlowLayout.RIGHT);
		ad_hit_rewards.setBackground(Color.WHITE);
		ad_hit_rewards.setBorder(new TitledBorder(null,
				"Belohnungen f\u00FCr's Rammen", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		ad_hit_rewards.setBounds(242, 11, 224, 145);
		ad_reward_system.add(ad_hit_rewards);

		JPanel ad_panel_32 = new JPanel();
		ad_panel_32.setBackground(Color.WHITE);
		ad_hit_rewards.add(ad_panel_32);

		JLabel ad_lblVonGegnerGerammt = new JLabel("Von Gegner gerammt:");
		ad_panel_32.add(ad_lblVonGegnerGerammt);

		JTextField ad_hitByEnemy = new JTextField();
		ad_hitByEnemy
				.setText(Config.getStringValue("AD_Reward_HitByEnemy", "1.0"));
		ad_hitByEnemy.setColumns(5);
		ad_panel_32.add(ad_hitByEnemy);

		JPanel ad_panel_23 = new JPanel();
		ad_panel_23.setBackground(Color.WHITE);
		ad_hit_rewards.add(ad_panel_23);

		JLabel ad_lblGegnerGerammt = new JLabel("Gegner gerammt:");
		ad_panel_23.add(ad_lblGegnerGerammt);

		JTextField ad_hitRobotReward = new JTextField();
		ad_hitRobotReward.setText(Config.getStringValue("AD_Reward_HitRobot",
				"1.0"));
		ad_hitRobotReward.setColumns(5);
		ad_panel_23.add(ad_hitRobotReward);

		JPanel ad_panel_24 = new JPanel();
		ad_panel_24.setBackground(Color.WHITE);
		ad_hit_rewards.add(ad_panel_24);

		JLabel ad_lblWandGerammt = new JLabel("Wand gerammt:");
		ad_panel_24.add(ad_lblWandGerammt);

		JTextField ad_hitWallReward = new JTextField();
		ad_hitWallReward.setText(Config
				.getStringValue("AD_Reward_HitWall", "-5.0"));
		ad_hitWallReward.setColumns(5);
		ad_panel_24.add(ad_hitWallReward);

		JPanel ad_victory_rewards = new JPanel();
		FlowLayout ad_flowLayout_17 = (FlowLayout) ad_victory_rewards
				.getLayout();
		ad_flowLayout_17.setAlignment(FlowLayout.RIGHT);
		ad_flowLayout_17.setVgap(0);
		ad_flowLayout_17.setHgap(0);
		ad_victory_rewards.setBorder(new TitledBorder(null,
				"Belohnungen bei Rundenende", TitledBorder.LEADING,
				TitledBorder.TOP, null, Color.BLACK));
		ad_victory_rewards.setBackground(Color.WHITE);
		ad_victory_rewards.setBounds(242, 161, 224, 105);
		ad_reward_system.add(ad_victory_rewards);

		JPanel ad_panel_26 = new JPanel();
		ad_panel_26.setBackground(Color.WHITE);
		ad_victory_rewards.add(ad_panel_26);

		JLabel ad_lblRundeGewonnen = new JLabel("Runde gewonnen:");
		ad_panel_26.add(ad_lblRundeGewonnen);

		JTextField ad_winningReward = new JTextField();
		ad_winningReward.setText(Config
				.getStringValue("AD_Reward_Winning", "10.0"));
		ad_winningReward.setColumns(5);
		ad_panel_26.add(ad_winningReward);

		JPanel ad_panel_27 = new JPanel();
		ad_panel_27.setBackground(Color.WHITE);
		ad_victory_rewards.add(ad_panel_27);

		JLabel ad_lblRundeVerloren = new JLabel("Runde verloren:");
		ad_panel_27.add(ad_lblRundeVerloren);

		JTextField ad_loosingReward = new JTextField();
		ad_loosingReward.setText(Config.getStringValue("AD_Reward_Loosing",
				"-10.0"));
		ad_loosingReward.setColumns(5);
		ad_panel_27.add(ad_loosingReward);

		// ###################################

		JPanel robocode_panel = new JPanel();
		robocode_panel.setBackground(Color.WHITE);
		tabbedPane.addTab("Robocode", null, robocode_panel, null);
		tabbedPane.setEnabledAt(2, true);
		robocode_panel.setLayout(null);

		JPanel settings_panel = new JPanel();
		settings_panel.setBackground(Color.WHITE);
		settings_panel.setBounds(7, 7, 220, 189);
		settings_panel.setBorder(new TitledBorder(null, "Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		robocode_panel.add(settings_panel);
		settings_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
		
		JPanel panel_36 = new JPanel();
		FlowLayout flowLayout_33 = (FlowLayout) panel_36.getLayout();
		flowLayout_33.setVgap(3);
		flowLayout_33.setHgap(0);
		panel_36.setBackground(Color.WHITE);
		settings_panel.add(panel_36);
		
		JCheckBox chckbxBotOliVictor = new JCheckBox("Bot Oli & Viktor");
		chckbxBotOliVictor.setSelected(Config.getBoolValue("Player_BotOliVictor"));
		panel_36.add(chckbxBotOliVictor);
		
		JPanel panel_37 = new JPanel();
		panel_37.setBackground(Color.WHITE);
		FlowLayout flowLayout_36 = (FlowLayout) panel_37.getLayout();
		flowLayout_36.setHgap(0);
		flowLayout_36.setVgap(3);
		settings_panel.add(panel_37);
		
		JCheckBox chckbxBotAlexDaniel = new JCheckBox("Bot Daniel & Alex");
		chckbxBotAlexDaniel.setSelected(Config.getBoolValue("Player_BotAlexDaniel"));
		panel_37.add(chckbxBotAlexDaniel);

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EmptyBorder(0, 0, 0, 20));
		settings_panel.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 3));

		JCheckBox showGUI = new JCheckBox("GUI starten");
		showGUI.setBackground(Color.WHITE);
		showGUI.setSelected(Config.getBoolValue("ShowRobocodeGUI"));
		panel.add(showGUI);
		showGUI.setHorizontalAlignment(SwingConstants.LEFT);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel_1.setBorder(new EmptyBorder(0, 0, 0, 20));
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setVgap(3);
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
		flowLayout_2.setVgap(3);
		settings_panel.add(rounds_panel);

		JSpinner rounds = new JSpinner();
		rounds.setModel(new SpinnerNumberModel(
				Config.getIntValue("Rounds", 10), 10, 100000000, 10));
		rounds_panel.add(rounds);

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		FlowLayout flowLayout_3 = (FlowLayout) panel_2.getLayout();
		flowLayout_3.setHgap(0);
		rounds_panel.add(panel_2);

		JLabel lblRunden = new JLabel("Runden");
		panel_2.add(lblRunden);
		lblRunden.setHorizontalAlignment(SwingConstants.LEFT);

		JPanel enemy_panel = new JPanel();
		enemy_panel.setBorder(new TitledBorder(null, "Gegner",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
		panel_12.setBounds(7, 208, 220, 70);
		robocode_panel.add(panel_12);
		panel_12.setBorder(new TitledBorder(null, "Robocode Home Directory:",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
				int res = folder_fc
						.showOpenDialog(frmLarcbotExperimentKonfigurator);

				if (res == JFileChooser.APPROVE_OPTION) {
					robocodeHome.setText(folder_fc.getSelectedFile().getPath());
					Config.setStringValue("RobocodeHome",
							robocodeHome.getText());

					loadRobots();
				}
			}
		});
		button.setBackground(Color.WHITE);
		panel_12.add(button);

		processPanel = new JPanel();
		processPanel.setBackground(Color.WHITE);
		tabbedPane.addTab("Prozesse", null, processPanel, null);
		tabbedPane.setEnabledAt(3, false);
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
				// Config.resetConfig();

				Config.setBoolValue("Agent_LoadOnStart",
						loadOnStart.isSelected());
				Config.setStringValue("Agent_LoadFile", agentLoadFile.getText());
				Config.setBoolValue("Agent_SaveAgents", saveAgents.isSelected());
				Config.setStringValue("Agent_SaveFile", agentSaveFile.getText());

				AgentMode agentMode = (AgentMode) agentModeChoose
						.getSelectedItem();

				Config.setIntValue("Agent_Mode", agentMode.ordinal());
				Config.setIntValue("Agent_SuccesChance",
						(int) successChance.getValue());
				switch (agentMode) {
				case Q_LEARNING:
					Config.setIntValue("Agent_DiscountRate",
							(int) discountRateQ.getValue());
					Config.setIntValue("Agent_LearnRate",
							(int) learnRateQ.getValue());
					break;
				case SARSA_LAMBDA:
					Config.setIntValue("Agent_QueueSize",
							(int) propagationDepth.getValue());
					Config.setIntValue("Agent_Lambda", (int) lambda.getValue());
					Config.setIntValue("Agent_DiscountRate",
							(int) discountRate.getValue());
					Config.setIntValue("Agent_LearnRate",
							(int) learnRate.getValue());
					break;
				default:
					break;
				}

				Config.setIntValue("Robot_MoveEnv",
						((MoveEnvironments) moveEnvChoose.getSelectedItem())
								.ordinal());
				Config.setIntValue(
						"Robot_AttackEnv",
						((AttackEnvironments) attackEnvChoose.getSelectedItem())
								.ordinal());

				Config.setBoolValue("Env_Debug", envDebug.isSelected());
				Config.setBoolValue("Env_PaintMoveEnv",
						paintMoveEnv.isSelected());
				Config.setBoolValue("Env_PaintAttackEnv",
						paintAttackEnv.isSelected());
				Config.setIntValue("Env_GridSize",
						(int) moveGridSize.getValue());

				Config.setDoubleValue("Reward_HitByBullet",
						Double.parseDouble(ov_hitByBulletReward.getText()));
				Config.setDoubleValue("Reward_BulletHitBullet",
						Double.parseDouble(ov_bulletHitBulletReward.getText()));
				Config.setDoubleValue("Reward_BulletHitEnemy",
						Double.parseDouble(ov_bulletHitEnemyReward.getText()));
				Config.setDoubleValue("Reward_BulletHitWall",
						Double.parseDouble(ov_bulletHitWallReward.getText()));
				Config.setBoolValue("Reward_MultBulletPower",
						ov_multBulletPower.isSelected());

				Config.setDoubleValue("Reward_HitByEnemy",
						Double.parseDouble(ov_hitByEnemy.getText()));
				Config.setDoubleValue("Reward_HitRobot",
						Double.parseDouble(ov_hitRobotReward.getText()));
				Config.setDoubleValue("Reward_HitWall",
						Double.parseDouble(ov_hitWallReward.getText()));

				Config.setDoubleValue("Reward_Winning",
						Double.parseDouble(ov_winningReward.getText()));
				Config.setDoubleValue("Reward_Loosing",
						Double.parseDouble(ov_loosingReward.getText()));

				// Alex & Daniel
				Config.setIntValue("AD_Agent_ListCapacity", (int)ad_propagationDepth.getValue());
				Config.setIntValue("AD_Agent_ExplorationRate", (int)ad_explorationRate.getValue());
				Config.setIntValue("AD_Agent_Lambda", (int)ad_lambda.getValue());
				Config.setIntValue("AD_Agent_LearnRate", (int)ad_learnRate.getValue());
				Config.setIntValue("AD_Agent_DiscountRate", (int)ad_discountRate.getValue());
				
				Config.setBoolValue("AD_PolicyFrozen", chckbxPolicyFrozen.isSelected());
				Config.setBoolValue("AD_ExploingFrozen", chckbxExplorationFrozen.isSelected());
				Config.setStringValue("AD_QValueFile", qValueFile.getText());
				
				Config.setDoubleValue("AD_Reward_HitByBullet",
						Double.parseDouble(ad_hitByBulletReward.getText()));
				Config.setDoubleValue("AD_Reward_BulletHitBullet",
						Double.parseDouble(ad_bulletHitBulletReward.getText()));
				Config.setDoubleValue("AD_Reward_BulletHitEnemy",
						Double.parseDouble(ad_bulletHitEnemyReward.getText()));
				Config.setDoubleValue("AD_Reward_BulletHitWall",
						Double.parseDouble(ad_bulletHitWallReward.getText()));
				Config.setBoolValue("AD_Reward_MultBulletPower",
						ad_multBulletPower.isSelected());

				Config.setDoubleValue("AD_Reward_HitByEnemy",
						Double.parseDouble(ad_hitByEnemy.getText()));
				Config.setDoubleValue("AD_Reward_HitRobot",
						Double.parseDouble(ad_hitRobotReward.getText()));
				Config.setDoubleValue("AD_Reward_HitWall",
						Double.parseDouble(ad_hitWallReward.getText()));

				Config.setDoubleValue("AD_Reward_Winning",
						Double.parseDouble(ad_winningReward.getText()));
				Config.setDoubleValue("AD_Reward_Loosing",
						Double.parseDouble(ad_loosingReward.getText()));
				

				// Allgemein
				Config.setStringValue("RobocodeHome", robocodeHome.getText());
				
				Config.setBoolValue("ShowRobocodeGUI", showGUI.isSelected());
				Config.setBoolValue("StartBattle", startBattle.isSelected());
				Config.setIntValue("Rounds", (int) rounds.getValue());
				Config.setStringValue("EnemyRobot",
						robot_list.getSelectedValue());
				Config.setIntValue("FieldWidth", 800);
				Config.setIntValue("FieldHeight", 600);
				
				Config.setBoolValue("Player_BotOliViktor", chckbxBotOliVictor.isSelected());
				Config.setBoolValue("Player_BotAlexDaniel", chckbxBotAlexDaniel.isSelected());

				start();
			}
		});
		button_panel.add(btnStart);

		agentModeChooseChanged();
	}

	private void agentModeChooseChanged() {
		switch ((AgentMode) agentModeChoose.getSelectedItem()) {
		case FIGHTING:
			sarsaLambda.setEnabled(false);
			enableComponents(sarsaLambda, false);

			qLearning.setEnabled(false);
			enableComponents(qLearning, false);

			successChance.setEnabled(false);
			tabPanelOliViktor.setEnabledAt(2, false);
			break;
		case Q_LEARNING:
			sarsaLambda.setEnabled(false);
			enableComponents(sarsaLambda, false);

			qLearning.setEnabled(true);
			enableComponents(qLearning, true);

			successChance.setEnabled(true);
			tabPanelOliViktor.setEnabledAt(2, true);
			break;
		case RANDOM:
			sarsaLambda.setEnabled(false);
			enableComponents(sarsaLambda, false);

			qLearning.setEnabled(false);
			enableComponents(qLearning, false);

			successChance.setEnabled(false);
			tabPanelOliViktor.setEnabledAt(2, false);
			break;
		case SARSA_LAMBDA:
			sarsaLambda.setEnabled(true);
			enableComponents(sarsaLambda, true);

			qLearning.setEnabled(false);
			enableComponents(qLearning, false);

			successChance.setEnabled(true);
			tabPanelOliViktor.setEnabledAt(2, true);
			break;
		}
	}

	private void enableComponents(Container container, boolean enable) {
		Component[] components = container.getComponents();
		for (Component component : components) {
			component.setEnabled(enable);
			if (component instanceof Container) {
				enableComponents((Container) component, enable);
			}
		}
	}

	private void loadRobots() {
		String robocode = Config.getStringValue("RobocodeHome");
		File robots = new File(robocode, "robots");

		if (robots.isDirectory()) {
			robot_listModel.removeAllElements();
			robot_listModel.addElement(" ");
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
					robot_listModel.addElement(name.substring(0,
							name.lastIndexOf('.')));
				}
			}
		}
	}

	private void start() {
		String rHome = Config.getStringValue("RobocodeHome"), robocodeArgs = "", battle = "", vmArgs = "-Xmx512M -Dsun.io.useCanonCaches=false -Ddebug=true -DNOSECURITY=true -DPARALLEL=true";
		ArrayList<String> classpath = new ArrayList<String>();

		String herePath = new File("").getAbsolutePath();
//		ArrayList<File> eclipseProjects = findEclipseProjectDir(new File(
//				herePath).getParentFile());

		classpath.add(rHome + "\\libs\\robocode.jar");
//		for (File dir : eclipseProjects) {
			 File libs = new File(herePath + "\\libs");
			 if (libs.exists() && libs.isDirectory()) {
				 for (File f : libs.listFiles()) {
					 classpath.add(f.getAbsolutePath());
				 }
			 }
//		 }
//		 classpath.add(new File("").getAbsolutePath());

		Config.save();

		if (!Config.getBoolValue("ShowRobocodeGUI")) {
			robocodeArgs += "-nodisplay ";
		}

		if (Config.getBoolValue("StartBattle")) {
			battle = Config.createAndSaveBattle(Config.getBoolValue("Player_BotOliViktor"), Config.getBoolValue("Player_BotAlexDaniel"), Config.getStringValue("EnemyRobot", null));
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
			tabbedPane.setEnabledAt(3, true);

			createObjectsForNewProcess(Config.getStringValue("EnemyRobot"), rcp);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private ArrayList<File> findEclipseProjectDir(File startDir) {
		ArrayList<File> founded = new ArrayList<File>();
		if (startDir.isDirectory() && startDir.getName().charAt(0) != '.') {
			ArrayList<File> dirs = new ArrayList<File>();
			for (File f : startDir.listFiles()) {
				if (f.isDirectory()) {
					if (f.getName().equals("src")) {
						founded.add(startDir);
						return founded;
					}
					dirs.add(f);
				}
			}
			for (File d : dirs) {
				ArrayList<File> res = findEclipseProjectDir(d);
				if (res.size() > 0) {
					founded.addAll(res);
				}
			}
		}

		return founded;
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
				JRadioButton btn = (JRadioButton) e.getSource();

				if (!btn.isSelected())
					return;

				String name = "panel"
						+ btn.getName().substring(btn.getName().indexOf('_'));

				for (Component c : processPanel.getComponents()) {
					if (c instanceof JPanel && c.getName() != null
							&& c.getName().startsWith("panel")) {
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
		redirectConsole.redirectOutput(loggingPanel, roundLabel,
				rcp.getInputStream(), rcp.getErrorStream(), index);
	}

	private void removeObjectsForProcess(int index) {
		boolean wasSelected = false;

		for (Component c : processPanel.getComponents()) {
			if (c instanceof JPanel && c.getName() != null
					&& c.getName().equals("panel_" + index)) {
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
			if (c instanceof JRadioButton && c.getName() != null
					&& c.getName().equals("btn_" + index)) {
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
