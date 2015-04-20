package environment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;

import robocode.AdvancedRobot;
import utils.Utils;
import utils.Vector2D;

public class AttackEnvironment implements Environment {

	private int ringDiameter, ringElementSize, fieldWidth, fieldHeight;
	private Vector2D selfPosition;
	private double gunHeading;

	private AttackEnvElement[][] ringStructure;

	
	// Aktuell 623 Felder
	public AttackEnvironment(int ringDiameter, int ringElementSize,
			Vector2D selfPosition, double gunHeading, int fieldWidth,
			int fieldHeight) {
		this.ringDiameter = ringDiameter;
		this.ringElementSize = ringElementSize;
		this.selfPosition = selfPosition;
		this.gunHeading = gunHeading;
		this.fieldWidth = fieldWidth;
		this.fieldHeight = fieldHeight;

		ringStructure = new AttackEnvElement[(int) Math.max(fieldWidth
				/ ringDiameter, fieldHeight / ringDiameter)][];
		System.out.printf("AttackEnvironment hat %d Ringe.\n",
				ringStructure.length);

		for (int i = 0; i < ringStructure.length; i++) {
			double ringWidth = 2 * i * ringDiameter + ringDiameter;
			int anzahlFelder = (int) Utils.circlePerimeter(ringWidth / 2)
			/ ringElementSize;
		
			System.out.printf("AttackEnvironment Ring %d hat %d Felder.\n",
					i+1, anzahlFelder);
			ringStructure[i] = new AttackEnvElement[anzahlFelder];
		}
		
		clearEnvironment();
	}

	@Override
	public void update(Collection<Enemy> enemies, AdvancedRobot selfBot) {
		clearEnvironment();
			
		for (Enemy enemy : enemies) {
			//TODO
		}
		
		// TODO: Felder updaten
	}
	
	
	/**
	 * Berechnet die Feldindizes, in dem sich ein absoluter Ortsvektor befindet.
	 * @return [Ringindex, Feldindex]
	 */
	private int[] getFieldByVector(Vector2D vector) {
		// TODO
		return new int[1];
	}

	private void clearEnvironment() {
		for (int i = 0; i < ringStructure.length; i++)
			for (int j = 0; j < ringStructure[i].length; j++)
				ringStructure[i][j] = AttackEnvElement.EMPTY;
	}

	@Override
	public void doPaint(Graphics2D g) {
		Vector2D source, line, target;

		// Zeichne die Blickrichtung als grünen Punkt ein
		g.setColor(new Color(0x00, 0xff, 0x00, 0x80));
		line = selfPosition.add((new Vector2D(0, ringDiameter))
				.rotate(gunHeading));
		g.fillArc((int) (line.getX() - 7.5), (int) (line.getY() - 7.5), 15, 15,
				0, 360);

		// Zeichne die Ringe in gelb ein
		g.setColor(new Color(0xdf, 0xff, 0x00, 0x80));
		double width;
		for (width = ringDiameter; width < fieldWidth * 2
				|| width < fieldHeight * 2; width += 2 * ringDiameter) {
			g.drawArc((int) (selfPosition.getX() - width / 2),
					(int) (selfPosition.getY() - width / 2), (int) width,
					(int) width, 0, 360);

			int elementCount = (int) Utils.circlePerimeter(width / 2)
					/ ringElementSize;

			for (double alpha = 0; alpha < 360; alpha += 360.0 / elementCount) {

				source = selfPosition.add((new Vector2D(0, width / 2))
						.rotate(gunHeading + alpha));
				line = new Vector2D(0, ringDiameter).rotate(gunHeading + alpha);
				target = source.add(line);

				g.drawLine((int) source.getX(), (int) source.getY(),
						(int) target.getX(), (int) target.getY());

			}
		}
		g.drawArc((int) (selfPosition.getX() - width / 2),
				(int) (selfPosition.getY() - width / 2), (int) width,
				(int) width, 0, 360);

		// TODO: Felder mit Gegnern rot markieren

	}

	
	/**
	 * Diese Methode geht davon aus, dass wir nur einen einzigen Gegner haben!
	 * Sollte es notwendig sein mehrere Gegner zu berücksichtigen MUSS diese Methode
	 * für ein korrektes Verhalten angepasst werden.
	 */
	@Override
	public int getId() {
		int id = 0, i = 0, j = 0;
		
		
		for (i = 0; i < ringStructure.length; i++) {
			id += i * ringStructure[i].length;
			for (j = 0; j < ringStructure[i].length; j++)
				if (ringStructure[i][j] == AttackEnvElement.ENEMY)
					return id + j;
		}
		
		return id + j + 1;
	}

}
