package robot.actionsystem;

import java.util.LinkedList;
import java.util.List;

/**
 * Wrapper Klasse zur Realisierung mehrerer gleichzeitig auszuführender Actions.
 * @author Viktor Winkelmann
 *
 */
public class ConcurrentAction extends Action {

	LinkedList<Action> actions;

	public ConcurrentAction(List<Action> actions) {
		this.actions = new LinkedList<>(actions);
	}

	@Override
	public void start() {
		started = true;
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
		if (!started || actions.isEmpty())
			return;

		finished = true;
		for (Action action : actions) {
			action.update();
			if (!action.hasFinished())
				finished = false;
		}
	}

	@Override
	public void setActor(ActorRobot robot) {
		super.setActor(robot);
		for (Action action : actions)
			action.setActor(bot);
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder("[");

		for (Action action : actions)
			string.append(action.toString() + ", ");

		if (string.length() > 1)
			string.delete(string.length() - 2, string.length());
		string.append("]");

		return String.format("ConcurrentAction mit Inhalt: %s", string);
	}
}
