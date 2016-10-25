package fi.valagroup.joonas;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 *
 */
public class TetrisApp {
	
	private static TetrisArray array;
	private static final int TETRIS_ARRAY_WIDTH = 4;
	private static final int TETRIS_ARRAY_HEIGHT = 4;
	
	private static List<TetrisBlock> blocks;
	
	public static void main(String[] args) {
		if (args == null) {
			System.out.println("Give file name as argument");
		} else {
			List<String> lines = readFile(args);
			/*
			 * A:0,0;1,0;1,1;2,1 
			 * B:0,0;0,1;0,2;1,2 
			 * C:0,0;1,0;2,0;1,1
			 * D:0,0;1,0;1,1;1,-1
			 */
			
			initTetrisObjects(lines);
			
			// Search a place for each block in the array
			boolean allBlocksFit = false;
			int nextX = 0;
			int nextY = 0;
			Coordinate startCoordinate;
			
			/**
			Käy läpi palikat yksi kerrallaan
				- Aloita seuraavasta käsittelemättömästä koordinaatista etsiminen, mahtuuko palikka siihen
				- Jos mahtuu, siirry seuraavan palikan käsittelyyn
					- tälle etsitään ensimmäisestä koordinaatista lähtien vapaata paikkaa
					- Jos vapaa paikka löytyy, siirry käsittelemään seuraavaa palikkaa
					- Jollei, siirry etsimään ensimmäiselle palikalle vapaata paikkaa seuraavasta koordinaatista
				- Muuten, siirry etsimään vapaata paikkaa seuraavasta käsittelemättömästä koordinaatista
			*/
			
			int blockIndex = 0;
			
			//boolean allBlocksHandled = false;
			boolean nonFittingBlockFound = false;

			//while (!allBlocksHandled && !nonFittingBlockFound) {
			while (blockIndex < blocks.size()) {
				startCoordinate = new Coordinate(nextX, nextY, null);
				try {
					if (blockIndex == 0) {
						fitBlockToCoordinate(array, blocks.get(blockIndex), startCoordinate);	
					} else {
						fitBlockToArray(array, blocks.get(blockIndex));
					}
				} catch (BlockDoesntFitArrayException bdfae) {
					nonFittingBlockFound = true;
					releaseArrayReservations(array);
				}
				if (nonFittingBlockFound) {
					blockIndex = 0;
					startCoordinate.increaseCoords(array.getWidth()-1, array.getHeight()-1);
				} else {
					blockIndex++;
				}
				//if (!non)
			}
			
			/*
			
			while (!allBlocksFit && !wholeArrayLooped(array, nextX, nextY)) {
				out("Trying to fit blocks to coord: " + startCoordinate.toString());
				
				
				
				for (TetrisBlock block : blocks) {
					out("Investigating block with blockId: " + block.getBlockId());
	
					if (!assignBlock(block, startCoordinate)) {
						out("block doesn't fit the array");
						break;
					} else {
						out("block fits the array!");
						block.setFitsTheArray(true);
					}
				}
				
				allBlocksFit = true;
				for (TetrisBlock block : blocks) {
					if (!block.isFitsTheArray()) {
						allBlocksFit = false;
						releaseArrayReservations(array);
					}
				}
				//increaseCoords(array, nextX, nextY);
				
				nextX++;
				if (nextX >= array.getWidth()) {
					nextX = 0;
					nextY++;
				}		
				
				startCoordinate.setX(nextX);
				startCoordinate.setY(nextY);
				
			}
			
			for (List<Coordinate> l : array.getCoords()) {
				for (Coordinate c : l) {
					out(c.toString());
				}
			}*/
		}

		String header1 = "";
		String header2 = "";
		
		for (int i=0; i<array.getWidth(); i++) {
			header1 += Integer.valueOf(i).toString() + " ";
			header2 += "--";
		}
		
		out(header1);
		out(header2);
		
		for (int y=array.getHeight()-1; y>=0; y--) {
			String lineOfArray = "";
			for (int x=array.getWidth()-1; x>=0; x--) {
				String blockId = null;
				if (array.getCoord(x, y).getBlockId() == null) {
					blockId = "  ";
				} else {
					blockId = array.getCoord(x, y).getBlockId() + " ";
				}
				lineOfArray += blockId;
			}
			out(lineOfArray);
		}
	}
		
	/**
	 * Method checks whether block fits to this coordinate of the array
	 * @param array
	 * @param block
	 * @param coord
	 * @throws BlockDoesntFitArrayException If block doesn't fit to this coordinate
	 */
	private static void fitBlockToCoordinate(TetrisArray array, TetrisBlock block, Coordinate coord) throws BlockDoesntFitArrayException {
		int xCoord = 0;
		int yCoord = 0;
		
		for (Coordinate blockCoord : block.getCoords()) {
			xCoord = blockCoord.getX() + coord.getX();
			yCoord = blockCoord.getY() + coord.getY();
			if (!array.coordsExist(xCoord, yCoord) || array.getCoord(xCoord, yCoord).getBlockId() != null  ) {
				throw new BlockDoesntFitArrayException(String.format("block with id %s doesn't fit array in coord %s", block.getBlockId(), coord.toString()));
			}
		}
		
		// Mark the coordinates of the array to which block fits reserved for this block
		for (Coordinate blockCoord : block.getCoords()) {
			xCoord = blockCoord.getX() + coord.getX();
			yCoord = blockCoord.getY() + coord.getY();
			array.getCoord(xCoord, yCoord).setBlockId(block.getBlockId());
		}
	}
	
	/**
	 * Method checks whether block fits to any coordinate of the array
	 * @param array
	 * @param block
	 * @throws BlockDoesntFitArrayException
	 */
	private static void fitBlockToArray(TetrisArray array, TetrisBlock block) {
		boolean coordinateFound = false;
		for (int x=0; x<array.getWidth(); x++) {
			for (int y=0; y<array.getHeight()-1; y++) {
				try {
					fitBlockToCoordinate(array, block, array.getCoord(x, y));
					coordinateFound = true;
					break;
				} catch (BlockDoesntFitArrayException bdfae) {
					// ignore exception as we move to next coordinate and check whether block fits there
				}
			}
			if (coordinateFound) {
				break;
			}
		}
		
				/*
		for (Coordinate blockCoord : block.getCoords()) {
			//for ()
			int xCoord = blockCoord.getX() + coord.getX();
			int yCoord = blockCoord.getY() + coord.getY();
			if (!array.coordsExist(xCoord, yCoord) || array.getCoord(xCoord, yCoord).getBlockId() != null  ) {
				throw new BlockDoesntFitArrayException(String.format("Block with id: %s doesn't fit to coordinate %s", block.getBlockId(), coord.toString()));
			}
		}
			*/
		//}
	}
	
	/*
	private static void increaseCoords(TetrisArray array, Integer x, Integer y) {
		
	}
	*/
	
	/*
	public static void testCoords() {
		TetrisArray array = new TetrisArray(3, 3);
		Integer x = new Integer(0);
		Integer y = new Integer(0);
		increaseCoords(array, x, y);
	}
	*/
	
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
	
	/*
	public static boolean assignBlock(TetrisBlock block, Coordinate startCoord) {
		boolean placeFound = false;
		
		// startCoord defines from which coordinate to start looking for a free position for the first part of the block
		int nextX = 0;
		int nextY = 0;
		//Coordinate startCoord = null;
		Coordinate firstPartCoordinate = null;
		
		boolean coordAvailable = false;
		while (!placeFound && coordsExist(array, nextX, nextY)) {
			startCoord = new Coordinate(nextX, nextY, null);
			firstPartCoordinate = findNextFreeCoord(array, startCoord, block.getCoords().get(0));
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
					
					if (coordsExist(array, xCoord, yCoord)) {			
						if (array.getCoords().get(xCoord).get(yCoord).getBlockId() == null) {
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
						if (nextX==array.getWidth()) {
							nextX = 0;
							nextY++;
							if (nextY == array.getHeight()) {
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
				Coordinate coord2 = array.getCoords().get(firstPartCoordinate.getX() + coord.getX()).get(firstPartCoordinate.getY() + coord.getY());
				array.getCoords().get(firstPartCoordinate.getX() + coord.getX()).get(firstPartCoordinate.getY() + coord.getY()).setBlockId(block.getBlockId());
			}
		}
		return placeFound;
	}
	*/
	
	private static void initTetrisObjects(List<String> lines) {
		array = new TetrisArray(TETRIS_ARRAY_WIDTH, TETRIS_ARRAY_HEIGHT);
		blocks = new ArrayList<TetrisBlock>();
		
		// Read each block from the file
		int lineNumber = 1;
		for (String line : lines) {
			System.out.println(String.format("line %s contains string: %s", Integer.toString(lineNumber++), line));

			String blockId = line.substring(0, 1);
			TetrisBlock block = new TetrisBlock(blockId);

			Pattern pattern = Pattern.compile("((-)*\\d+),((-)*\\d+)");
			Matcher matcher = pattern.matcher(line);

			while (matcher.find()) {
				int x = Integer.parseInt(matcher.group(1));
				int y = Integer.parseInt(matcher.group(3));
				Coordinate coord = new Coordinate(x, y, blockId);

				System.out.println(coord.toString());

				block.addCoordinate(new Coordinate(x, y, blockId));
			}
			
			blocks.add(block);
		}
	}
	
	private static List<String> readFile(String[] args) {
		List<String> lines = new ArrayList<String>();
		String fileName = args[0];
		System.out.println("fileName read: " + fileName);

		try {
			lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println(String.format("Reading the file with fileName <%s> failed, exception: %s", fileName,
					e.toString()));
			e.printStackTrace();
		}
		return lines;
	}
	
	private static boolean wholeArrayLooped(TetrisArray array, int nextX, int nextY) {
		boolean retVal = false;
		
		if (nextX >= array.getWidth() || nextY >= array.getHeight()) {
			retVal = true;
		}
		
		return retVal;
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
	public static Coordinate findNextFreeCoord(TetrisArray array, Coordinate startCoord, Coordinate coord) {
		Coordinate foundCoord = null;
		
		int x = startCoord.getX();
		int y = startCoord.getY();
		
		while (x<array.getWidth() && y<array.getHeight() && foundCoord == null) {
			if (array.getCoords().get(x).get(y).getBlockId() == null) {
				//foundCoord = new Coordinate(x, y, RESERVED);
				foundCoord = new Coordinate(x, y, null);
			} else {
				x++;
				if (x==array.getWidth() && y<array.getHeight()) {
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
	
	private static void releaseArrayReservations(TetrisArray array) {
		for (List<Coordinate> l : array.getCoords()) {
			for (Coordinate c : l) {
				c.setBlockId(null);
			}
		}
	}

	private static void out(String str) {
		System.out.println(str);
	}
	
}
