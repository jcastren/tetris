package fi.valagroup.joonas;

import java.util.ArrayList;
import java.util.List;

/** Class implements the actual Tetris array to which Tetris blocks are put */
public class TetrisArray {

	private int width;
	
	private int height;
	
	private List<List<Coordinate>> arrayOfCoords;
	
	//private static final String RESERVED = "RESERVED";
	
	//Coordinate[][] arrayOfCoords;

	public TetrisArray(int width, int height) {
		this.width = width;
		this.height = height;
		
		arrayOfCoords = new ArrayList<List<Coordinate>>();
		
		// Initialize array with coordinates
		for (int x=0; x<width; x++) {
			List<Coordinate> yCoordinates = new ArrayList<Coordinate>();
			
			for (int y=0; y<height; y++) {
				yCoordinates.add(new Coordinate(x, y, null));
			}
			arrayOfCoords.add(yCoordinates);
		}
	}
	
	/**
	 * Etsi palikan ensimmäiselle koordinaatille vapaa paikka
	 * Kun vapaa paikka on löytynyt, rupea käsittelemään palikan muita osia:
	 * 	Luuppi (niin kauan kuin palikan muita osia jäljellä)
	 * 		Tutki onko palikan seuraavan osan koordinaateille taulukossa tilaa
	 * 		Jos on, siirry palikan seuraavaan osaan
	 * 		Muuten, palaa etsimään palikan ensimmäiselle osalle vapaata paikkaa
	 * 
	 * 
	 * 
	 */
	
	public boolean assignBlock(TetrisBlock block, Coordinate startCoord) {
		boolean placeFound = false;
		
		// startCoord defines from which coordinate to start looking for a free position for the first part of the block
		int nextX = 0;
		int nextY = 0;
		//Coordinate startCoord = null;
		Coordinate firstPartCoordinate = null;
		
		boolean coordAvailable = false;
		while (!placeFound && coordsExist(nextX, nextY)) {
			startCoord = new Coordinate(nextX, nextY, null);
			firstPartCoordinate = findNextFreeCoord(startCoord, block.getCoords().get(0));
			if (firstPartCoordinate != null) {
				startCoord.setX(firstPartCoordinate.getX());
				startCoord.setY(firstPartCoordinate.getY());
			}
			if (firstPartCoordinate != null) {
				// free coordinate found for first part of the block
				// let's next check if array has free coordinates for other parts of the block
				//coordAvailable = false;
				for (int i=1; i<block.getCoords().size(); i++) {
					coordAvailable = false;
					Coordinate coords = block.getCoords().get(i);
					
					int xCoord = firstPartCoordinate.getX() + coords.getX();
					int yCoord = firstPartCoordinate.getY() + coords.getY();
					
					if (coordsExist(xCoord, yCoord)) {			
						if (getArrayOfCoords().get(xCoord).get(yCoord).getBlockId() == null) {
							// free coord found for next part of the block
							coordAvailable = true;
							if (i==block.getCoords().size()-1) {
								// we're handling last part of block so a free place for block is found
								placeFound = true;
							}
						}
					}
					if (!coordAvailable) {
						// This coord for this part of block wasn't free
						nextX++;
						if (nextX==getWidth()) {
							nextX = 0;
							nextY++;
							if (nextY == getHeight()) {
								// No free coordinate was found
								break;
							}
						}
						break;
					}
				}
			}
		}
		
		if (placeFound) {
			// Let's make the array coordinates reserved for this block
			for (Coordinate coord : block.getCoords()) {
				Coordinate coord2 = getArrayOfCoords().get(firstPartCoordinate.getX() + coord.getX()).get(firstPartCoordinate.getY() + coord.getY());
				getArrayOfCoords().get(firstPartCoordinate.getX() + coord.getX()).get(firstPartCoordinate.getY() + coord.getY()).setBlockId(block.getBlockId());
			}
		}
		return placeFound;
	}
	
	private boolean coordsExist(int x, int y) {
		boolean exists = false;
		
		if (x >= 0 && x < getArrayOfCoords().size() && y >=0 && y < getArrayOfCoords().get(x).size()) {
			exists = true;
		}

		return exists;
	}
	
	
	/*
	
	public boolean assignBlock(TetrisBlock block) {
		boolean placeFound = false;
		
		//boolean placeFound = false;
		
		//while(!placeFound) {
			
			// startCoord defines from which coordinate to start looking for a free position for blockPart
			int nextX = 0;
			int nextY = 0;
			Coordinate startCoord = null;
			
			for (Coordinate coord : block.getCoords()) {
				
				Coordinate targetCoordinate = null;
				
				while (targetCoordinate == null && nextX<getWidth() && nextY<getHeight()) {
					startCoord = new Coordinate(nextX, nextY, null);
					targetCoordinate = findNextFree(startCoord, coord);
					if (targetCoordinate == null) {
						nextX++;
						if (nextX==getWidth()) {
							nextX = 0;
							nextY++;
							if (nextY == getHeight()) {
								// No free coordinate was found
								break;
							}
						}
					} else {
						// free coordinate found for this part of the block
					}
				}
				
				
			}
			
			
		//}
		
		return placeFound;
	}
	
	/*
	 * ota palikka käsittelyyn

niin kauan kuin käsittelemättömiä taulukon koordinaatteja löytyy
	niin kauan kuin seuraavia osia löytyy
		etsi palikan seuraavalle osalle vapaa paikka
			kun vapaa paikka on löytynyt, etsi seuraavalle osalle vapaa paikka
	loppu-niin: loppuu kun palikan seuraavia osia ei enää löydy
	jos löytyi koko palikalle vapaa paikka, voidaan lopettaa etsiminen. muuten siirrytään etsimään
		vapaata paikkaa seuraavasta koordinaatista
loppu-niin
	 */
	
	
	
	/**
	 * Method searches for next free coordinate in array and returns that
	 * coordinate to the caller. If no free coordinate is found, null is
	 * returned
	 * 
	 * @param coord
	 * @return
	 */
	public Coordinate findNextFreeCoord(Coordinate startCoord, Coordinate coord) {
		Coordinate foundCoord = null;
		
		int x = startCoord.getX();
		int y = startCoord.getY();
		
		while (x<getWidth() && y<getHeight() && foundCoord == null) {
			if (getArrayOfCoords().get(x).get(y).getBlockId() == null) {
				//foundCoord = new Coordinate(x, y, RESERVED);
				foundCoord = new Coordinate(x, y, null);
			} else {
				x++;
				if (x==getWidth() && y<getHeight()) {
					// End of line
					x = 0;
					y++;
				}
			}
		}
		
		
		/*
		for (int x = 0; x<getWidth(); x++) {
			for (int y=0; y<getHeight(); y++) {
				if (getArrayOfCoords().get(x).get(y).getBlockId() == null) {
					// Free place for coord found
					foundCoord = getArrayOfCoords().get(x).get(y);
				}
				if (foundCoord != null) {
					break;
				}
			}
			if (foundCoord != null) {
				break;
			}
		}*/
		return foundCoord;
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

	public List<List<Coordinate>> getArrayOfCoords() {
		return arrayOfCoords;
	}

	public void setArrayOfCoords(List<List<Coordinate>> arrayOfCoords) {
		this.arrayOfCoords = arrayOfCoords;
	}
	
}
