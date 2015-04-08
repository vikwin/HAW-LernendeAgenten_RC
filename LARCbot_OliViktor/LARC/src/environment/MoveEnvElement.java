package environment;

import java.awt.Color;

public enum MoveEnvElement {
	SELF, EMPTY, ENEMY;

	public Color getColor() {
		switch (this) {
		case SELF:
			return new Color(0x00, 0x00, 0xff, 0x80);
		case ENEMY:
			return new Color(0xff, 0x00, 0x00, 0x80);
		default:
			return new Color(0x00, 0x00, 0x00, 0xff);
		}
	}
}
