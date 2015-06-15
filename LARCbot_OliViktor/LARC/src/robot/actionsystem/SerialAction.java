package robot.actionsystem;

import java.util.LinkedList;
import java.util.List;

/**
 * SerialAction stellt einen Wrapper für nacheinander auszuführende Actions dar.
 * @author Viktor Winkelmann
 *
 */
public class SerialAction extends Action {

	LinkedList<Action> actions;

	public SerialAction(List<Action> actions) {
		this.actions = new LinkedList<>(actions);
	}

	@Override
	public void start() {
		started = true;
		if (!started && !actions.isEmpty()) 
			actions.peek().start();
	}

	@Override
	public void stop() {
		if (started && !actions.isEmpty())
			actions.peek().stop();
	}

	@Override
	public void update() {
		if (!started)
			return;

		if (actions.isEmpty()) {
			finished = true;
			return;
		}

		actions.peek().start();
		actions.peek().update();
		updateQueue();
	}

	private void updateQueue() {
		if (actions.peek().hasFinished())
			actions.removeFirst();

		if (!actions.isEmpty())
			actions.peek().start();
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

		return String.format("SerialAction mit Inhalt: %s", string);
	}

}
