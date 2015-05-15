package agents;

import robot.LARCRobot;
import utility.Position;

public class Action {

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
		return actionID - (getTurnGun() * 16 + getMove() * 2);
	}

	public int getMove() {
		return (actionID - getTurnGun() * 16) / 2;
	}

	public int getTurnGun() {
		return actionID / 16;
	}

	public double[] getMoveVector() {
		System.out.println("actionID: " + actionID + " Fire: " + getFire() + " Move: " + getMove() + " GunTurn: "
				+ getTurnGun());
		double[] moveVector = new double[4];

		double currentHeading = this.myRobi.getHeading();
		double normHeading = Position.normalizeDegrees(currentHeading);
		switch (getMove()) {
		case 0:
			moveVector[0] = this.SHORTDIST;
			moveVector[1] = Position.normalizeDegrees(0 - normHeading);
			break;
		case 1:
			moveVector[0] = this.LONGDIST;
			moveVector[1] = Position.normalizeDegrees(45 - normHeading);
			break;
		case 2:
			moveVector[0] = this.SHORTDIST;
			moveVector[1] = Position.normalizeDegrees(90 - normHeading);
			break;
		case 3:
			moveVector[0] = this.LONGDIST;
			moveVector[1] = Position.normalizeDegrees(135 - normHeading);
			break;
		case 4:
			moveVector[0] = this.SHORTDIST;
			moveVector[1] = Position.normalizeDegrees(180 - normHeading);
			break;
		case 5:
			moveVector[0] = this.LONGDIST;
			moveVector[1] = Position.normalizeDegrees(-135 - normHeading);
			break;
		case 6:
			moveVector[0] = this.SHORTDIST;
			moveVector[1] = Position.normalizeDegrees(-90 - normHeading);
			break;
		case 7:
			moveVector[0] = this.LONGDIST;
			moveVector[1] = Position.normalizeDegrees(-45 - normHeading);
			break;
		default:
			break;
		}

		moveVector[2] = getFire();

		int turnGun = getTurnGun();
		if (turnGun > 4) {
			moveVector[3] = -(turnGun - 4) * GUN_TURN_STEP;
		} else {
			moveVector[3] = turnGun * GUN_TURN_STEP;
		}
		return moveVector;
	}
}
