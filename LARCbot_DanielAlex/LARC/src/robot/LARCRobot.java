package robot;

import java.awt.Color;
import java.awt.Graphics2D;

import robocode.AdvancedRobot;
import robocode.DeathEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import utility.Position;
import agents.LARCAgent;
import environment.LARCEnvironment;

public class LARCRobot extends AdvancedRobot {

	public static final int ROUNDS_TO_LEARN = 1000;

	public static final int NO_OF_STATES = 5 * 8 * 36; // GridPos * GegnerPos * GunPos
	public static final int NO_OF_ACTIONS = 2 * 8 * 2; // Fire * Direction * MoveGun
	public static double[][] VALUE_FUNCTION = new double[NO_OF_ACTIONS][NO_OF_STATES];
	public static int EPISODE_COUNTER = 0;
	public double currentGunAngleToEnemy;
	public double oldGunAngleToEnemy = 0;
	private static final double DEFAULT_GUNTURN = 10;
	private LARCEnvironment environment;
	private LARCAgent agent;
	private double enemyX;
	private double enemyY;
	private double angleToEnemy;
	private double distanceToEnemy;

	private Position selfGridPos;
	private Position enemyGridPos;
	private double selfEnergy;
	private double enemyEnergy;
	private double energyRatio;
	private int currentReward;
	private double gunPostion;

	public LARCRobot() {
		EPISODE_COUNTER++;
		System.out.println("EPISODE COUNTER: " + EPISODE_COUNTER);
		this.enemyX = 0;
		this.enemyY = 0;
		this.selfGridPos = new Position(1, 1);
		this.enemyGridPos = new Position(2, 2);
		this.environment = new LARCEnvironment(this);
		this.agent = new LARCAgent(this);
	}

	@Override
	public void run() {
		this.setTurnGunLeft(this.getGunHeading() % 10);
		this.execute();
		this.setAdjustRadarForGunTurn(true);
		this.setColors(Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN);
		// init
		this.environment.env_init();
		this.agent.agent_init();
		int stateID = this.environment.env_start();
		int actionID = this.agent.agent_start(stateID);
		while (true) {
			// default actions:
			setTurnRadarRight(360);
			// stepping
			if (this.getGunTurnRemaining() == 0 && this.getVelocity() == 0) {
				stateID = this.environment.env_step(actionID);
				actionID = this.agent.agent_step(stateID);
			}
			execute();
		}
	}

	public void move(double[] instructions) {
		if (instructions[2] == 1.0) {
			setFire(this.getEnergy() * 0.2);
		}
		setAhead(instructions[0]);
		setTurnLeft(instructions[1]);
		if (instructions[3] == 1) {
			this.setTurnGunLeft(DEFAULT_GUNTURN);
		} else if (instructions[3] == 2) {
			this.setTurnGunRight(DEFAULT_GUNTURN);
		}
	}

	private void updateEnergyRatio() {
		if (this.enemyEnergy > 0) {
			this.energyRatio = this.selfEnergy / this.enemyEnergy; // Energy ratio: self/enemy
		}
	}

	private void triangulateEnemyPosition() {
		// Normalisierter Winkel zwischen uns und Gegner (Radiant)
		double angle = Math.toRadians(((this.getHeading() + angleToEnemy) % 360));

		// Dreiecks Winkelformeln lösen nach:
		this.enemyX = (this.getX() + Math.sin(angle) * this.distanceToEnemy); // Gegenkathete
		this.enemyY = (this.getY() + Math.cos(angle) * this.distanceToEnemy); // Ankathete
	}

	public void getSelfGridPosition() {
		selfGridPos.setX(((int) (this.getX() / LARCEnvironment.TILESIZE)));
		selfGridPos.setY(((int) (this.getY() / LARCEnvironment.TILESIZE)));
	}

	public void getEnemyGridPosition() {
		enemyGridPos.setX(((int) (this.getEnemyX() / LARCEnvironment.TILESIZE)));
		enemyGridPos.setY(((int) (this.getEnemyY() / LARCEnvironment.TILESIZE)));
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

	public int getCurrentReward() {
		return currentReward;
	}

	public void setCurrentReward(int lastReward) {
		this.currentReward = lastReward;
	}

	public double getEnergyRatio() {
		return energyRatio;
	}

	public double getAngleToEnemy() {
		return angleToEnemy;
	}

	public void setAngleToEnemy(double angleToEnemy) {
		this.angleToEnemy = angleToEnemy;
	}

	public double getGunPostion() {
		return gunPostion;
	}

	/*************************************************************************************************************************/
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		// point gun towards enemy:
		// setTurnGunRight(getHeading() - getGunHeading() + event.getBearing());
		// update enemy-related state variables:
		this.currentGunAngleToEnemy = Math.abs(getHeading() - getGunHeading() + event.getBearing());
		this.angleToEnemy = event.getBearing();
		this.distanceToEnemy = event.getDistance();
		this.gunPostion = this.getGunHeading();
		this.triangulateEnemyPosition();
		this.enemyEnergy = event.getEnergy();
		this.selfEnergy = this.getEnergy();
		this.updateEnergyRatio();
	}

	@Override
	public void onDeath(DeathEvent event) {
		this.agent.agent_end();
		this.environment.env_cleanup();
		this.agent.agent_cleanup();
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		this.currentReward = 10;
		this.agent.agent_end();
		this.environment.env_cleanup();
		this.agent.agent_cleanup();
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
