package robot;

import java.awt.Graphics2D;

import org.rlcommunity.rlglue.codec.LocalGlue;
import org.rlcommunity.rlglue.codec.RLGlue;

import environment.LARCEnvironment;
import agents.LARCAgent;
import robocode.AdvancedRobot;
import robocode.DeathEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import state.State;
import utility.Position;

public class LARCRobot extends AdvancedRobot {

	private LARCEnvironment environment;
	private LARCAgent agent;
	private LARCExperiment experiment;
	private double enemyX;
	private double enemyY;
	private double angleToEnemy;
	private double distanceToEnemy;
	private boolean gameOver;

	private Position selfGridPos;
	private Position enemyGridPos;
	private double selfEnergy;
	private double enemyEnergy;
	private double energyRatio;
	private State currentState;
	private State nextState;
	private int lastReward;

	public LARCRobot() {
		this.enemyX = 0;
		this.enemyY = 0;
		this.selfGridPos = new Position(1, 1);
		this.enemyGridPos = new Position(2, 2);
		this.gameOver = false;
		this.environment = new LARCEnvironment(this);
		this.agent = new LARCAgent(this);
		this.experiment = new LARCExperiment(this);

		// AgentLoader theAgentLoader = new AgentLoader(agent);
		// // Create an environmentloader that will start the environment when
		// its
		// // run method is called
		// EnvironmentLoader theEnvironmentLoader = new
		// EnvironmentLoader(environment);
		//
		// // Create threads so that the agent and environment can run
		// // asynchronously
		// Thread agentThread = new Thread(theAgentLoader);
		// Thread environmentThread = new Thread(theEnvironmentLoader);
		//
		// // Start the threads
		// agentThread.start();
		// environmentThread.start();

		LocalGlue localGlueImplementation = new LocalGlue(environment, agent);
		RLGlue.setGlue(localGlueImplementation);
		experiment.start();
	}

	@Override
	public void run() {
		this.setAdjustRadarForGunTurn(true);
		while (true) {
			// default actions:
			setTurnRadarLeft(360);
			execute();
		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		// point gun towards enemy:
		setTurnGunRight(getHeading() - getGunHeading() + event.getBearing());

		// update enemy-related state variables:
		this.angleToEnemy = event.getBearing();
		this.distanceToEnemy = event.getDistance();
		this.triangulateEnemyPosition();
		this.enemyEnergy = event.getEnergy();
		this.selfEnergy = this.getEnergy();
		this.updateEnergyRatio();
	}

	@Override
	public void onDeath(DeathEvent event) {
		this.gameOver = true;
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		this.gameOver = true;
	}

	private void updateEnergyRatio() {
		if (this.enemyEnergy > 0) {
			this.energyRatio = this.selfEnergy / this.enemyEnergy; // Energy ratio: self/enemy
		} else {
			this.gameOver = true;
		}
	}

	private void triangulateEnemyPosition() {
		// Normalisierter Winkel zwischen uns und Gegner (Radiant)
		double angle = Math.toRadians(((this.getHeading() + angleToEnemy) % 360));

		// Dreiecks Winkelformeln lösen nach:
		this.enemyX = (this.getX() + Math.sin(angle) * this.distanceToEnemy); // Gegenkathete
		this.enemyY = (this.getY() + Math.cos(angle) * this.distanceToEnemy); // Ankathete
	}

	public void move(double[] instructions) {
		if (instructions[2] == 1.0) {
			setFire(this.getEnergy() * 0.2);
		}
		setAhead(instructions[0]);
		if (instructions[1] > 180.0) {
			setTurnLeft(360 - instructions[1]);
		}
		setTurnRight(instructions[1]);
	}

	public void getSelfGridPosition() {
		selfGridPos.setX(((int) (this.getX() / LARCEnvironment.TILESIZE)));
		selfGridPos.setY(((int) (this.getY() / LARCEnvironment.TILESIZE)));
	}

	public void getEnemyGridPosition() {
		enemyGridPos.setX(((int) (this.getEnemyX() / LARCEnvironment.TILESIZE)));
		enemyGridPos.setY(((int) (this.getEnemyY() / LARCEnvironment.TILESIZE)));
	}

	public boolean getGameOver() {
		return this.gameOver;
	}

	public double getEnemyX() {
		return enemyX;
	}

	public double getEnemyY() {
		return enemyY;
	}

	public Position getSelfGridPos() {
		return selfGridPos;
	}

	public Position getEnemyGridPos() {
		return enemyGridPos;
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public int getLastReward() {
		return lastReward;
	}

	public void setLastReward(int lastReward) {
		this.lastReward = lastReward;
	}

	public State getNextState() {
		return nextState;
	}

	public void setNextState(State nextState) {
		this.nextState = nextState;
	}

	@Override
	public void onPaint(Graphics2D g) {
		// Set the paint color to red
		g.setColor(java.awt.Color.RED);
		// draw grid
		for (int i = 0; i < this.getBattleFieldWidth(); i += LARCEnvironment.TILESIZE) {
			for (int j = 0; j < this.getBattleFieldHeight(); j += LARCEnvironment.TILESIZE) {

				g.drawRect(i, j, LARCEnvironment.TILESIZE, LARCEnvironment.TILESIZE);
			}
		}
		g.fillRect(((int) (selfGridPos.getX() * LARCEnvironment.TILESIZE)),
				((int) (selfGridPos.getY() * LARCEnvironment.TILESIZE)), LARCEnvironment.TILESIZE,
				LARCEnvironment.TILESIZE);
		g.fillRect(((int) (enemyGridPos.getX() * LARCEnvironment.TILESIZE)),
				((int) (enemyGridPos.getY() * LARCEnvironment.TILESIZE)), LARCEnvironment.TILESIZE,
				LARCEnvironment.TILESIZE);
	}
}
