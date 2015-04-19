package robot.actionsystem;

import java.util.LinkedList;
import java.util.List;

import robocode.CustomEvent;

public class SerialAction implements Action {

	LinkedList<Action> actions;
	private ActorRobot bot = null;
	private boolean finished;

	public SerialAction(List<Action> actions) {
		this.actions = new LinkedList<>(actions);
		finished = false;
	}

	@Override
	public boolean hasFinished() {
		return finished;
	}

	@Override
	public void start() {
		if (!actions.isEmpty())
			actions.peek().start();
	}

	@Override
	public void stop() {
		if (!actions.isEmpty())
			actions.peek().stop();
	}

	@Override
	public void update() {
		if (actions.isEmpty())
			return;

		boolean changedCurrentAction = false;

		actions.peek().update();

		while (!actions.isEmpty() && actions.peek().hasFinished()) {
			actions.removeFirst();
			changedCurrentAction = true;

			if (!actions.isEmpty())
				actions.peek().update();
		}

		if (!actions.isEmpty() && changedCurrentAction)
			actions.peek().start();

	}

	@Override
	public void update(CustomEvent event) {
		if (!actions.isEmpty())
			actions.peek().update(event);
	}

	@Override
	public void setActor(ActorRobot robot) {
		bot = robot;
		for (Action action : actions)
			action.setActor(bot);
	}
}
