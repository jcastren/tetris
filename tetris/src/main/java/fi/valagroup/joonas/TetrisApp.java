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
	private static final int TETRIS_ARRAY_WIDTH = 6;
	private static final int TETRIS_ARRAY_HEIGHT = 6;
	
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
			Coordinate startCoordinate = new Coordinate(nextX, nextY, null);
			
			/**
			Käy läpi palikat yksi kerrallaan
				- Aloita seuraavasta käsittelemättömästä koordinaatista etsiminen, mahtuuko palikka siihen
				- Jos mahtuu, siirry seuraavan palikan käsittelyyn
					- tälle etsitään ensimmäisestä koordinaatista lähtien vapaata paikkaa
					- Jos vapaa paikka löytyy, siirry käsittelemään seuraavaa palikkaa
					- Jollei, siirry etsimään ensimmäiselle palikalle vapaata paikkaa seuraavasta koordinaatista
				- Muuten, siirry etsimään vapaata paikkaa seuraavasta käsittelemättömästä koordinaatista
			*/
			
			while (!allBlocksFit && !wholeArrayLooped(array, nextX, nextY)) {
				out("Trying to fit blocks to coord: " + startCoordinate.toString());
				
				
				for (TetrisBlock block : blocks) {
					out("Investigating block with blockId: " + block.getBlockId());
	
					if (!array.assignBlock(block, startCoordinate)) {
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
				nextX++;
				if (nextX >= array.getWidth()) {
					nextX = 0;
					nextY++;
				}
				startCoordinate.setX(nextX);
				startCoordinate.setY(nextY);
				
			}
			
			for (List<Coordinate> l : array.getArrayOfCoords()) {
				for (Coordinate c : l) {
					out(c.toString());
				}
			}
		}
	}
	
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
	
	private static void releaseArrayReservations(TetrisArray array) {
		for (List<Coordinate> l : array.getArrayOfCoords()) {
			for (Coordinate c : l) {
				c.setBlockId(null);
			}
		}
	}

	private static void out(String str) {
		System.out.println(str);
	}

}
