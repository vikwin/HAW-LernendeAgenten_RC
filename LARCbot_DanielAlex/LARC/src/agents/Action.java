package agents;

import robot.LARCRobot;
import utility.Position;

public class Action {

	private static final double NODISTANCE = 0;
	private final double GUN_TURN_STEP = 2.0;
	private final double SHORTDIST = 40.0;
	private final double LONGDIST = Math.sqrt(2 * SHORTDIST * SHORTDIST);

	private int actionID;
	private LARCRobot myRobi;

	public Action(LARCRobot robot) {
		this.myRobi = robot;
	}

	public void setActionID(int actionID) {
		this.actionID = actionID;
	}

	public int getActionID() {
		return actionID;
	}

	public int getFire() {
		return actionID - (getTurnGun() * 18 + getMove() * 2);
	}

	public int getMove() {
		return (actionID - getTurnGun() * 18) / 2;
	}

	public int getTurnGun() {
		return actionID / 18;
	}

	public double[] getMoveVector() {
		double[] moveVector = new double[4];

		double currentHeading = this.myRobi.getCurrentHeading();
		double normHeading = Position.normalizeDegrees(currentHeading);
		switch (getMove()) {
		case 0:
			Position.printdebug("NORD");
			moveVector[0] = this.SHORTDIST;
			moveVector[1] = Position.normalizeDegrees(0 - normHeading);
			break;
		case 1:
			Position.printdebug("NORDOST");
			moveVector[0] = this.LONGDIST;
			moveVector[1] = Position.normalizeDegrees(45 - normHeading);

			break;
		case 2:
			Position.printdebug("OST");
			moveVector[0] = this.SHORTDIST;
			moveVector[1] = Position.normalizeDegrees(90 - normHeading);
			break;
		case 3:
			Position.printdebug("SÜDOST");
			moveVector[0] = this.LONGDIST;
			moveVector[1] = Position.normalizeDegrees(135 - normHeading);
			break;
		case 4:
			Position.printdebug("SÜD");
			moveVector[0] = this.SHORTDIST;
			moveVector[1] = Position.normalizeDegrees(180 - normHeading);
			break;
		case 5:
			Position.printdebug("SÜDWEST");
			moveVector[0] = this.LONGDIST;
			moveVector[1] = Position.normalizeDegrees(-135 - normHeading);
			break;
		case 6:
			Position.printdebug("WEST");
			moveVector[0] = this.SHORTDIST;
			moveVector[1] = Position.normalizeDegrees(-90 - normHeading);
			break;
		case 7:
			Position.printdebug("NORDWEST");
			moveVector[0] = this.LONGDIST;
			moveVector[1] = Position.normalizeDegrees(-45 - normHeading);
			break;
		case 8:
			Position.printdebug("NO_MOVEMENT");
			moveVector[0] = NODISTANCE;
			moveVector[1] = NODISTANCE;
			break;			
		default:
			break;
		}

		moveVector[2] = getFire();

		int turnGun = getTurnGun();
		if (turnGun > 2) {
			moveVector[3] = -(turnGun - 2) * GUN_TURN_STEP;
		} else {
			moveVector[3] = turnGun * GUN_TURN_STEP;
		}
		Position.printdebug("Gunturn: " + moveVector[3]);
		return moveVector;
	}
}
