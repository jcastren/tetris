package fi.valagroup.joonas;

import java.util.ArrayList;
import java.util.List;

public class TetrisBlock {
	
	private List<Coordinate> coords;
	
	public void addCoordinate(Coordinate coord) {
		if (coords == null) {
			coords = new ArrayList<Coordinate>();
		}
		coords.add(coord);
	}

	public List<Coordinate> getCoords() {
		return coords;
	}

	public void setCoords(List<Coordinate> coords) {
		this.coords = coords;
	}

}
