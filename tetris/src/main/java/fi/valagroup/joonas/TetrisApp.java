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
public class TetrisApp extends TetrisUtil {
	
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
				array.findCoordinatesForBlocks(blocks);
			} catch (CoordOutOfBoundsException coobe) {
				out("blocks didn't fit the array!");
			}
		}
		array.printFinalArray();
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

}
