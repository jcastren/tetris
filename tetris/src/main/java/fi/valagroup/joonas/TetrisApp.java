package fi.valagroup.joonas;

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
			initTetrisObjects(lines);
			try {
				findCoordinatesForBlocks(array, blocks);
			} catch (CoordOutOfBoundsException coobe) {
				out("blocks didn't fit the array!");
			}
		}
		printFinalArray(array);
	}
	
	/**
	 * Method searches a location for each block 
	 * @param array
	 * @param blocks
	 * @throws CoordOutOfBoundsException
	 */
	private static void findCoordinatesForBlocks(TetrisArray array, List<TetrisBlock> blocks) throws CoordOutOfBoundsException {
		// Search a place for each block in the array
		Coordinate startCoordinate = new Coordinate(0, 0, null);

		int blockIndex = 0;

		boolean nonFittingBlockFound = false;

		while (blockIndex < blocks.size()) {
			try {
				if (blockIndex == 0) {
					fitBlockToCoordinate(array, blocks.get(blockIndex), startCoordinate);
				} else {
					fitBlockToArray(array, blocks.get(blockIndex));
				}
			} catch (BlockDoesntFitException bdfae) {
				nonFittingBlockFound = true;
				releaseArrayReservations(array);
			}
			if (nonFittingBlockFound) {
				blockIndex = 0;
				try {
					startCoordinate.increaseCoords(array.getWidth() - 1, array.getHeight() - 1);
					nonFittingBlockFound = false;
				} catch (CoordOutOfBoundsException coobe) {
					out(coobe.toString());
					throw coobe;
				}
			} else {
				blockIndex++;
			}
		}
	}
	
	private static void printFinalArray(TetrisArray array) {
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
			for (int x=0; x<=array.getWidth()-1; x++) {
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
	 * @throws BlockDoesntFitException If block doesn't fit to this coordinate
	 */
	private static void fitBlockToCoordinate(TetrisArray array, TetrisBlock block, Coordinate coord) throws BlockDoesntFitException {
		int xCoord = 0;
		int yCoord = 0;
		
		for (Coordinate blockCoord : block.getCoords()) {
			xCoord = blockCoord.getX() + coord.getX();
			yCoord = blockCoord.getY() + coord.getY();
			if (!array.coordsExist(xCoord, yCoord) || array.getCoord(xCoord, yCoord).getBlockId() != null  ) {
				throw new BlockDoesntFitException(String.format("block with id %s doesn't fit array in coord %s", block.getBlockId(), coord.toString()));
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
	 * @throws BlockDoesntFitException
	 */
	private static void fitBlockToArray(TetrisArray array, TetrisBlock block) throws BlockDoesntFitException {
		boolean coordinateFound = false;
		for (int x=0; x<array.getWidth(); x++) {
			for (int y=0; y<array.getHeight()-1; y++) {
				try {
					fitBlockToCoordinate(array, block, array.getCoord(x, y));
					coordinateFound = true;
					break;
				} catch (BlockDoesntFitException bdfae) {
					// ignore exception as we move to next coordinate and check whether block fits there
				}
			}
			if (coordinateFound) {
				break;
			}
		}
		if (!coordinateFound) {
			throw new BlockDoesntFitException(String.format("block with id %s doesn't fit array", block.getBlockId()));
		}
	}
	
	/**
	 * Method inits the values for objects
	 * @param lines
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
	
	/**
	 * Method reads the file from args and returns the file contents as a list of strings
	 * @param args
	 * @return
	 */
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
