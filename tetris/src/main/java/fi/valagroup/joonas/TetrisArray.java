package fi.valagroup.joonas;

import java.util.List;

public class TetrisArray {

	private int width;
	
	private int height;
	
	//private List<List<Coordinate>> arrayOfCoords;
	
	Coordinate[][] arrayOfCoords;

	public TetrisArray(int height, int width) {
		this.height = height;
		this.width = width;
		
		for (int i=1; i<=width; i++) {
			for (int j=1; j<height; j++) {
				Coordinate coord = new Coordinate(i, j, null);
				//arrayOfCoords[i, j] = coord;
			}
		}
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
