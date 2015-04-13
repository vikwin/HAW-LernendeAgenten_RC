package src.robot;

import java.awt.Graphics2D;

import org.rlcommunity.rlglue.codec.util.AgentLoader;
import org.rlcommunity.rlglue.codec.util.EnvironmentLoader;

import robocode.AdvancedRobot;
import robocode.DeathEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import src.agents.LARCAgent;
import src.environment.LARCEnvironment;
import src.utility.Position;

public class LARCRobot extends AdvancedRobot {

	public final double NIENTY = 90.0;

	private LARCEnvironment environment;
	private LARCAgent agent;

	private double EnemyX;
	private double EnemyY;
	private double enemyHeading;
	private double distanceToEnemy;
	private boolean gameOver;

	private Position selfGridPos;
	private Position enemyGridPos;

	public LARCRobot() {

		EnemyX = 0;
		EnemyY = 0;
		this.gameOver = false;
		this.environment = new LARCEnvironment(this);
		this.agent = new LARCAgent();
		AgentLoader theAgentLoader = new AgentLoader(agent);
		// Create an environmentloader that will start the environment when its
		// run method is called
		EnvironmentLoader theEnvironmentLoader = new EnvironmentLoader(environment);

		// Create threads so that the agent and environment can run
		// asynchronously
		Thread agentThread = new Thread(theAgentLoader);
		Thread environmentThread = new Thread(theEnvironmentLoader);

		// Start the threads
		agentThread.start();
		environmentThread.start();

	}

	@Override
	public void run() {
		this.setAdjustRadarForGunTurn(true);
		while (true) {
			setTurnRadarLeft(360);
			execute();
		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		setTurnGunRight(getHeading() - getGunHeading() + event.getBearing()); // auf den Gegener zielen

		this.enemyHeading = this.getHeading();
		this.distanceToEnemy = event.getDistance();
		this.triangulateEnemyPosition();

	}

	private void triangulateEnemyPosition() {
		double alpha = Math.abs(this.NIENTY - this.enemyHeading);
		double gegenkathete = Math.sin(alpha) * this.distanceToEnemy;
		double ankathete = Math.cos(alpha) * this.distanceToEnemy;
		if (enemyHeading < 90 && enemyHeading >= 0) {
			// rechts hoch
			this.EnemyX = this.getX() + ankathete;
			this.EnemyY = this.getY() + gegenkathete;
			this.getEnemyGridPosition();
		} else if (enemyHeading < 180 && enemyHeading >= 90) {
			// rechts runter
			this.EnemyX = this.getX() + ankathete;
			this.EnemyY = this.getY() - gegenkathete;
			this.getEnemyGridPosition();
		} else if (enemyHeading < 270 && enemyHeading >= 180) {
			// links runter
			this.EnemyX = this.getX() - ankathete;
			this.EnemyY = this.getY() - gegenkathete;
			this.getEnemyGridPosition();
		} else {
			// links hoch
			this.EnemyX = this.getX() - ankathete;
			this.EnemyY = this.getY() + gegenkathete;
			this.getEnemyGridPosition();
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

	@Override
	public void onDeath(DeathEvent event) {
		this.gameOver = true;
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		this.gameOver = true;
	}

	public boolean getGameOver() {
		return this.gameOver;
	}

	public double getEnemyX() {
		return EnemyX;
	}

	public double getEnemyY() {
		return EnemyY;
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
