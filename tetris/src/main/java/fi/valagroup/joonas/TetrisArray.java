package fi.valagroup.joonas;

import java.util.ArrayList;
import java.util.List;

/** Class implements the actual Tetris array to which Tetris blocks are put */
public class TetrisArray extends TetrisUtil {

	private int width;

	private int height;

	private List<List<Coordinate>> coords;

	List<Coordinate> finalBlockStartCoordinates = new ArrayList<Coordinate>();

	public TetrisArray(int width, int height) {
		this.width = width;
		this.height = height;

		coords = new ArrayList<List<Coordinate>>();

		// Initialize array with coordinates
		for (int x = 0; x < width; x++) {
			List<Coordinate> yCoordinates = new ArrayList<Coordinate>();

			for (int y = 0; y < height; y++) {
				yCoordinates.add(new Coordinate(x, y, null));
			}
			coords.add(yCoordinates);
		}
	}

	/**
	 * Method searches a location for each block
	 * 
	 * @param array
	 * @param blocks
	 * @throws CoordOutOfBoundsException
	 */
	public void findCoordinatesForBlocks(List<TetrisBlock> blocks) throws CoordOutOfBoundsException {
		Coordinate startCoordinate = new Coordinate(0, 0, null);

		int blockIndex = 0;

		boolean nonFittingBlockFound = false;

		while (blockIndex < blocks.size()) {
			try {
				if (blockIndex == 0) {
					fitBlockToCoordinate(blocks.get(blockIndex), startCoordinate);
				} else {
					fitBlockToArray(blocks.get(blockIndex));
				}
			} catch (BlockDoesntFitException bdfae) {
				nonFittingBlockFound = true;
				releaseArrayReservations();
			}
			if (nonFittingBlockFound) {
				blockIndex = 0;
				try {
					startCoordinate.increaseCoords(getWidth() - 1, getHeight() - 1);
					nonFittingBlockFound = false;
					finalBlockStartCoordinates.clear();
				} catch (CoordOutOfBoundsException coobe) {
					out(coobe.toString());
					throw coobe;
				}
			} else {
				blockIndex++;
			}
		}
	}

	/**
	 * Method prints out the finalArray
	 * 
	 * @param array
	 */
	public void printFinalArray() {
		String header1 = "";
		String header2 = "";

		for (int i = 0; i < getWidth(); i++) {
			header1 += Integer.valueOf(i).toString() + " ";
			header2 += "--";
		}

		out(header1);
		out(header2);

		for (int y = getHeight() - 1; y >= 0; y--) {
			String lineOfArray = "";
			for (int x = 0; x <= getWidth() - 1; x++) {
				String blockId = null;
				if (getCoord(x, y).getBlockId() == null) {
					blockId = "  ";
				} else {
					blockId = getCoord(x, y).getBlockId() + " ";
				}
				lineOfArray += blockId;
			}
			out(lineOfArray);
		}

		out("Starting coordinates of the blocks:");
		for (Coordinate coord : finalBlockStartCoordinates) {
			out(coord.toString());
		}
	}

	private Coordinate getCoord(int x, int y) {
		Coordinate coord = null;

		if (coordsExist(x, y)) {
			coord = getCoords().get(x).get(y);
		}

		return coord;
	}

	private boolean coordsExist(int x, int y) {
		boolean exists = false;

		if (x >= 0 && x < getCoords().size() && y >= 0 && y < getCoords().get(x).size()) {
			exists = true;
		}

		return exists;
	}

	/**
	 * Method cleans the reservations from the array (sets every blockId of
	 * array coordinates to null)
	 * 
	 * @param array
	 */
	private void releaseArrayReservations() {
		for (List<Coordinate> l : getCoords()) {
			for (Coordinate c : l) {
				c.setBlockId(null);
			}
		}
	}

	/**
	 * Method checks whether block fits to this coordinate of the array
	 * 
	 * @param array
	 * @param block
	 * @param coord
	 * @throws BlockDoesntFitException
	 *             If block doesn't fit to this coordinate
	 */
	private void fitBlockToCoordinate(TetrisBlock block, Coordinate coord) throws BlockDoesntFitException {
		int finalXCoord = 0;
		int finalYCoord = 0;

		for (Coordinate blockCoord : block.getCoords()) {
			finalXCoord = blockCoord.getX() + coord.getX();
			finalYCoord = blockCoord.getY() + coord.getY();
			if (!coordsExist(finalXCoord, finalYCoord) || getCoord(finalXCoord, finalYCoord).getBlockId() != null) {
				throw new BlockDoesntFitException(String.format("block with id %s doesn't fit array in coord %s",
						block.getBlockId(), coord.toString()));
			}
		}

		// Mark the coordinates of the array to which block fits reserved for
		// this block
		for (Coordinate blockCoord : block.getCoords()) {
			finalXCoord = blockCoord.getX() + coord.getX();
			finalYCoord = blockCoord.getY() + coord.getY();
			getCoord(finalXCoord, finalYCoord).setBlockId(block.getBlockId());
		}
		
		finalXCoord = block.getCoords().get(0).getX() + coord.getX();
		finalYCoord = block.getCoords().get(0).getY() + coord.getY();
		finalBlockStartCoordinates.add(new Coordinate(finalXCoord, finalYCoord, block.getBlockId()));
	}

	/**
	 * Method checks whether block fits to any coordinate of the array
	 * 
	 * @param array
	 * @param block
	 * @throws BlockDoesntFitException
	 */
	private void fitBlockToArray(TetrisBlock block) throws BlockDoesntFitException {
		boolean coordinateFound = false;
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight() - 1; y++) {
				try {
					fitBlockToCoordinate(block, getCoord(x, y));
					coordinateFound = true;
					break;
				} catch (BlockDoesntFitException bdfae) {
					// ignore exception as we move to next coordinate and check
					// whether block fits there
				}
			}
			if (coordinateFound) {
				break;
			}
		}
		if (!coordinateFound) {
			// block doesn't fit the array, remove the entry from the final list
			// to be returned by main
			List<Coordinate> newList = new ArrayList<Coordinate>();
			for (Coordinate coord : finalBlockStartCoordinates) {
				if (!coord.getBlockId().equals(block.getBlockId())) {
					newList.add(coord);
				}
			}
			finalBlockStartCoordinates = newList;
			throw new BlockDoesntFitException(String.format("block with id %s doesn't fit array", block.getBlockId()));
		}
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
