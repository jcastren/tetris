package fi.valagroup.joonas;

import java.util.ArrayList;
import java.util.List;

/** Class implements the actual Tetris array to which Tetris blocks are put */
public class TetrisArray {

	private int width;
	
	private int height;
	
	private List<List<Coordinate>> coords;

	public TetrisArray(int width, int height) {
		this.width = width;
		this.height = height;
		
		coords = new ArrayList<List<Coordinate>>();
		
		// Initialize array with coordinates
		for (int x=0; x<width; x++) {
			List<Coordinate> yCoordinates = new ArrayList<Coordinate>();
			
			for (int y=0; y<height; y++) {
				yCoordinates.add(new Coordinate(x, y, null));
			}
			coords.add(yCoordinates);
		}
	}
	
	public Coordinate getCoord(int x, int y) {
		Coordinate coord = null;
		
		if (coordsExist(x, y)) {
			coord = getCoords().get(x).get(y);
		}
		
		return coord;
	}
	
	public boolean coordsExist(int x, int y) {
		boolean exists = false;
		
		if (x >= 0 && x < getCoords().size() && y >=0 && y < getCoords().get(x).size()) {
			exists = true;
		}

		return exists;
	}
	
	public List<List<Coordinate>> getCoords() {
		return coords;
	}

	public void setCoords(List<List<Coordinate>> coords) {
		this.coords = coords;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
}
