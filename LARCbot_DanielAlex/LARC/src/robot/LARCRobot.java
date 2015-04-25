package robot;

import java.awt.Graphics2D;

import robocode.AdvancedRobot;
import robocode.DeathEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import utility.Position;
import agents.LARCAgent;
import environment.LARCEnvironment;

public class LARCRobot extends AdvancedRobot {

	private LARCEnvironment environment;
	private LARCAgent agent;
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
	private int lastReward;

	public LARCRobot() {
		this.enemyX = 0;
		this.enemyY = 0;
		this.selfGridPos = new Position(1, 1);
		this.enemyGridPos = new Position(2, 2);
		this.gameOver = false;
		this.environment = new LARCEnvironment(this);
		this.agent = new LARCAgent(this);
	}

	@Override
	public void run() {
		this.setAdjustRadarForGunTurn(true);
		// init
		this.environment.env_init();
		this.agent.agent_init();
		int stateID = this.environment.env_start();
		int actionID = this.agent.agent_start(stateID);
		while (!this.getGameOver()) {
			// default actions:
			setTurnRadarRight(360);
			// stepping
			stateID = this.environment.env_step(actionID);
			actionID = this.agent.agent_step(stateID);
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
		this.lastReward = -10;
		this.agent.agent_end();
		this.environment.env_cleanup();
		this.agent.agent_cleanup();
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		this.gameOver = true;
		this.lastReward = 10;
		this.agent.agent_end();
		this.environment.env_cleanup();
		this.agent.agent_cleanup();
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
		if (this.getVelocity() == 0) {
			if (instructions[2] == 1.0) {
				setFire(this.getEnergy() * 0.2);
			}
			setAhead(instructions[0]);
			setTurnLeft(instructions[1]);
		}
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

	public int getLastReward() {
		return lastReward;
	}

	public void setLastReward(int lastReward) {
		this.lastReward = lastReward;
	}

	public double getEnergyRatio() {
		return energyRatio;
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
