package fi.valagroup.joonas;

import java.util.ArrayList;
import java.util.List;

public class TetrisBlock {
	
	private String blockId;
	
	private List<Coordinate> coords;
	
	private boolean fitsTheArray = false;
	
	public void addCoordinate(Coordinate coord) {
		if (coords == null) {
			coords = new ArrayList<Coordinate>();
		}
		coords.add(coord);
	}

	public List<Coordinate> getCoords() {
		return coords;
	}

	public TetrisBlock(String blockId) {
		this.blockId = blockId;
	}

	public void setCoords(List<Coordinate> coords) {
		this.coords = coords;
	}

	public String getBlockId() {
		return blockId;
	}

	public void setBlockId(String blockId) {
		this.blockId = blockId;
	}

	public boolean isFitsTheArray() {
		return fitsTheArray;
	}

	public void setFitsTheArray(boolean fitsTheArray) {
		this.fitsTheArray = fitsTheArray;
	}

}
