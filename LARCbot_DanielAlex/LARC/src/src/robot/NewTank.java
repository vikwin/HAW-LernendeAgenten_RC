package src.robot;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class NewTank extends AdvancedRobot {

	public void run() {
		while (true) {
			setTurnGunRight(360);
			setAhead(100);
			setTurnLeft(100);
			execute();
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		setStop();
		execute();
		setFire(1);
		execute();
		setResume();
		execute();
	}
}
