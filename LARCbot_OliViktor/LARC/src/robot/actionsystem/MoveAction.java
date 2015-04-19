package robot.actionsystem;

import robocode.CustomEvent;

public class MoveAction implements Action{

	private double distance;
	private ActorRobot bot = null;
	private boolean finished;
	
	public MoveAction(double distance) {
		this.distance = distance;
		finished = false;
	}
	
	
	@Override
	public boolean hasFinished() {
		return finished;
	}

	@Override
	public void start() {
		bot.setAhead(distance);
	}

	@Override
	public void stop() {
		bot.setBack(0);	
	}

	
	@Override
	public void update() {
		if (bot.getDistanceRemaining() == 0)
			finished = true;
		
	}
	
	@Override
	public void update(CustomEvent event) {
		if (event.getCondition().getName().equals("botmove_completed")) {
			finished = true;
		}
	}

	@Override
	public void setActor(ActorRobot robot) {
		bot = robot;
		
	}

}
