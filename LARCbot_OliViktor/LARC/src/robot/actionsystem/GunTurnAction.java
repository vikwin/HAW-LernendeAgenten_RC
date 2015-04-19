package robot.actionsystem;

import robocode.CustomEvent;

public class GunTurnAction implements Action {

	private double angle;
	private ActorRobot bot = null;
	private boolean finished;
	
	public GunTurnAction(double degrees) {
		angle = degrees;
		finished = false;
	}
	
	@Override
	public boolean hasFinished() {
		return finished;
	}

	@Override
	public void start() {
		bot.setTurnGunRight(angle);
		
	}

	@Override
	public void stop() {
		bot.setTurnGunLeft(0);
	}

	@Override
	public void update() {
		if (bot.getGunTurnRemaining() == 0)
			finished = true;
		
	}
	
	@Override
	public void update(CustomEvent event) {
		if (event.getCondition().getName().equals("gunturn_completed")) {
			finished = true;
		}
	}

	@Override
	public void setActor(ActorRobot robot) {
		bot = robot;
	}

}
