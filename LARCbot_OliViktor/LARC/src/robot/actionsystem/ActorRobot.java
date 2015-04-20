package robot.actionsystem;

import java.util.LinkedList;
import java.util.List;

import robocode.AdvancedRobot;

/**
 * Der ActorRobot ist eine abstrakte Robot Klasse. Dieser kann mit Actions 
 * umgehen und aktualisiert diese ständig.Damit das funktioniert, MUSS die 
 * implementierende Klasse in jedem Durchlauf, also in der Hauptschleife,
 * updateActions() aufrufen.
 * 
 * Die Actions werden alle nacheinander "abgearbeitet". Ist eine Action zuende,
 * wird diese vergessen.
 * 
 * @author Viktor Winkelmann
 *
 */
public abstract class ActorRobot extends AdvancedRobot {

	private LinkedList<Action> actions;
	public boolean firing;

	public ActorRobot() {
		firing = false;
		actions = new LinkedList<Action>();
	}

	@Override
	public void run() {
		//createEvents();
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

		System.out.println("Aktuelle Action: " + actions.peek().toString());
		
		actions.peek().start();
		actions.peek().update(); // implizites Update durch Events

		if (actions.peek().hasFinished())
			actions.removeFirst();

		if (!actions.isEmpty())
			actions.peek().start();
		}
}
