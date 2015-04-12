package environment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;

import robocode.AdvancedRobot;
import utils.Utils;
import utils.Vector2D;

public class MoveEnvironment implements Environment {

	private MoveEnvElement[][] field;
	private int robotSize, gridSize, fieldWidth, fieldHeight;

	public MoveEnvironment(int robotSize, int gridSize, int fieldWidth,
			int fieldHeight) {
		this.robotSize = robotSize;
		this.gridSize = gridSize;
		this.fieldWidth = fieldWidth;
		this.fieldHeight = fieldHeight;

		field = new MoveEnvElement[fieldWidth / gridSize][fieldHeight
				/ gridSize];

		clearField();
	}

	private void clearField() {
		for (int x = 0; x < field.length; x++)
			for (int y = 0; y < field[x].length; y++) {
				field[x][y] = MoveEnvElement.EMPTY;
			}
	}

	private void setElement(Vector2D position, int width, int height,
			MoveEnvElement elementType) {
		Vector2D rect2UL = new Vector2D(position.getX() - width/2, position.getY() + width/2);
		Vector2D rect2LR = new Vector2D(position.getX() + width/2, position.getY() - width/2);
		
		for (int x = 0; x < field.length; x++)
				for (int y = 0; y < field[x].length; y++) {
					Vector2D rect1UL = new Vector2D(x * gridSize, y * gridSize + gridSize);
					Vector2D rect1LR = new Vector2D(x * gridSize + gridSize, y * gridSize);
					
					if (Utils.checkRectOverlap(rect1UL, rect1LR, rect2UL, rect2LR))
						field[x][y] = elementType;
				}
					
	}

	@Override
	public void update(Collection<Enemy> enemies, AdvancedRobot selfBot) {
		clearField();

		for (Enemy enemy : enemies) {
			setElement(enemy.getPosition(), 1, 1,
					MoveEnvElement.ENEMY);
		}

		setElement(Utils.getBotCoordinates(selfBot), 1, 1, MoveEnvElement.SELF);
	}

	@Override
	public void doPaint(Graphics2D g) {
		// Zeichne die Gitterlinien in grün ein
		g.setColor(new Color(0x00, 0xff, 0x00, 0x80));
		for (int x = 0; x <= fieldWidth; x += gridSize) 
			g.drawLine(x, 0, x, fieldHeight);
		for (int y = 0; y <= fieldHeight; y += gridSize) 
			g.drawLine(0, y, fieldWidth, y);
		
		
		// Zeichne Felder ein
		for (int x = 0; x < field.length; x++)
			for (int y = 0; y < field[x].length; y++)
				if (field[x][y] != MoveEnvElement.EMPTY) {
					g.setColor(field[x][y].getColor());
					g.fillRect(x * gridSize, y * gridSize, gridSize, gridSize);
				}				
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
