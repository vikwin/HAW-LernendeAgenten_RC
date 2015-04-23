package environment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Collection;

import robocode.AdvancedRobot;
import utils.Utils;
import utils.Vector2D;

public class AttackEnvironment implements Environment {

	private int ringDiameter;
	private Vector2D selfPosition;
	private double gunHeading;

	private AttackEnvElement[][] ringStructure;

	public AttackEnvironment(int ringDiameter, int ringElementSize,
			Vector2D selfPosition, double gunHeading, int fieldWidth,
			int fieldHeight) {
		this.ringDiameter = ringDiameter;
		this.selfPosition = selfPosition;
		this.gunHeading = gunHeading;

		ringStructure = new AttackEnvElement[(int) Math.max(fieldWidth
				/ ringDiameter, fieldHeight / ringDiameter)][];
		System.out.printf("AttackEnvironment hat %d Ringe.\n",
				ringStructure.length);

		for (int i = 0; i < ringStructure.length; i++) {
			double ringWidth = 2 * i * ringDiameter + ringDiameter;
			int anzahlFelder = (int) Utils.circlePerimeter(ringWidth / 2)
					/ ringElementSize;

			System.out.printf("AttackEnvironment Ring %d hat %d Felder.\n", i,
					anzahlFelder);
			ringStructure[i] = new AttackEnvElement[anzahlFelder];
		}

		clearEnvironment();
	}

	@Override
	public void update(Collection<Enemy> enemies, AdvancedRobot selfBot) {
		selfPosition = Utils.getBotCoordinates(selfBot);
		gunHeading = selfBot.getGunHeading();
		clearEnvironment();

		for (Enemy enemy : enemies) {
			int[] field = getFieldByVector(enemy.getPosition());
			System.out.printf("Errechnetes Gegnerfeld bei [%d,%d]\n", field[0],
					field[1]);
			ringStructure[field[0]][field[1]] = AttackEnvElement.ENEMY;
		}
	}

	/**
	 * Berechnet die Feldindizes, in denen sich ein absoluter Ortsvektor
	 * befindet.
	 * 
	 * @return [Ringindex, Feldindex]
	 */
	private int[] getFieldByVector(Vector2D vector) {
		Vector2D relativePosition = vector.subtract(selfPosition).rotate(
				-gunHeading);
		double distance = relativePosition.length();
		double direction = relativePosition.getHeading();

		System.out.printf("Gegner liegt in Winkel %f\n", direction);

		int ringIndex = (int) ((distance - ringDiameter / 2) / ringDiameter);
		if (ringIndex >= ringStructure.length) // Sonderfall: Gegner ist weiter
												// entfernt als RingStructure
												// zulässt
			ringIndex = ringStructure.length - 1;
		int fieldIndex = (int) (direction / (360.0 / ringStructure[ringIndex].length));
		if (direction % (360.0 / ringStructure[ringIndex].length) == 0) // Sonderfall:
																		// Position
																		// direkt
																		// auf
																		// "Trennlinie"
			fieldIndex--;

		return new int[] { ringIndex, fieldIndex };
	}

	private void clearEnvironment() {
		for (int i = 0; i < ringStructure.length; i++)
			for (int j = 0; j < ringStructure[i].length; j++)
				ringStructure[i][j] = AttackEnvElement.EMPTY;
	}

	@Override
	public void doPaint(Graphics2D g) {
		// Zeichne die Blickrichtung als grünen Punkt ein
		g.setColor(new Color(0x00, 0xff, 0x00, 0x80));
		Vector2D aim = selfPosition.add((new Vector2D(0, ringDiameter))
				.rotate(gunHeading));
		g.fillArc((int) (aim.getX() - 7.5), (int) (aim.getY() - 7.5), 15, 15,
				0, 360);

		// Zeichne die Ringstruktur und die Felder in gelb ein
		g.setColor(new Color(0xdf, 0xff, 0x00, 0x80));
		// Zeichne zunächst einen Ring um den Bot selbst
		g.drawArc((int) selfPosition.getX() - ringDiameter / 2,
				(int) selfPosition.getY() - ringDiameter / 2, ringDiameter,
				ringDiameter, 0, 360);
		// Zeichne alle anderen Ringe und die Felder
		int ringIndex, fieldIndex, circleSize;
		double rotation;
		Vector2D vectorA, vectorB;
		for (ringIndex = 0; ringIndex < ringStructure.length; ringIndex++) {
			circleSize = ringDiameter + (ringIndex + 1) * ringDiameter * 2;
			g.drawArc((int) selfPosition.getX() - circleSize / 2,
					(int) selfPosition.getY() - circleSize / 2, circleSize,
					circleSize, 0, 360);
			for (fieldIndex = 0; fieldIndex < ringStructure[ringIndex].length; fieldIndex++) {
				vectorA = new Vector2D(0, ringDiameter / 2 + ringIndex
						* ringDiameter);
				vectorB = vectorA.add(new Vector2D(0, ringDiameter));
				rotation = gunHeading + fieldIndex * 360.0
						/ ringStructure[ringIndex].length;
				vectorA = selfPosition.add(vectorA.rotate(rotation));
				vectorB = selfPosition.add(vectorB.rotate(rotation));

				g.drawLine((int) vectorA.getX(), (int) vectorA.getY(),
						(int) vectorB.getX(), (int) vectorB.getY());
			}
		}

		// Zeichne die Felder in denen sich ein Gegner befindet rot ein
		g.setColor(AttackEnvElement.ENEMY.getColor());
		for (int i = 0; i < ringStructure.length; i++)
			for (int j = 0; j < ringStructure[i].length; j++)
				if (ringStructure[i][j] == AttackEnvElement.ENEMY) {
					g.fillPolygon(getPolygonByField(i, j));
				}

	}

	public Polygon getPolygonByField(int ringIndex, int fieldIndex) {
		Polygon p = new Polygon();
		Vector2D workVector;

		// Punkt unten links berechnen und einfügen
		workVector = new Vector2D(0, ringDiameter / 2 + ringIndex
				* ringDiameter);
		workVector = workVector.rotate(gunHeading + fieldIndex * 360.0
				/ ringStructure[ringIndex].length);
		workVector = selfPosition.add(workVector);
		p.addPoint((int) workVector.getX(), (int) workVector.getY());

		// Punkt unten rechts berechnen und einfügen
		workVector = new Vector2D(0, ringDiameter / 2 + ringIndex
				* ringDiameter);
		workVector = workVector.rotate(gunHeading + (fieldIndex + 1) * 360.0
				/ ringStructure[ringIndex].length);
		workVector = selfPosition.add(workVector);
		p.addPoint((int) workVector.getX(), (int) workVector.getY());

		// Punkt oben rechts berechnen und einfügen
		workVector = new Vector2D(0, ringDiameter / 2 + (ringIndex + 1)
				* ringDiameter);
		workVector = workVector.rotate(gunHeading + (fieldIndex + 1) * 360.0
				/ ringStructure[ringIndex].length);
		workVector = selfPosition.add(workVector);
		p.addPoint((int) workVector.getX(), (int) workVector.getY());

		// Punkt oben links berechnen und einfügen
		workVector = new Vector2D(0, ringDiameter / 2 + (ringIndex + 1)
				* ringDiameter);
		workVector = workVector.rotate(gunHeading + fieldIndex * 360.0
				/ ringStructure[ringIndex].length);
		workVector = selfPosition.add(workVector);
		p.addPoint((int) workVector.getX(), (int) workVector.getY());

		return p;
	}

	/**
	 * Diese Methode geht davon aus, dass wir nur einen einzigen Gegner haben!
	 * Sollte es notwendig sein mehrere Gegner zu berücksichtigen MUSS diese
	 * Methode für ein korrektes Verhalten angepasst werden.
	 */
	@Override
	public int getId() {
		int id = 0, i = 0, j = 0;

		for (i = 0; i < ringStructure.length; i++) {
			for (j = 0; j < ringStructure[i].length; j++) {
				if (ringStructure[i][j] == AttackEnvElement.ENEMY) {
					System.out.printf("AttackEnv ID %d bei i=%d und j=%d\n", id
							+ j, i, j);
					return id + j;
				}
			}
			id += j;
		}
		System.out.printf("AttackEnv ID %d bei i=%d und j=%d\n", id, i, j);
		return id;
	}

	@Override
	public int getStateCount() {
		int count = 0;

		for (int i = 0; i < ringStructure.length; i++)
			count += ringStructure[i].length;

		return count + 1;
	}

}
