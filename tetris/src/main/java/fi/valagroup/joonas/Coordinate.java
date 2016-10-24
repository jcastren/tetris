package fi.valagroup.joonas;

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
		super();
		this.x = x;
		this.y = y;
		this.blockId = blockId;
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
