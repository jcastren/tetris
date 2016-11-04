package fi.joonas.tetris;

/*
 * This class implements one coordinate
 */
public class Coordinate {

	private int x;
	
	private int y;
	
	private String blockId;
	
	@Override
	public String toString() {
		return "Coordinate [x=" + x + ", y=" + y + ", blockId=" + blockId + "]";
	}

	public Coordinate(int x, int y, String blockId) {
		this.x = x;
		this.y = y;
		this.blockId = blockId;
	}
	
	public void increaseCoords(int maxX, int maxY) throws CoordOutOfBoundsException {
		setX(getX() +1);
		
		if (getX() > maxX) {
			setX(0);
			setY(getY() + 1);
			if (getY() > maxY) {
				throw new CoordOutOfBoundsException(String.format("increasing coords failed, %s bigger than maxY: %s", getY(), maxY));
			}
		}
	}
	
	public Coordinate clone() {
		Coordinate newCoord = new Coordinate(getX(), getY(), getBlockId());
		return newCoord;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public String getBlockId() {
		return blockId;
	}

	public void setBlockId(String blockId) {
		this.blockId = blockId;
	}
	
	
}
