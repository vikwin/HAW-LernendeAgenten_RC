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

	public static final int NO_OF_STATES = 5 * 8 * 8; // my position * enemy position * enemy direction * enemydistance
	public static final int NO_OF_ACTIONS = 2 * 8 * 9; // Fire * Direction * MoveGun + BACK
	public static double[][] VALUE_FUNCTION = new double[NO_OF_ACTIONS][NO_OF_STATES];
	public double currentGunAngleToEnemy;
	public double oldGunAngleToEnemy = 0;
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
	private double enemyDirection;

	public LARCRobot() {
		this.enemyX = 0;
		this.enemyY = 0;
		this.enemyDirection = 1; 
		this.selfGridPos = new Position(1, 1);
		this.enemyGridPos = new Position(2, 2);
		this.environment = new LARCEnvironment(this);
		this.agent = new LARCAgent(this);
	}

	@Override
	public void run() {
		this.execute();
		this.setAdjustRadarForGunTurn(true);
		this.setAdjustGunForRobotTurn(true);
		this.setColors(Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN);
		// init
		this.environment.env_init();
		this.agent.agent_init();
		int stateID = this.environment.env_start();
		int actionID = this.agent.agent_start(stateID);
		while (true) {
			// default actions:
			setTurnRadarRight(2000);
			// stepping
			if (this.getVelocity() == 0) {
				stateID = this.environment.env_step(actionID);
				actionID = this.agent.agent_step(stateID);
			}
			execute();
		}
	}

	public void move(double[] instructions) {
		// Gun Zielen:
		// setTurnGunRight(this.angleToEnemy + instructions[4]);

		// Panzer Fahren!:
		backOrAhead(instructions[1], instructions[0]);

		// Schießen:
		if (instructions[2] == 1.0) {
			setFire(this.getEnergy() * 0.2);
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

		// Dreiecks Winkelformeln lÃ¶sen nach:
		this.enemyX = (this.getX() + Math.sin(angle) * this.distanceToEnemy); // Gegenkathete
		this.enemyY = (this.getY() + Math.cos(angle) * this.distanceToEnemy); // Ankathete
	}

	private void backOrAhead(double turnDegrees, double distance) {
		if (turnDegrees > 90) {
			setAhead(-distance);
			setTurnLeft(turnDegrees - 180);
		} else if (turnDegrees < -90) {
			setAhead(-distance);
			setTurnLeft(turnDegrees + 180);
		} else {
			setAhead(distance);
			setTurnLeft(turnDegrees);
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

	public double getEnemyDirection() {
		return enemyDirection;
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
		// calculate enemy direction via oster algorithm:
		double velocity = event.getVelocity();
		if (velocity > 0) {
			enemyDirection = event.getHeading();
		} else {
			enemyDirection = event.getHeading() - 180;
			if (enemyDirection < 0) {
				enemyDirection += 360;
			}
		}
		System.out.println("Enemy Direction: "+enemyDirection);

		// setTurnGunRight(getHeading() - getGunHeading() + event.getBearing());
		double x = getHeading() - getGunHeading();
		if (x > 180) {
			x -= 360;
		} else if (x < -180) {
			x += 360;
			x *= -1;
		}
		x += event.getBearing();
		if (x > 180) {
			x -= 360;
		} else if (x < -180) {
			x += 360;
			x *= -1;
		}
		setTurnGunRight(x);

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
		this.currentReward = 30;
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
