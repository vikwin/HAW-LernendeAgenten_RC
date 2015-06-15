package robot.actionsystem;

/**
 * NothingAction das Nichtstun und somit das warten um eine Anzahl von Sekunden dar.
 * @author Oliver Niebsch
 *
 */
public class NothingAction extends Action {
	private static final int WAITING_TICKS = 15;

	private int waitingTicks, ticksCounter;

	public NothingAction() {
		waitingTicks = WAITING_TICKS;
	}
	
	public NothingAction(int ticksToWait) {
		waitingTicks = ticksToWait;
	}

	@Override
	public void start() {
		if (!started) {
			ticksCounter = 0;
			started = true;
		}
	}

	@Override
	public void stop() {
		if (started)
			waitingTicks = 0;
	}

	@Override
	public void update() {
		if (started && ++ticksCounter > waitingTicks)
			finished = true;
	}

	@Override
	public String toString() {
		return String.format("NothingAction für %d Nillisekunden", waitingTicks);
	}

}
