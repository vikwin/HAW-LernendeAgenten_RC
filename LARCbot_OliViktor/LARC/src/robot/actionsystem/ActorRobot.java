package robot.actionsystem;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import robocode.AdvancedRobot;

/**
 * Der ActorRobot ist eine abstrakte Robot Klasse. Dieser kann mit Actions 
 * umgehen und aktualisiert diese ständig.Damit das funktioniert, MUSS die 
 * implementierende Klasse in jedem Durchlauf, also in der Hauptschleife,
 * updateActions() aufrufen.
 * 
 * Die Actions werden zeitgleich "abgearbeitet". Ist eine Action zuende,
 * wird diese vergessen. Sollen Actions nacheinander ausgeführt werden,
 * müssen diese in einer SerialAction zusammengefasst werden.
 * 
 * @author Viktor Winkelmann
 *
 */
public abstract class ActorRobot extends AdvancedRobot {

	private LinkedList<Action> actions;

	public ActorRobot() {
		actions = new LinkedList<Action>();
	}

	/**
	 * Liefert eine Liste aller noch bestehenden Actions.
	 * 
	 * @return Action Liste
	 */
	public List<Action> allActions() {
		return actions;
	}

	/**
	 * Fügt dem Bot eine Action hinzu.
	 * 
	 * @param action
	 */
	public void addAction(Action action) {
		action.setActor(this);
		actions.add(action);

		if (actions.size() == 1)
			action.start();
	}

	/**
	 * Stoppt alle laufenden Actions und löscht alle zukünftigen.
	 */
	public void clearActions() {
		for (Action action : actions) {
			action.stop();
		}

		actions.clear();
	}

	public void updateActions() {
		if (actions.isEmpty())
			return;
		
		
		for (Action action : actions) {
			action.start();
			action.update();
		}
		
		actions.removeIf(new Predicate<Action>() {

			@Override
			public boolean test(Action action) {
				return action.hasFinished();
			}
			
		});
		}
}
