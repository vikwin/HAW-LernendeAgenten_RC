package robot.actionsystem;

import java.util.LinkedList;
import java.util.List;

import robocode.CustomEvent;

public class ConcurrentAction implements Action {

	LinkedList<Action> actions;
	private ActorRobot bot = null;
	private boolean finished;

	public ConcurrentAction(List<Action> actions) {
		this.actions = new LinkedList<>(actions);
		finished = false;
	}

	@Override
	public boolean hasFinished() {
		return finished;
	}

	@Override
	public void start() {
		for (Action action : actions)
			action.start();
	}

	@Override
	public void stop() {
		for (Action action : actions)
			action.stop();
	}

	@Override
	public void update() {
		finished = true;
		for (Action action : actions) {
			action.update();
			if (!action.hasFinished())
				finished = false;
		}
	}

	@Override
	public void update(CustomEvent event) {
		finished = true;
		for (Action action : actions) {
			action.update(event);
			if (!action.hasFinished())
				finished = false;
		}
	}

	@Override
	public void setActor(ActorRobot robot) {
		bot = robot;
		for (Action action : actions)
			action.setActor(bot);
	}
}
