package robot.actionsystem;

/**
 * Abstrakte Klasse für Action Objekte. Eine Action ist eine atomare Handlung,
 * die ein ActorRobot durchführen kann.
 * 
 * @author Viktor Winkelmann
 *
 */

public abstract class Action {

	boolean started, finished;
	ActorRobot bot;

	public Action() {
		started = false;
		finished = false;
		bot = null;
	}

	public boolean hasFinished() {
		return finished;
	}

	public abstract void start();

	public abstract void stop();

	public abstract void update();

	public void setActor(ActorRobot robot) {
		bot = robot;
	}

	public abstract String toString();

}
