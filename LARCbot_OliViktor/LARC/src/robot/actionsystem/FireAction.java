package robot.actionsystem;

import robocode.CustomEvent;

public class FireAction implements Action {

	private double angle;
	private ActorRobot bot = null;
	private boolean finished;
	
	public FireAction(double degrees) {
		angle = degrees;
		finished = false;
	}
	
	@Override
	public boolean hasFinished() {
		return finished;
	}

	@Override
	public void start() {
		bot.setTurnRight(angle);
		
	}

	@Override
	public void stop() {
		bot.setTurnLeft(0);
	}

	@Override
	public void update() {
		if (bot.getTurnRemaining() == 0)
			finished = true;
		
	}
	
	@Override
	public void update(CustomEvent event) {
		if (event.getCondition().getName().equals("botturn_completed")) {
			finished = true;
		}
	}

	@Override
	public void setActor(ActorRobot robot) {
		bot = robot;
	}

}
