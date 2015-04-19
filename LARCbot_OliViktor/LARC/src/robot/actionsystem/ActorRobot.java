package robot.actionsystem;

import java.util.LinkedList;
import java.util.List;

import robocode.AdvancedRobot;
import robocode.Condition;
import robocode.CustomEvent;
import robocode.GunTurnCompleteCondition;
import robocode.MoveCompleteCondition;
import robocode.TurnCompleteCondition;

/**
 * Der ActorRobot ist eine abstrakte Klasse, die einen Eventbasierten Robot
 * darstellt. Dieser kann mit Actions umgehen und aktualisiert diese ständig.
 * Damit das funktioniert, MUSS die implementierende Klasse in jedem Durchlauf
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
		createEvents();
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

	private void createEvents() {

		// Bewegungs Conditiopns
		MoveCompleteCondition movCond = new MoveCompleteCondition(this);
		movCond.setName("botmove_completed");
		this.addCustomEvent(movCond);

		TurnCompleteCondition turnCond = new TurnCompleteCondition(this);
		turnCond.setName("botturn_completed");
		this.addCustomEvent(turnCond);

		// Angriffs Conditions
		GunTurnCompleteCondition gunTurnCond = new GunTurnCompleteCondition(
				this);
		gunTurnCond.setName("gunturn_completed");
		this.addCustomEvent(gunTurnCond);

		this.addCustomEvent(new Condition("gunfire_completed") {

			@Override
			public boolean test() {
				return firing && getGunHeat() == 0;
			}
		});

	}

	@Override
	public void onCustomEvent(CustomEvent event) {
		actions.peek().update(event);
	}

}
