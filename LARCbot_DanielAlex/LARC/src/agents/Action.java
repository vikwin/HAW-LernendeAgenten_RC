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
		System.out.println(" Move: " + getMove());
		double[] moveVector = new double[4];

		double currentHeading = this.myRobi.getCurrentHeading();
		double normHeading = Position.normalizeDegrees(currentHeading);
		switch (getMove()) {
		case 0:
			moveVector[0] = this.SHORTDIST;
			System.out.println("Norm Input: " + normHeading);
			System.out.println("Osterrechnung: " + (0 - normHeading));
			moveVector[1] = Position.normalizeDegrees(0 - normHeading);
			System.out.println("New Norm: " + moveVector[1]);
			System.out.println();
			break;
		case 1:
			moveVector[0] = this.LONGDIST;
			System.out.println("Norm Input: " + normHeading);
			System.out.println("Osterrechnung: " + (45 - normHeading));
			moveVector[1] = Position.normalizeDegrees(45 - normHeading);
			System.out.println("New Norm: " + moveVector[1]);
			System.out.println();
			break;
		case 2:
			moveVector[0] = this.SHORTDIST;
			System.out.println("Norm Input: " + normHeading);
			System.out.println("Osterrechnung: " + (90 - normHeading));
			moveVector[1] = Position.normalizeDegrees(90 - normHeading);
			System.out.println("New Norm: " + moveVector[1]);
			System.out.println();
			break;
		case 3:
			moveVector[0] = this.LONGDIST;
			System.out.println("Norm Input: " + normHeading);
			System.out.println("Osterrechnung: " + (135 - normHeading));
			moveVector[1] = Position.normalizeDegrees(135 - normHeading);
			System.out.println("New Norm: " + moveVector[1]);
			System.out.println();
			break;
		case 4:
			moveVector[0] = this.SHORTDIST;
			System.out.println("Norm Input: " + normHeading);
			System.out.println("Osterrechnung: " + (180 - normHeading));
			moveVector[1] = Position.normalizeDegrees(180 - normHeading);
			System.out.println("New Norm: " + moveVector[1]);
			System.out.println();
			break;
		case 5:
			moveVector[0] = this.LONGDIST;
			System.out.println("Norm Input: " + normHeading);
			System.out.println("Osterrechnung: " + (-135 - normHeading));
			moveVector[1] = Position.normalizeDegrees(-135 - normHeading);
			System.out.println("New Norm: " + moveVector[1]);
			System.out.println();
			break;
		case 6:
			moveVector[0] = this.SHORTDIST;
			System.out.println("Norm Input: " + normHeading);
			System.out.println("Osterrechnung: " + (-90 - normHeading));
			moveVector[1] = Position.normalizeDegrees(-90 - normHeading);
			System.out.println("New Norm: " + moveVector[1]);
			System.out.println();
			break;
		case 7:
			moveVector[0] = this.LONGDIST;
			System.out.println("Norm Input: " + normHeading);
			System.out.println("Osterrechnung: " + (-45 - normHeading));
			moveVector[1] = Position.normalizeDegrees(-45 - normHeading);
			System.out.println("New Norm: " + moveVector[1]);
			System.out.println();
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
