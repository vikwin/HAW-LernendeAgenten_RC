package robot;

import java.awt.Graphics2D;

import environment.LARCEnvironment;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class LARCRobot extends AdvancedRobot {

	public LARCEnvironment environment;

	public LARCRobot() {
		this.environment = new LARCEnvironment(this);
	}

	@Override
	public void run() {
		setTurnGunLeft(360);
		execute();
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		setFire(1);
		execute();
	}

	@Override
	public void onPaint(Graphics2D g) {
		// Set the paint color to red
		g.setColor(java.awt.Color.RED);
		// draw grid
		for (int i = 0; i < this.getBattleFieldWidth(); i += LARCEnvironment.tileSize) {
			for (int j = 0; j < this.getBattleFieldHeight(); j += LARCEnvironment.tileSize) {

				g.drawRect(i, j, ((int) this.getHeight()),
						((int) this.getHeight()));
			}
		}
	}
}
